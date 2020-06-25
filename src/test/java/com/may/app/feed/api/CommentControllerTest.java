package com.may.app.feed.api;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.may.app.common.CreateEntity;
import com.may.app.feed.CommentService;
import com.may.app.feed.FeedService;
import com.may.app.feed.dto.CommentDto;
import com.may.app.feed.dto.CommentDto.Post;
import com.may.app.feed.entity.Comment;
import com.may.app.feed.entity.Feed;
import com.may.app.member.entity.Member;

@WebMvcTest(value = {CommentController.class, CommentService.class})
public class CommentControllerTest {
	@Autowired private MockMvc mvc;
	@Autowired private ObjectMapper objMapper;
	@MockBean private CommentService commentService;
	@MockBean private FeedService feedService;
	
	@Test
	public void 댓글_저장_성공() throws Exception{
		//given
		Member member1 = CreateEntity.createMember(1L);
		Feed feed1 = CreateEntity.createFeed(1L, member1, 0, 0, 0);
		Comment comment1 = CreateEntity.createComment(1L, member1, feed1, null);
		given(commentService.add(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).willReturn(comment1);
		
		Post request = CommentDto.Post.builder()
			.memberId(member1.getId())
			.content(comment1.getContent())
			.build();
		
		//then
		mvc.perform
		(
			post("/api/v1/feed/{id}/comment", 1)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objMapper.writeValueAsString(request))
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content").value(request.getContent()))
		.andExpect(jsonPath("$.parentId").value(IsNull.nullValue()));
	}
	
	@Test
	public void 대댓글_저장_성공() throws Exception{
		//given
		Member member1 = CreateEntity.createMember(1L);
		Feed feed1 = CreateEntity.createFeed(1L, member1, 0, 0, 0);
		Comment parent1 = CreateEntity.createComment(1L, member1, feed1, null);
		Comment comment1 = CreateEntity.createComment(2L, member1, feed1, parent1);
		given(commentService.add(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).willReturn(comment1);
		
		Post request = CommentDto.Post.builder()
			.memberId(member1.getId())
			.content(comment1.getContent())
			.build();
		
		//when & then
		mvc.perform
		(
			post("/api/v1/feed/{id}/comment", 1)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objMapper.writeValueAsString(request))
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.parentId").value(parent1.getId()));
	}
	
	@Test
	public void 댓글_저장_필수값_없음_실패() throws Exception{
		//given
		Member member1 = CreateEntity.createMember(1L);
		Feed feed1 = CreateEntity.createFeed(1L, member1, 0, 0, 0);
		Comment parent1 = CreateEntity.createComment(1L, member1, feed1, null);
		Comment comment1 = CreateEntity.createComment(2L, member1, feed1, parent1);
		given(commentService.add(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).willReturn(comment1);
		
		Post request = CommentDto.Post.builder()
			.memberId(member1.getId())
			.build();
		
		//when & then
		mvc.perform
		(
			post("/api/v1/feed/{id}/comment", 1)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objMapper.writeValueAsString(request))
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.name()));
	}
	
	@Test
	public void 댓글_리스트_조회_성공() throws Exception{
		//given
		Member member1 = CreateEntity.createMember(1L);
		Feed feed1 = CreateEntity.createFeed(1L, member1, 0, 0, 0);
		Comment parent1 = CreateEntity.createComment(1L, member1, feed1, null);
		Comment parent2 = CreateEntity.createComment(2L, member1, feed1, null);
		
		Page<Comment> comments = new PageImpl<>(Lists.newArrayList(parent1, parent2));
		given(commentService.comments(Mockito.any(), Mockito.any())).willReturn(comments);
		
		//when & then 
		mvc.perform
		(
			get("/api/v1/feed/{id}/comments", 1)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("page", "0")
				.param("size", "2")
				.param("sort", "")
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.size()").value(2));
	}
	
	@Test
	public void 댓글과_대댓글_리스트_조회_성공() throws Exception{
		//given
		Member member1 = CreateEntity.createMember(1L);
		Feed feed1 = CreateEntity.createFeed(1L, member1, 0, 0, 0);
		Comment child1 = CreateEntity.createComment(2L, member1, feed1, null);
		Comment child2 = CreateEntity.createComment(2L, member1, feed1, null);
		Comment parent1 = CreateEntity.createCommentWithChildren(1L, member1, feed1, null, Lists.newArrayList(child1, child2));
		Comment parent2 = CreateEntity.createComment(2L, member1, feed1, null);
		
		Page<Comment> comments = new PageImpl<>(Lists.newArrayList(parent1, parent2));
		given(commentService.comments(Mockito.any(), Mockito.any())).willReturn(comments);
		
		//when & then
		mvc.perform
		(
			get("/api/v1/feed/{id}/comments", 1)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("page", "0")
				.param("size", "2")
				.param("sort", "")
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content.size()").value(2))
		.andExpect(jsonPath("$.content[0].children.size()").value(2));
	}
}
