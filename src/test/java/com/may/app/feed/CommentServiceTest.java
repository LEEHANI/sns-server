package com.may.app.feed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.Optional;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.may.app.common.CreateEntity;
import com.may.app.feed.entity.Comment;
import com.may.app.feed.entity.Feed;
import com.may.app.feed.exception.NoCommentException;
import com.may.app.feed.repository.CommentRepository;
import com.may.app.feed.repository.FeedRepository;
import com.may.app.member.entity.Member;
import com.may.app.member.repository.MemberRepository;

@SpringBootTest(classes = CommentService.class)
@ActiveProfiles("test")
public class CommentServiceTest {
	@Autowired private CommentService commentService;
	@MockBean private CommentRepository commentRepository;
	@MockBean private FeedRepository feedRepository;
	@MockBean private MemberRepository memberRepository;
	
	@Test
	public void 댓글_추가_성공() {
		Member member1 = CreateEntity.createMember(1L);
		Feed feed1 = CreateEntity.createFeed(1L, member1, 0, 0, 0);
		Comment comment1 = CreateEntity.createComment(1L, member1, feed1, null);
		
		given(feedRepository.findById(Mockito.any())).willReturn(Optional.of(feed1));
		given(memberRepository.findById(Mockito.any())).willReturn(Optional.of(member1));
		given(commentRepository.save(Mockito.any())).willReturn(comment1);
		
		Comment result = commentService.add(1L, 1L, "content", null);
		
		assertNotNull(result);
		assertEquals(result.getMember(), member1);
	}
	
	@Test
	public void 대댓글_추가_성공() {
		Member member1 = CreateEntity.createMember(1L);
		Feed feed1 = CreateEntity.createFeed(1L, member1, 0, 0, 0);
		Comment parent = CreateEntity.createComment(1L, member1, feed1, null);
		Comment child1 = CreateEntity.createComment(1L, member1, feed1, parent);
		
		given(feedRepository.findById(Mockito.any())).willReturn(Optional.of(feed1));
		given(memberRepository.findById(Mockito.any())).willReturn(Optional.of(member1));
		given(commentRepository.findById(Mockito.any())).willReturn(Optional.of(parent));
		given(commentRepository.save(Mockito.any())).willReturn(child1);
		
		Comment result = commentService.add(1L, 1L, "content", parent.getId());
		
		assertNotNull(result);
		assertEquals(result.getParent(), parent);
		assertEquals(result.getMember(), member1);
	}
	
	@Test
	public void 대댓글_추가_실패() {
		Member member1 = CreateEntity.createMember(1L);
		Feed feed1 = CreateEntity.createFeed(1L, member1, 0, 0, 0);
		Comment parent = CreateEntity.createComment(1L, member1, feed1, null);
		Comment child1 = CreateEntity.createComment(1L, member1, feed1, parent);
		
		given(feedRepository.findById(Mockito.any())).willReturn(Optional.of(feed1));
		given(memberRepository.findById(Mockito.any())).willReturn(Optional.of(member1));
		given(commentRepository.save(Mockito.any())).willReturn(child1);
		
		assertThrows(NoCommentException.class, ()-> commentService.add(1L, 1L, "content", parent.getId()));
	}
	
	@Test
	public void 댓글_조회_성공() {
		Member member1 = CreateEntity.createMember(1L);
		Feed feed1 = CreateEntity.createFeed(1L, member1, 0, 0, 0);
		Page<Comment> comments = new PageImpl<>(
				Lists.newArrayList(CreateEntity.createComment(1L, member1, feed1, null)), 
				PageRequest.of(1, 2), 
				4);
		
		given(feedRepository.findById(Mockito.any())).willReturn(Optional.of(feed1));
		given(commentRepository.findComments(Mockito.any(), Mockito.any())).willReturn(comments);
		
		Page<Comment> result = commentService.comments(1L, PageRequest.of(1, 2));
		
		assertNotNull(result);
		assertEquals(result.getContent().size(), comments.getContent().size());
	}
	
	@Test
	public void 댓글과_대댓글_조회_성공() {
		Member member1 = CreateEntity.createMember(1L);
		Feed feed1 = CreateEntity.createFeed(1L, member1, 0, 0, 0);
		
		Comment comment1 = CreateEntity.createCommentWithChildren
			(
				1L, 
				member1, 
				feed1, 
				null, 
				Lists.newArrayList(CreateEntity.createComment(1L, member1, feed1, null))
			);
		
		Page<Comment> comments = new PageImpl<>(
				Lists.newArrayList(comment1), 
				PageRequest.of(1, 2), 
				4);
		
		given(feedRepository.findById(Mockito.any())).willReturn(Optional.of(feed1));
		given(commentRepository.findComments(Mockito.any(), Mockito.any())).willReturn(comments);
		
		Page<Comment> result = commentService.comments(1L, PageRequest.of(1, 2));
		
		assertNotNull(result);
		assertEquals(result.getContent().get(0).getChildren().size(), comments.getContent().get(0).getChildren().size());
	}
}
