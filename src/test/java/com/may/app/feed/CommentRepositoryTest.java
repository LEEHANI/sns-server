package com.may.app.feed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.may.app.common.CreateEntity;
import com.may.app.configuration.QuerydslConfiguration;
import com.may.app.feed.entity.Comment;
import com.may.app.feed.entity.Feed;
import com.may.app.feed.repository.CommentRepository;
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
@Import(value = {QuerydslConfiguration.class})
public class CommentRepositoryTest {
	@Autowired private CommentRepository commentRepository;
	@Autowired private FeedRepository feedRepository;
	@Autowired private MemberRepository memberRepository;
	@Autowired private ItemRepository itemRepository;
	@Autowired private TagRepository tagRepository;
	@Autowired private EntityManager em;
	
	@Test
	public void 댓글_리스트_조회_성공() throws Exception {
		//given
		Member member1 = memberRepository.save(CreateEntity.createMember(null));
		List<Item> items = itemRepository.saveAll(CreateEntity.createItems(2, member1, false));
		List<Tag> tags = tagRepository.saveAll(CreateEntity.createTags(0, 2, false));
		Feed feed1 = feedRepository.save(CreateEntity.createFeed(null, member1, CreateEntity.createResources(1, false), items, tags));
		
		Comment parent1 = commentRepository.save(CreateEntity.createComment(null, member1, feed1, null));
		Comment parent2 = commentRepository.save(CreateEntity.createComment(null, member1, feed1, null));
		Comment parent3 = commentRepository.save(CreateEntity.createComment(null, member1, feed1, null));
		Comment child = commentRepository.save(CreateEntity.createComment(null, member1, feed1, parent2));
		
		em.clear();
		
		//when
		Page<Comment> result = commentRepository.findComments(feed1, PageRequest.of(0, 2));
		
		//then
		assertNotNull(result);
		assertEquals(result.getTotalElements(), 3L);
		assertEquals(result.getContent().get(1).getChildren().size(), 1);
	}
}
