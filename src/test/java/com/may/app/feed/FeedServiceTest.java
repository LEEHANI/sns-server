package com.may.app.feed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.may.app.common.CreateEntity;
import com.may.app.feed.entity.Comment;
import com.may.app.feed.entity.Feed;
import com.may.app.feed.entity.Resource;
import com.may.app.feed.exception.FeedOwnerMismatchException;
import com.may.app.feed.exception.NoFeedException;
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
	
	Member member1 = CreateEntity.createMember(1L);
	Member member2 = CreateEntity.createMember(2L);
	
	List<Item> items = CreateEntity.createItems(2, member1, true);
	List<Long> stringItems = items.stream().map(o->o.getId().longValue()).collect(Collectors.toList());
	
	List<Tag> tags = CreateEntity.createTags(0,2, true);
	List<Tag> saveNewTags = CreateEntity.createTags(2,4, true); 
	List<Tag> totalTags = tags;
//	totalTags.addAll(saveNewTags);
	List<String> StringTags = totalTags.stream().map(o->o.getTitle().toString()).collect(Collectors.toList());
	
	List<Resource> resources = CreateEntity.createResources(4, true);
	List<Comment> comments = CreateEntity.createComments(3, true);
	
	Feed feed1 = CreateEntity.createFeed(1L, member1, resources, comments, items, tags);
	Feed feed2 = CreateEntity.createFeed(2L, member1, resources, comments, items, tags);

	final String content = "피드 수정합니다~";
	
	@BeforeEach
	public void setUp() throws Exception{
		given(memberRepository.findById(member1.getId())).willReturn(Optional.of(member1));
		given(itemRepository.findAllById(stringItems)).willReturn(items);
		given(tagRepository.findByTitleIn(StringTags)).willReturn(tags);
		given(tagRepository.saveAll(saveNewTags)).willReturn(saveNewTags);
		given(feedRepository.findById(feed1.getId())).willReturn(Optional.of(feed1));
		given(feedRepository.findFetchMemberById(feed1.getId())).willReturn(Optional.of(feed1));
	}
	
	/**
	 * feedService.add() 성공
	 * tags중 [0,1]는 이미 저장되어있는 태그, [2,3]은 새로 저장할 태그
	 */
	@Test
	public void 피드_추가_성공() throws Exception {
		//given
		List<String> comment = comments.stream().map(o->o.getContent().toString()).collect(Collectors.toList());
		List<String> imgs = resources.stream().map(o->o.getPath().toString()).collect(Collectors.toList());
		
		//when
		feedService.add(content, member1.getId(), imgs, comment, StringTags, stringItems);
		//리플렉션 .. reflection
		ArgumentCaptor<Feed> feedCaptor = ArgumentCaptor.forClass(Feed.class);
		verify(feedRepository).save(feedCaptor.capture());
		Feed result = feedCaptor.getValue();
		
		//then
		assertNotNull(result);
		assertEquals(result.getTags().size(), 2);
		assertEquals(result.getComments().size(), comments.size());
		assertEquals(result.getItems().size(), items.size());
		assertEquals(result.getItems().get(0).getItem().getTitle(), items.get(0).getTitle());
	}
	
	/**
	 * save() Test 실패
	 * 저장되어 있지 않은 회원을 대상으로 피드 저장할 때는 NoMemberException이 발생한다.
	 * @throws Exception
	 */
	@Test
	public void 피드_추가_실패() throws Exception {
		//when & then
		assertThrows(NoMemberException.class, ()-> feedService.add("피드내용", member2.getId(), null, null, StringTags, stringItems));
	}
	
	/**
	 * detail find() Test 성공
	 * 피드를 상세 조회한다. 
	 */
	@Test
	public void 피드_상세_조회_성공() throws Exception {
		//when
		Feed result = feedService.detail(feed1.getId());
		
		//then
		assertNotNull(result);
		assertEquals(result.getContent(), feed1.getContent());
		assertEquals(result.getItems().size(), feed1.getItems().size());
		assertEquals(result.getComments().get(1).getContent(), feed1.getComments().get(1).getContent());
	}

	/**
	 * detail find() Test 실패
	 * 저장되어 있지 않은 피드를 상세 조회할 때는 NoFeedException이 발생한다.
	 * @throws Exception
	 */
	@Test
	public void 피드_상세_조회_실패() throws Exception {
		//when & then
		assertThrows(NoFeedException.class, ()-> feedService.detail(10L));
	}
	
	/**
	 * list find() 성공
	 * @throws Exception
	 */
	@Test
	public void 피드_페이지_리스트_조회_성공() throws Exception {
		//given
		Feed feed2 = CreateEntity.createFeed(2L, member2, resources, comments, items, tags);
		Feed feed3 = CreateEntity.createFeed(3L, member1, resources, comments, items, tags);
		List<Feed> feeds = Lists.newArrayList(feed1, feed2, feed3);
		PageRequest pageable = PageRequest.of(0, 2);
		Page<Feed> pageFeeds = new PageImpl<>(feeds, pageable, feeds.size());
		
		given(feedRepository.findEntityGraphBy(pageable)).willReturn(pageFeeds);
		
		//when
		Page<Feed> result = feedService.list(pageable);
		
		//then
		assertNotNull(result);
		assertEquals(result.getTotalElements(), feeds.size());
		assertEquals(result.getTotalElements(), pageFeeds.getTotalElements());
		assertEquals(result.getContent().get(0).getId(), feeds.get(0).getId());
	}
	
	/**
	 * edit() Test
	 * 피드를 수정한다. 
	 * @throws Exception
	 */
	@Test
	public void 피드_수정_성공() throws Exception {
		//given
		List<String> imgs = Lists.newArrayList(resources.get(0).getPath());
		
		List<Tag> editTags = Lists.newArrayList(tags.get(0), CreateEntity.createTag(101L));
		List<String> tags = editTags.stream().map(o->o.getTitle()).collect(Collectors.toList());
		given(tagRepository.findByTitleIn(tags)).willReturn(Lists.newArrayList(editTags.get(0)));
		
		List<Item> editItems = Lists.newArrayList(items.get(0), CreateEntity.createItem(100L, member1));
		List<Long> itemIds = editItems.stream().map(o->o.getId()).collect(Collectors.toList());
		given(itemRepository.findAllById(itemIds)).willReturn(editItems);
		
		//when
		Feed result = feedService.edit(feed1.getId(), content, member1.getId(), imgs, tags, itemIds);
		
		//then
		assertNotNull(result);
		assertEquals(result.getContent(), content);
		assertEquals(result.getResources().size(), imgs.size());
		assertEquals(result.getResources().get(0).getPath(), imgs.get(0));
		assertEquals(result.getTags().size(), tags.size());
		assertEquals(result.getTags().get(1).getTag().getTitle(), editTags.get(1).getTitle());
		assertEquals(result.getItems().size(), editItems.size());
		assertEquals(result.getItems().get(1).getItem().getTitle(), editItems.get(1).getTitle());
	}
	
	/**
	 * edit() Test
	 * img, tag, item을 전부 없애기
	 * @throws Exception
	 */
	@Test
	public void 피드_수정_전부_제로_리스트_성공() throws Exception {
		//given
		List<String> imgs = Lists.newArrayList();
		
		List<Tag> editTags = Lists.newArrayList();
		List<String> tags = editTags.stream().map(o->o.getTitle()).collect(Collectors.toList());
		
		List<Item> editItems = Lists.newArrayList();
		List<Long> itemIds = editItems.stream().map(o->o.getId()).collect(Collectors.toList());
		
		//when
		Feed result = feedService.edit(feed1.getId(), content, member1.getId(), imgs, tags, itemIds);
		
		//then
		assertNotNull(result);
		assertEquals(result.getContent(), content);
		assertEquals(result.getResources().size(), imgs.size());
		assertEquals(result.getTags().size(), tags.size());
		assertEquals(result.getItems().size(), editItems.size());
	}
	
	@Test
	public void 피드_수정_실패() throws Exception {
		//given
		List<String> imgs = Lists.newArrayList(resources.get(0).getPath());
		
		List<Tag> editTags = Lists.newArrayList(tags.get(0), CreateEntity.createTag(101L));
		List<String> tags = editTags.stream().map(o->o.getTitle()).collect(Collectors.toList());
		given(tagRepository.findByTitleIn(tags)).willReturn(Lists.newArrayList(editTags.get(0)));
		
		List<Item> editItems = Lists.newArrayList(items.get(0), CreateEntity.createItem(100L, member1));
		List<Long> itemIds = editItems.stream().map(o->o.getId()).collect(Collectors.toList());
		given(itemRepository.findAllById(itemIds)).willReturn(editItems);
		
		//when & then
		assertThrows(FeedOwnerMismatchException.class, ()-> feedService.edit(feed1.getId(), content, member2.getId(), imgs, tags, itemIds));
	}
	
	/**
	 * delete() Test 성공
	 */
	@Test
	public void 피드_삭제_성공() throws Exception {
		//when
		Long result = feedService.delete(feed1.getId(), member1.getId());
		
		//then
		assertNotNull(result);
		assertEquals(result, feed1.getId());
	}
	
	/**
	 * find() Test 실패
	 * 저장되어 있지 않은 피드를 상세 조회할 때는 NoFeedException이 발생한다.
	 * @throws Exception
	 */
	@Test
	public void 피드_삭제_실패() throws Exception {
		//when & then
		assertThrows(NoFeedException.class, ()-> feedService.delete(feed2.getId(), member1.getId()));
	}
}
