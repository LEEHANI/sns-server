package com.may.app.feed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import com.may.app.common.CreateEntity;
import com.may.app.feed.dto.FeedDto;
import com.may.app.feed.dto.FeedDto.Get;
import com.may.app.feed.entity.Feed;
import com.may.app.feed.entity.Resource;
import com.may.app.feed.exception.DuplicateFeedGoodFeedException;
import com.may.app.feed.exception.FeedOwnerMismatchException;
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

@SpringBootTest(classes = FeedService.class)
public class FeedServiceTest {
	@Autowired private FeedService feedService;
	@MockBean private FeedRepository feedRepository;
	@MockBean private MemberRepository memberRepository;
	@MockBean private ItemRepository itemRepository;
	@MockBean private TagRepository tagRepository;
	@MockBean private RedisTemplate<String, Object> redisTemplate;
	@MockBean private SetOperations<String, Object> setOperations;
	
	@BeforeEach
	public void setUp() throws Exception{
		when(redisTemplate.opsForSet()).thenReturn(setOperations);
	}
	
	/**
	 * save() Test 성공
	 * request로 tags[0,1,2,3]를 보낸다. 
	 * tags중 [0,1]는 이미 저장되어있는 태그, [2,3]은 새로 저장해야할 태그라고 가정하면, feed는 4개가 모두 저장되어야 한다.  
	 */
	@Test
	public void 피드_추가_성공() throws Exception {
		//given
		Member member1 = CreateEntity.createMember(1L);
		List<Resource> resources = CreateEntity.createResources(4, true);
		List<Tag> existTags = CreateEntity.createTags(0,2, true);
		List<Tag> newTags = CreateEntity.createTags(2,4, true);
		List<Item> items = CreateEntity.createItems(2, member1, true);
		
		given(memberRepository.findById(Mockito.any())).willReturn(Optional.of(member1));
		given(tagRepository.findByTitleIn(Mockito.any())).willReturn(existTags);
		given(itemRepository.findAllById(Mockito.any())).willReturn(items);
		given(tagRepository.saveAll(Mockito.any())).willReturn(newTags);
		existTags.addAll(newTags);
		
		//when
		feedService.add
		(
			"피드 추가합니다~", 
			member1.getId(), 
			resources.stream().map(o->o.getPath().toString()).collect(Collectors.toList()), 
			existTags.stream().map(o->o.getTitle().toString()).collect(Collectors.toList()), 
			items.stream().map(o->o.getId().longValue()).collect(Collectors.toList())
		);
		
		//리플렉션 .. reflection
		ArgumentCaptor<Feed> feedCaptor = ArgumentCaptor.forClass(Feed.class);
		verify(feedRepository).save(feedCaptor.capture());
		Feed result = feedCaptor.getValue();
		
		//then
		assertNotNull(result);
		assertEquals(result.getTags().size(), 4);
		assertEquals(result.getItems().size(), items.size());
		assertEquals(result.getItems().get(0).getItem().getTitle(), items.get(0).getTitle());
	}
	
	/**
	 * save() Test 실패
	 * 저장되어 있지 않은 회원을 대상으로 피드 저장할 때는 NoMemberException이 발생한다.
	 */
	@Test
	public void 피드_추가_실패() throws Exception {
		//when & then
		assertThrows(NoMemberException.class, () -> feedService.add("피드내용", 2L, null, null, null));
	}
	
	/**
	 * detail find() Test 성공
	 */
	@Test
	public void 피드_상세_조회_성공() throws Exception {
		//given
		Feed feed1 = CreateEntity.createFeed(1L, CreateEntity.createMember(1L), 1,1,1);
		
		given(feedRepository.findById(Mockito.any())).willReturn(Optional.of(feed1));
		given(redisTemplate.opsForSet().size(Mockito.any())).willReturn(2L);
		
		//when
		FeedDto.Get result = feedService.detail(feed1.getId(), null);
		
		//then
		assertNotNull(result);
		assertEquals(result.getContent(), feed1.getContent());
		assertEquals(result.getItems().size(), feed1.getItems().size());
		assertEquals(result.getTags().size(), feed1.getTags().size());
	}
	
	@Test
	public void 피드_상세_조회_좋아요_성공() throws Exception {
		//given
		Feed feed1 = CreateEntity.createFeed(1L, CreateEntity.createMember(1L), 1,1,1);
		
		given(feedRepository.findById(Mockito.any())).willReturn(Optional.of(feed1));
		given(redisTemplate.opsForSet().size(Mockito.any())).willReturn(2L);
		given(redisTemplate.opsForSet().isMember(Mockito.any(), Mockito.any())).willReturn(true);
		
		//when
		FeedDto.Get result = feedService.detail(feed1.getId(), 1L);
		
		//then
		assertNotNull(result);
		assertEquals(result.getIsGood(), true);
		assertEquals(result.getGoodCount(), 2L);
	}

	/**
	 * detail find() Test 실패
	 * 저장되어 있지 않은 피드를 상세 조회할 때는 NoFeedException이 발생한다.
	 */
	@Test
	public void 피드_상세_조회_실패() throws Exception {
		//when & then
		assertThrows(NoFeedException.class, ()-> feedService.detail(10L, null));
	}
	
	/**
	 * list find() 성공
	 */
	@Test
	public void 피드_페이지_리스트_조회_성공() throws Exception {
		//given
		Member member1 = CreateEntity.createMember(1L);
		Page<Feed> pageFeeds = new PageImpl<>
		(
			Lists.newArrayList(CreateEntity.createFeed(1L,member1,0,0,0), CreateEntity.createFeed(2L,member1,0,0,0), CreateEntity.createFeed(2L,member1,0,0,0)), 
			PageRequest.of(0, 2), 
			3
		);
		
		given(feedRepository.findEntityGraphBy(Mockito.any(PageRequest.class))).willReturn(pageFeeds);
		
		//when
		Page<FeedDto.GetList> result = feedService.list(PageRequest.of(0, 2), null);
		
		//then
		assertNotNull(result);
		assertEquals(result.getTotalElements(), pageFeeds.getContent().size());
		assertEquals(result.getTotalElements(), pageFeeds.getTotalElements());
	}
	
	/**
	 * edit() Test 성공 
	 */
	@Test
	public void 피드_수정_성공() throws Exception {
		//given
		Member member1 = CreateEntity.createMember(1L);
		List<Item> items = CreateEntity.createItems(2, member1, true);
		List<Tag> tags = CreateEntity.createTags(0,2, true);
		List<Resource> resources = CreateEntity.createResources(4, true);
		Feed feed1 = CreateEntity.createFeed(1L, member1, resources, items, tags);
		
		given(tagRepository.findByTitleIn(Mockito.any())).willReturn(Lists.newArrayList(tags.get(0)));
		given(itemRepository.findAllById(Mockito.any())).willReturn(Lists.newArrayList(items.get(0)));
		given(tagRepository.saveAll(Mockito.any())).willReturn(CreateEntity.createTags(2,4, true));
		given(feedRepository.findFetchMemberById(Mockito.any())).willReturn(Optional.of(feed1));
		
		//when
		Feed result = feedService.edit
				(
					feed1.getId(), 
					"피드 수정", 
					member1.getId(), 
					Lists.newArrayList(resources.get(0).getPath()), 
					Lists.newArrayList(tags.get(0).getTitle()), 
					Lists.newArrayList(items.get(0).getId()) 
				);
		
		//then
		assertNotNull(result);
		assertEquals(result.getContent(), "피드 수정");
		assertEquals(result.getResources().size(), 1);
		assertEquals(result.getTags().size(), 1);
		assertEquals(result.getItems().size(), 1);
	}
	
	/**
	 * edit() Test
	 * img, tag, item을 전부 없애기
	 */
	@Test
	public void 피드_수정_전부_제로_리스트_성공() throws Exception {
		//given
		Feed feed1 = CreateEntity.createFeed(1L, CreateEntity.createMember(1L), 1,1,1);
		Member member1 = CreateEntity.createMember(1L);
		given(feedRepository.findFetchMemberById(Mockito.any())).willReturn(Optional.of(feed1));
		
		//when
		Feed result = feedService.edit(feed1.getId(), "피드 수정", member1.getId(), Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList());
		
		//then
		assertNotNull(result);
		assertEquals(result.getResources().size(), 0);
		assertEquals(result.getTags().size(), 0);
		assertEquals(result.getItems().size(), 0);
	}
	
	/**
	 * edit() Test  실패
	 * 다른 사람의 피드를 수정하려 시도할 떄 FeedOwnerMismatchException이 발생한다. 
	 */
	@Test
	public void 피드_수정_실패() throws Exception {
		//given
		Feed feed1 = CreateEntity.createFeed(1L, CreateEntity.createMember(1L), 0,0,0);
		
		given(feedRepository.findFetchMemberById(Mockito.any())).willReturn(Optional.of(feed1));
		
		//when & then
		assertThrows(FeedOwnerMismatchException.class, ()-> feedService.edit(feed1.getId(), null, 2L, null, null, null));
	}
	
	/**
	 * delete() Test 성공
	 */
	@Test
	public void 피드_삭제_성공() throws Exception {
		//given
		Feed feed1 = CreateEntity.createFeed(1L, CreateEntity.createMember(1L), 1,1,1);
		Member member1 = CreateEntity.createMember(1L);
		given(feedRepository.findFetchMemberById(Mockito.any())).willReturn(Optional.of(feed1));
		
		//when
		Long result = feedService.delete(feed1.getId(), member1.getId());
		
		//then
		assertNotNull(result);
		assertEquals(result, feed1.getId());
	}
	
	/**
	 * find() Test 실패
	 * 저장되어 있지 않은 피드를 상세 조회할 때는 NoFeedException이 발생한다.
	 */
	@Test
	public void 피드_삭제_실패() throws Exception {
		//when & then
		assertThrows(NoFeedException.class, ()-> feedService.delete(2L, 1L));
	}
	
	/**
	 * save() Test 성공 
	 */
	@Test
	public void 피드_좋아요_성공() throws Exception {
		//given
		Member member1 = CreateEntity.createMember(1L);
		Feed feed1 = CreateEntity.createFeed(1L, CreateEntity.createMember(1L), 1,1,1);
		
		given(feedRepository.findById(Mockito.anyLong())).willReturn(Optional.of(feed1));
		given(memberRepository.findById(Mockito.anyLong())).willReturn(Optional.of(member1));
		given(setOperations.add(Mockito.anyString(), Mockito.anyObject())).willReturn(1L);
		
		//when
		Long result = feedService.good(feed1.getId(), member1.getId());
		
		//then
		assertNotNull(result);
	}
	
	/**
	 * save() Test 실패
	 * 좋아요를 중복해서 시도할 때는 DuplicateFeedGoodFeedException이 발생한다.
	 */
	@Test
	public void 피드_좋아요_실패() throws Exception {
		//given
		Member member1 = CreateEntity.createMember(1L);
		Feed feed1 = CreateEntity.createFeed(1L, CreateEntity.createMember(1L), 1,1,1);
		
		given(feedRepository.findById(Mockito.anyLong())).willReturn(Optional.of(feed1));
		given(memberRepository.findById(Mockito.anyLong())).willReturn(Optional.of(member1));
		given(setOperations.add(Mockito.anyString(), Mockito.anyObject())).willReturn(0L);
		
		//when & then
		assertThrows(DuplicateFeedGoodFeedException.class, ()->feedService.good(feed1.getId(), member1.getId()));
	}
	
	/**
	 * delete() Test 성공
	 */
	@Test
	public void 피드_좋아요_해제_성공() throws Exception {
		//given
		Member member1 = CreateEntity.createMember(1L);
		Feed feed1 = CreateEntity.createFeed(1L, CreateEntity.createMember(1L), 1,1,1);
		
		given(feedRepository.findById(Mockito.anyLong())).willReturn(Optional.of(feed1));
		given(memberRepository.findById(Mockito.anyLong())).willReturn(Optional.of(member1));
		given(setOperations.remove(Mockito.anyString(), Mockito.anyObject())).willReturn(1L);
		
		//when
		Long result = feedService.unGood(feed1.getId(), member1.getId());
		
		//then
		assertNotNull(result);
	}
	
	/**
	 * delete() Test 실패
	 * 좋아요를 누른적 없는데, 좋아요 해제를 시도할 때는 NoFeedGoodException이 발생한다.
	 */
	@Test
	public void 피드_좋아요_해제_실패() throws Exception {
		//given
		Member member1 = CreateEntity.createMember(1L);
		Feed feed1 = CreateEntity.createFeed(1L, CreateEntity.createMember(1L), 1,1,1);
		
		given(feedRepository.findById(Mockito.anyLong())).willReturn(Optional.of(feed1));
		given(memberRepository.findById(Mockito.anyLong())).willReturn(Optional.of(member1));
		
		//when & then
		assertThrows(NoFeedGoodException.class, ()->feedService.unGood(feed1.getId(), member1.getId()));
	}
}
