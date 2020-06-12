package com.may.app.feed;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.may.app.event.FollowerPushedEvent;
import com.may.app.feed.entity.Feed;
import com.may.app.feed.exception.NoFeedException;
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

	@Transactional
	public Feed add
	(
		String content, 
		Long memberId, 
		List<String> imgs, 
		List<String> comments, 
		List<String> tags, 
		List<Long> itemIds
	) {
		Member member = memberRepository.findById(memberId).orElseThrow(()-> new NoMemberException());
		
		List<Tag> tagsResult = saveTag(tags);
		List<Item> itemsResult = itemRepository.findAllById(itemIds).stream().collect(Collectors.toList());
		
		Feed feed = Feed.createFeed(content, member, imgs, comments, tagsResult, itemsResult);
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
	public Feed detail(Long id) {
		Feed feed = feedRepository.findById(id).orElseThrow(()-> new NoFeedException());
		
		return feed;
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
	
	@Transactional
	public Long delete(Long id, Long memberId) {
		Feed feed = feedRepository.findFetchMemberById(id).orElseThrow(()-> new NoFeedException());

		feed.isOwner(memberId);
		
		feedRepository.delete(feed);
		
		return feed.getId();
	}
}
