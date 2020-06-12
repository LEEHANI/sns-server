package com.may.app.feed.api;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.may.app.common.CreateEntity;
import com.may.app.exception.ControllerExceptionHandler;
import com.may.app.feed.FeedService;
import com.may.app.feed.dto.FeedDto;
import com.may.app.feed.entity.Comment;
import com.may.app.feed.entity.Feed;
import com.may.app.feed.entity.Resource;
import com.may.app.item.entity.Item;
import com.may.app.member.entity.Member;
import com.may.app.tag.entity.Tag;

@WebMvcTest(value = {FeedController.class, FeedService.class})
public class FeedControllerTest {
	@Autowired private MockMvc mvc;
	@Autowired private ObjectMapper objMapper;
	@Autowired private FeedController feedController;
	@Autowired private ControllerExceptionHandler controllerAdvice;
	@MockBean private FeedService feedService;
	
	Member member1 = CreateEntity.createMember(1L);
	
	List<Resource> resources = CreateEntity.createResources(4, true);
	List<Comment> comments = CreateEntity.createComments(3, true);
	List<Item> items = CreateEntity.createItems(2, member1, true);
	List<Tag> tags = CreateEntity.createTags(0,1, true);
	
	Feed feed1 = CreateEntity.createFeed(1L, member1, resources, comments, items, tags);
	Feed feed2 = CreateEntity.createFeed(1L, member1, resources, comments, items, tags);
	Feed feed3 = CreateEntity.createFeed(1L, member1, resources, comments, items, tags);
	Feed feed4 = CreateEntity.createFeed(1L, member1, resources, comments, items, tags);
	
	@BeforeEach
	public void setUp() throws Exception {
		
		mvc = MockMvcBuilders
				.standaloneSetup(feedController)
				.setControllerAdvice(controllerAdvice)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
	            .build();
	}
	
	/**
	 * save() Test 성공
	 * 피드 저장 성공 테스트
	 */
	@Test
	public void 피드_저장_성공() throws Exception {
		//given
		List<String> comment = comments.stream().map(o->o.getContent().toString()).collect(Collectors.toList());
		List<String> imgs = resources.stream().map(o->o.getPath().toString()).collect(Collectors.toList());
		List<String> tag = tags.stream().map(o->o.getTitle().toString()).collect(Collectors.toList());
		List<Long> itemIds = items.stream().map(o->o.getId().longValue()).collect(Collectors.toList());
		
		FeedDto.Post request = FeedDto.Post
				.builder()
				.content ("피드내용")
				.memberId(member1.getId())
				.imgs(imgs)
				.comments(comment)
				.tags(tag)
				.itemIds(itemIds)
				.build();
		
		given
		(
			feedService.add
			(
				request.getContent(), 
				request.getMemberId(), 
				request.getImgs(), 
				request.getComments(), 
				request.getTags(), 
				request.getItemIds()
			)
		).willReturn(feed1);
		
		//when & then
		mvc.perform
		(
			post("/api/v1/feed")
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objMapper.writeValueAsString(request))
		
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.content").value(feed1.getContent()))
		.andExpect(jsonPath("$.member.userId").value(feed1.getMember().getUserId()))
		.andExpect(jsonPath("$.imgs.size()").value(imgs.size()))
		.andExpect(jsonPath("$.imgs[0].path").value(resources.get(0).getPath()))
		.andExpect(jsonPath("$.comments[2].content").value(comments.get(2).getContent()))
		.andExpect(jsonPath("$.items.size()").value(itemIds.size()))
		.andExpect(jsonPath("$.tags.size()").value(tags.size()));
	}

	/**
	 * save() Test 실패
	 * 필수값이 없으면 bad request를 반환한다. 
	 * @throws Exception
	 */
	@Test
	public void 피드_저장_필수값_없음_실패() throws Exception {
		//given
		FeedDto.Post request = FeedDto.Post
				.builder()
				.content(null)
				.memberId(member1.getId())
				.imgs(null)
				.comments(null)
				.tags(null)
				.itemIds(null)
				.build();
		
		given
		(
			feedService.add
			(
				request.getContent(), 
				request.getMemberId(), 
				request.getImgs(), 
				request.getComments(), 
				request.getTags(), 
				request.getItemIds()
			)
		).willReturn(feed1);
		
		//when & then
		mvc.perform
		(
			post("/api/v1/feed")
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objMapper.writeValueAsString(request))
		)
		.andDo(print())
		.andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.name()))
		.andReturn()
		;
	}
	
	/**
	 * find() Test 성공
	 * 피드 상세 조회 
	 * @throws Exception
	 */
	@Test
	public void 피드_상세_조회_성공() throws Exception {
		//given
		given(feedService.detail(feed1.getId())).willReturn(feed1);
		
		//when & then
		mvc.perform
		(
			get("/api/v1/feed/{id}", feed1.getId())
				.accept(MediaType.APPLICATION_JSON_UTF8)
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.content").value(feed1.getContent()))
		.andExpect(jsonPath("$.member.userId").value(feed1.getMember().getUserId()))
		.andExpect(jsonPath("$.imgs.size()").value(feed1.getResources().size()))
		.andExpect(jsonPath("$.imgs[0].path").value(resources.get(0).getPath()))
		.andExpect(jsonPath("$.comments[2].content").value(comments.get(2).getContent()))
		.andExpect(jsonPath("$.items.size()").value(feed1.getItems().size()))
		.andExpect(jsonPath("$.tags.size()").value(feed1.getTags().size()));
	}
	
	/**
	 * list find() Test 성공
	 * 피드 페이징 리스트 조회 
	 * @throws Exception
	 */
	@Test
	public void 피드_페이징_리스트_조회_성공() throws Exception {
		//given
		List<Feed> feeds = Lists.newArrayList(feed1, feed2, feed3, feed4);
		PageRequest pageable = PageRequest.of(1, 2);
		Page<Feed> pageFeeds = new PageImpl<>(feeds, pageable, feeds.size());
		given(feedService.list(pageable)).willReturn(pageFeeds);

		//when & then
		mvc.perform
		(
			get("/api/v1/feed")
				.param("page", String.valueOf(pageable.getPageNumber()))
				.param("size", String.valueOf(pageable.getPageSize()))
				.accept(MediaType.APPLICATION_JSON_UTF8)
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.size").value(pageable.getPageSize()))
		.andExpect(jsonPath("$.last").value(true))
		;
	}
	
	/**
	 * list() Test 성공
	 * @throws Exception
	 */
	@Test
	public void 피드_페이징_리스트_데이터_없을때() throws Exception {
		//given
		PageRequest pageable = PageRequest.of(0, 2);
		Page<Feed> pageFeeds = Page.empty(pageable);
		given(feedService.list(pageable)).willReturn(pageFeeds);
		
		//when & then
		mvc.perform(get("/api/v1/feed")
				.accept(MediaType.APPLICATION_JSON_UTF8))
		.andDo(print())
		.andExpect(status().isOk())
		;
	}
	
	/**
	 * edit() Test 성공
	 * @throws Exception
	 */
	@Test
	public void 피드_수정_성공() throws Exception {
		//given
		List<String> imgs = feed1.getResources().stream().map(o->o.getPath()).collect(Collectors.toList());
		List<String> tags = feed1.getTags().stream().map(o->o.getTag().getTitle()).collect(Collectors.toList());
		List<Long> itemIds = feed1.getItems().stream().map(o->o.getItem().getId()).collect(Collectors.toList());
		final String content = feed1.getContent();
		
		FeedDto.Put request = FeedDto.Put.builder()
				.content(content)
				.memberId(member1.getId())
				.imgs(imgs)
				.tags(tags)
				.itemIds(itemIds)
				.build();
		
		given
		(
			feedService.edit
			(
				feed1.getId(), 
				request.getContent(), 
				request.getMemberId(), 
				request.getImgs(), 
				request.getTags(), 
				request.getItemIds()
			)
		).willReturn(feed1);
		
		//when & then
		mvc.perform
		(
			put("/api/v1/feed/{id}", feed1.getId())
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objMapper.writeValueAsString(request))
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(jsonPath("$.content").value(content))
		.andExpect(jsonPath("$.member.userId").value(feed1.getMember().getUserId()))
		.andExpect(jsonPath("$.imgs.size()").value(imgs.size()))
		.andExpect(jsonPath("$.imgs[0].path").value(imgs.get(0)))
		.andExpect(jsonPath("$.items.size()").value(itemIds.size()))
		.andExpect(jsonPath("$.tags.size()").value(tags.size()));
		
	}
	
	/**
	 * edit() Test 실패
	 * 필수값이 없으면 bad request를 반환한다. 
	 * @throws Exception
	 */
	@Test
	public void 피드_수정_필수값_없음_실패() throws Exception {
		//given
		FeedDto.Put request = FeedDto.Put.builder()
				.content(null)
				.memberId(member1.getId())
				.imgs(null)
				.tags(null)
				.itemIds(null)
				.build();
		
		given
		(
			feedService.edit
			(
				feed1.getId(), 
				request.getContent(), 
				request.getMemberId(), 
				request.getImgs(), 
				request.getTags(), 
				request.getItemIds()
			)
		).willReturn(feed1);
		
		//when & then
		mvc.perform
		(
			put("/api/v1/feed/{id}", feed1.getId())
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objMapper.writeValueAsString(request))
		)
		.andDo(print())
		.andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.name()))
		.andReturn()
		;
	}
	
	/**
	 * delete() Test 성공
	 * @throws Exception
	 */
	@Test
	public void 피드_삭제_성공() throws Exception {
		//given
		given(feedService.delete(feed1.getId(), member1.getId())).willReturn(feed1.getId());
		
		//when & then
		mvc.perform
		(
			delete("/api/v1/feed/{id}", feed1.getId())
				.param("memberId", member1.getId().toString())
		)
		.andDo(print())
		.andExpect(jsonPath("$.data").value(true))
		;
	}
	
	/**
	 * delete() Test 실패 
	 * 피드 삭제 
	 * @throws Exception
	 */
	@Test
	public void 피드_삭제_실패() throws Exception {
		//given
		given(feedService.delete(feed1.getId(), member1.getId())).willReturn(null);
		
		//when & then
		mvc.perform
		(
			delete("/api/v1/feed/{id}", feed1.getId())
				.param("memberId", member1.getId().toString())
		)
		.andDo(print())
		.andExpect(jsonPath("$.data").value(false))
		;
	}
}


