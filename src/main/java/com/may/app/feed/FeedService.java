package com.may.app.feed;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.may.app.event.FollowerPushedEvent;
import com.may.app.feed.dto.FeedDto;
import com.may.app.feed.dto.GoodDto;
import com.may.app.feed.entity.Feed;
import com.may.app.feed.exception.DuplicateFeedGoodFeedException;
import com.may.app.feed.exception.NoFeedException;
import com.may.app.feed.exception.NoFeedGoodException;
import com.may.app.feed.repository.FeedRepository;
import com.may.app.item.entity.Item;
import com.may.app.item.repository.ItemRepository;
import com.may.app.member.entity.Member;
import com.may.app.member.exception.NoMemberException;
import com.may.app.member.repository.MemberRepository;
import com.may.app.tag.entity.Tag;
import com.may.app.tag.repository.TagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedService {
	private final FeedRepository feedRepository;
	private final MemberRepository memberRepository;
	private final ItemRepository itemRepository;
	private final TagRepository tagRepository;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final RedisTemplate<String, Object> redisTemplate;
	
	@CacheEvict(value = "members", key = "#memberId")
	@Transactional
	public Feed add
	(
		String content, 
		Long memberId, 
		List<String> imgs, 
		List<String> tags, 
		List<Long> itemIds
	) {
		Member member = memberRepository.findById(memberId).orElseThrow(()-> new NoMemberException());
		
		List<Tag> tagsResult = saveTag(tags);
		List<Item> itemsResult = itemRepository.findAllById(itemIds).stream().collect(Collectors.toList());
		
		Feed feed = Feed.createFeed(content, member, imgs, tagsResult, itemsResult);
		Feed result = feedRepository.save(feed);
		
		applicationEventPublisher.publishEvent(new FollowerPushedEvent(feed));
		
		return result;
	}
	
	private List<Tag> saveTag(List<String> tags) {
		// 저장된 태그와 저장할 태그 분리
		List<Tag> alreadyTagEntities = tagRepository.findByTitleIn(tags);
		Set<String> alreadyTags = alreadyTagEntities.stream().map(t->t.getTitle().toString()).collect(Collectors.toSet());

		Set<String> newTags = tags.stream().collect(Collectors.toSet());
		newTags.removeAll(alreadyTags);
		
		// 새로운 태그 저장
		Set<Tag> saveTags = newTags.stream().map(Tag::new).collect(Collectors.toSet());
		tagRepository.saveAll(saveTags);
		
		// 병합
		alreadyTagEntities.addAll(saveTags);
		
		return alreadyTagEntities;
	}
	
	@Transactional(readOnly = true)
	public FeedDto.Get detail(Long id, Long requestMemberId) {
		Feed feed = feedRepository.findById(id).orElseThrow(()-> new NoFeedException());
		GoodDto good = goodCheck(id, requestMemberId);
		
		return new FeedDto.Get(feed, good);
	}
	
	public GoodDto goodCheck(Long feedId, Long memberId) {
		Long count = redisTemplate.opsForSet().size(feedId.toString()); // O(1)
		Boolean isGood = memberId==null? false : redisTemplate.opsForSet().isMember(feedId.toString(), memberId);
		
		return new GoodDto(isGood==null? false : isGood, count);
	}
	
	@Transactional(readOnly = true)
	public Page<Feed> list(PageRequest pageable) {
		// feed + XXToOne entity 조회
		Page<Feed> feeds = feedRepository.findEntityGraphBy(pageable);
		
		return feeds;
	}
	
	@Transactional
	public Feed edit
	(
		Long id,
		String content, 
		Long memberId, 
		List<String> imgs, 
		List<String> tags, 
		List<Long> itemIds
	) {
		Feed feed = feedRepository.findFetchMemberById(id).orElseThrow(()-> new NoFeedException());
		
		feed.isOwner(memberId);
		
		List<Tag> tagEntities = saveTag(tags);
		List<Item> itemEntites = itemRepository.findAllById(itemIds);
		
		feed.edit(content, imgs, tagEntities, itemEntites);
		
		return feed;
	}
	
	@CacheEvict(value = "members", key = "#memberId")
	@Transactional
	public Long delete(Long id, Long memberId) {
		Feed feed = feedRepository.findFetchMemberById(id).orElseThrow(()-> new NoFeedException());

		feed.isOwner(memberId);
		feedRepository.delete(feed);
		
		return feed.getId();
	}
	
	@Transactional
	public Long good(Long id, Long memberId) {
		feedRepository.findById(id).orElseThrow(()-> new NoFeedException());
		memberRepository.findById(memberId).orElseThrow(()-> new NoMemberException());
		
		Long result = redisTemplate.opsForSet().add(id.toString(), memberId);
		if(result==0) throw new DuplicateFeedGoodFeedException();
		
		return result;
	}
	
	@Transactional
	public Long unGood(Long id, Long memberId) {
		feedRepository.findById(id).orElseThrow(()-> new NoFeedException());
		memberRepository.findById(memberId).orElseThrow(()-> new NoMemberException());
		
		Long result = redisTemplate.opsForSet().remove(id.toString(), memberId);
		if(result==0) throw new NoFeedGoodException(); 
		
		return result;
	}
}
