package com.may.app.feed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.assertj.core.util.Lists;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.may.app.common.CreateEntity;
import com.may.app.feed.entity.Comment;
import com.may.app.feed.entity.Feed;
import com.may.app.feed.entity.Resource;
import com.may.app.feed.repository.FeedRepository;
import com.may.app.item.entity.Item;
import com.may.app.item.repository.ItemRepository;
import com.may.app.member.entity.Member;
import com.may.app.member.repository.MemberRepository;
import com.may.app.tag.entity.Tag;
import com.may.app.tag.repository.TagRepository;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
public class FeedRepositoryTest {
	@Autowired private FeedRepository feedRepository;
	@Autowired private MemberRepository memberRepository;
	@Autowired private ItemRepository itemRepository;
	@Autowired private TagRepository tagRepository;
	@Autowired private EntityManager em;
	
	Member member1 = CreateEntity.createMember(null);
	Member member2 = CreateEntity.createMember(null);
	
	List<Tag> tags = CreateEntity.createTags(0, 2, false);
	List<Item> items = CreateEntity.createItems(2, member1, false);
	List<Resource> resources = CreateEntity.createResources(4, false);
	List<Comment> comments = CreateEntity.createComments(2, false);
	Feed feed1;
	List<Feed> feeds;
	
	@BeforeEach
	public void setUp() throws Exception { 
		member1 = memberRepository.save(member1);
		member2 = memberRepository.save(member2);
		items = itemRepository.saveAll(items);
		tags = tagRepository.saveAll(tags);
		
		feed1 = CreateEntity.createFeed(null, member1, resources, comments, items, tags);
	}
	
	/**
	 * feedRepository.save()
	 */
	@Test
	public void 피드_추가_성공() throws Exception {
		//when
		Feed result = feedRepository.save(feed1);
		
		//then
		assertNotNull(result);
		assertEquals(result.getContent(), feed1.getContent());
		assertEquals(result.getResources().size(), feed1.getResources().size());
		assertEquals(result.getComments().size(), feed1.getComments().size());
		assertEquals(result.getComments().get(0).getContent(), feed1.getComments().get(0).getContent());
		assertEquals(result.getTags().size(), feed1.getTags().size());
		assertEquals(result.getItems().size(), feed1.getItems().size());
		assertEquals(result.getItems().get(0).getItem().getContent(), feed1.getItems().get(0).getItem().getContent());
	}
	
	@Test
	public void 피드_페이징_조회_성공() throws Exception {
		//given
		Feed feed2 = CreateEntity.createFeed(null, member1, CreateEntity.createResources(2, false), CreateEntity.createComments(2, true), items, tags);
		Feed feed3 = CreateEntity.createFeed(null, member1, CreateEntity.createResources(3, false), CreateEntity.createComments(2, true), items, tags);
		Feed feed4 = CreateEntity.createFeed(null, member2, CreateEntity.createResources(1, false), CreateEntity.createComments(2, true), items, tags);
		Feed feed5 = CreateEntity.createFeed(null, member2, CreateEntity.createResources(4, false), CreateEntity.createComments(2, true), items, tags);
		feeds = Lists.newArrayList(feed1, feed2, feed3, feed4, feed5);
		feeds = feedRepository.saveAll(feeds);
		Pageable pageable = PageRequest.of(1, 2);
		
		//쓰기 지연 저장소를 DB에 반영시키고 비우기 
		em.flush();
		//1차 캐시 비우기
		em.clear();
		
		//when
		Page<Feed> result = feedRepository.findEntityGraphBy(pageable);
		
		//then
		assertNotNull(result);
		assertEquals(result.getTotalElements(), feeds.size());
		assertEquals(result.getSize(), pageable.getPageSize());
		assertEquals(result.getContent().get(0).getMember().getId(), feeds.get(2).getMember().getId());
	}
	
	/**
	 * find() Test
	 * 피드id와 회원id로 피드를 조회에 성공한다. 
	 * @throws Exception
	 */
	@Test
	public void 피드id와_회원id로_피드_조회_성공() throws Exception {
		//given
		feed1 = feedRepository.save(feed1);
		
		//when
		Optional<Feed> result = feedRepository.findByIdAndMemberId(feed1.getId(), member1.getId());
		
		//then
		assertNotNull(result.get());
		assertEquals(result.get().getId(), feed1.getId());
	}
	
	@Test
	public void 피드id와_회원id로_피드_lazy_조회_성공() throws Exception {
		//given
		feed1 = feedRepository.save(feed1);
		
		em.flush();
		em.clear();
		
		//when
		Optional<Feed> result = feedRepository.findByIdAndMemberId(feed1.getId(), member1.getId());
		
		//then
		assertNotNull(result.get());
		assertEquals(result.get().getId(), feed1.getId());
		assertFalse(Hibernate.isInitialized(result.get().getMember()));
		result.get().getMember().getName(); // 초기화
		assertTrue(Hibernate.isInitialized(result.get().getMember()));
		assertFalse(Hibernate.isInitialized(result.get().getResources()));
		assertFalse(Hibernate.isInitialized(result.get().getComments()));
		assertFalse(Hibernate.isInitialized(result.get().getItems()));
	}
	
	/**
	 * find() Test
	 * 피드id와 회원id로 피드를 조회에 실패한다. 
	 * @throws Exception
	 */
	@Test
	public void 피드id와_회원id로_피드_조회_실패() throws Exception {
		//when
		Optional<Feed> result = feedRepository.findByIdAndMemberId(feed1.getId(), member1.getId());
		
		//then
		assertThat(result).isEmpty();
	}
	
	@Test
	public void 피드_패치조인_회원_조회_성공() throws Exception {
		//given
		feed1 = feedRepository.save(feed1);
		
		em.flush();
		em.clear();
		
		//when
		Optional<Feed> result = feedRepository.findFetchMemberById(feed1.getId());
		
		//then
		assertNotNull(result.get());
		assertEquals(result.get().getId(), feed1.getId());
		assertTrue(Hibernate.isInitialized(result.get().getMember()));
		assertFalse(Hibernate.isInitialized(result.get().getResources()));
		assertFalse(Hibernate.isInitialized(result.get().getComments()));
		assertFalse(Hibernate.isInitialized(result.get().getItems()));
	}
	
}
