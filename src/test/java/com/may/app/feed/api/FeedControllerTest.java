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
import org.mockito.Mockito;
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
	 */
	@Test
	public void 피드_저장_성공() throws Exception {
		//given
		Member member1 = CreateEntity.createMember(1L);
		Feed feed1 = CreateEntity.createFeed
				(
					1L, 
					member1, 
					CreateEntity.createResources(1, true), 
					CreateEntity.createItems(1, member1, true), 
					CreateEntity.createTags(0,1, true)
				);
		
		FeedDto.Post request = FeedDto.Post
				.builder()
				.content (feed1.getContent())
				.memberId(member1.getId())
				.imgs(Lists.newArrayList(feed1.getResources().get(0).getPath()))
				.tags(Lists.newArrayList(feed1.getTags().get(0).getTag().getTitle()))
				.itemIds(Lists.newArrayList(feed1.getItems().get(0).getItem().getId()))
				.build();
		
		given(feedService.add(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).willReturn(feed1);
		
		//when & then
		mvc.perform
		(
			post("/api/v1/feed")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objMapper.writeValueAsString(request))
		
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.member.userId").value(feed1.getMember().getUserId()))
		.andExpect(jsonPath("$.imgs.size()").value(request.getImgs().size()))
		.andExpect(jsonPath("$.items.size()").value(request.getItemIds().size()))
		.andExpect(jsonPath("$.tags.size()").value(request.getTags().size()));
	}

	/**
	 * save() Test 실패
	 * 필수값이 없으면 bad request를 반환한다. 
	 */
	@Test
	public void 피드_저장_필수값_없음_실패() throws Exception {
		//given
		Member member1 = CreateEntity.createMember(1L);
		Feed feed1 = CreateEntity.createFeed(1L, member1, null, null, null);
		
		FeedDto.Post request = FeedDto.Post
				.builder()
				.memberId(member1.getId())
				.build();
		
		given(feedService.add(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).willReturn(feed1);
		
		//when & then
		mvc.perform
		(
			post("/api/v1/feed")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objMapper.writeValueAsString(request))
		)
		.andDo(print())
		.andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.name()));
	}
	
	/**
	 * find() Test 성공
	 */
	@Test
	public void 피드_상세_조회_성공() throws Exception {
		//given
		Member member1 = CreateEntity.createMember(1L);
		Feed feed1 = CreateEntity.createFeed
				(
					1L, 
					member1, 
					CreateEntity.createResources(1, true), 
					CreateEntity.createItems(1, member1, true), 
					CreateEntity.createTags(0,1, true)
				);
		
		given(feedService.detail(Mockito.any(), Mockito.any())).willReturn(new FeedDto.Get(feed1));
		
		//when & then
		mvc.perform
		(
			get("/api/v1/feed/{id}", feed1.getId())
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content").value(feed1.getContent()))
		.andExpect(jsonPath("$.member.userId").value(feed1.getMember().getUserId()))
		.andExpect(jsonPath("$.imgs.size()").value(feed1.getResources().size()))
		.andExpect(jsonPath("$.items.size()").value(feed1.getItems().size()))
		.andExpect(jsonPath("$.tags.size()").value(feed1.getTags().size()));
	}
	
	/**
	 * list find() Test 성공
	 */
	@Test
	public void 피드_페이징_리스트_조회_성공() throws Exception {
		//given
		Member member1 = CreateEntity.createMember(1L);
		List<FeedDto.GetList> feeds = Lists.newArrayList
				(
					new FeedDto.GetList(CreateEntity.createFeed(1L, member1, 0, 0, 0)),
					new FeedDto.GetList(CreateEntity.createFeed(2L, member1, 0, 0, 0)),
					new FeedDto.GetList(CreateEntity.createFeed(3L, member1, 0, 0, 0)),
					new FeedDto.GetList(CreateEntity.createFeed(4L, member1, 0, 0, 0))
				);
		Page<FeedDto.GetList> pageFeeds = new PageImpl<>(feeds, PageRequest.of(1, 2), feeds.size());
		
		given(feedService.list(Mockito.any(), Mockito.any())).willReturn(pageFeeds);

		//when & then
		mvc.perform
		(
			get("/api/v1/feed")
				.param("page", "1")
				.param("size", "2")
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.size").value(pageFeeds.getPageable().getPageSize()))
		.andExpect(jsonPath("$.last").value(true));
	}
	
	/**
	 * list() Test 성공
	 */
	@Test
	public void 피드_페이징_리스트_데이터_없을때() throws Exception {
		//given
		Page<FeedDto.GetList> pageFeeds = Page.empty(PageRequest.of(0, 2));
		given(feedService.list(Mockito.any(), Mockito.any())).willReturn(pageFeeds);
		
		//when & then
		mvc.perform(get("/api/v1/feed"))
			.andDo(print())
			.andExpect(status().isOk());
	}
	
	/**
	 * edit() Test 성공
	 */
	@Test
	public void 피드_수정_성공() throws Exception {
		//given
		Member member1 = CreateEntity.createMember(1L);
		Feed feed1 = CreateEntity.createFeed
				(
					1L, 
					member1, 
					CreateEntity.createResources(1, true), 
					CreateEntity.createItems(1, member1, true), 
					CreateEntity.createTags(0,1, true)
				);
		
		FeedDto.Put request = FeedDto.Put.builder()
				.content(feed1.getContent())
				.memberId(member1.getId())
				.build();
		
		given(feedService.edit(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).willReturn(feed1);
		
		//when & then
		mvc.perform
		(
			put("/api/v1/feed/{id}", feed1.getId())
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objMapper.writeValueAsString(request))
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content").value(request.getContent()));
	}
	
	/**
	 * edit() Test 실패
	 * 필수값이 없으면 bad request를 반환한다. 
	 */
	@Test
	public void 피드_수정_필수값_없음_실패() throws Exception {
		//given
		FeedDto.Put request = FeedDto.Put.builder()
				.memberId(1L)
				.build();
		
		given(feedService.edit(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).willReturn(new Feed());
		
		//when & then
		mvc.perform
		(
			put("/api/v1/feed/{id}", 1)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objMapper.writeValueAsString(request))
		)
		.andDo(print())
		.andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.name()))
		.andReturn();
	}
	
	/**
	 * delete() Test 성공
	 */
	@Test
	public void 피드_삭제_성공() throws Exception {
		//given
		given(feedService.delete(Mockito.any(), Mockito.any())).willReturn(1L);
		
		//when & then
		mvc.perform
		(
			delete("/api/v1/feed/{id}", 1)
				.param("memberId", "1")
		)
		.andDo(print())
		.andExpect(jsonPath("$.data").value(true));
	}
	
	/**
	 * delete() Test 실패 
	 * 피드 삭제 
	 */
	@Test
	public void 피드_삭제_실패() throws Exception {
		//given
		given(feedService.delete(Mockito.any(), Mockito.any())).willReturn(null);
		
		//when & then
		mvc.perform
		(
			delete("/api/v1/feed/{id}", 1)
				.param("memberId", "1")
		)
		.andDo(print())
		.andExpect(jsonPath("$.data").value(false));
	}
	
	/**
	 * save() Test 성공 
	 */
	@Test
	public void 피드_좋아요_성공() throws Exception {
		//given
		given(feedService.good(Mockito.any(), Mockito.any())).willReturn(1L);
		
		//when & then
		mvc.perform
		(
			post("/api/v1/feed/good/{id}", "1")
				.param("memberId", "1")
		)
		.andExpect(jsonPath("$.data").value(true));
	}
	
	/**
	 * delete() Test 성공 
	 */
	@Test
	public void 피드_좋아요_해제_성공() throws Exception {
		//given
		given(feedService.good(Mockito.any(), Mockito.any())).willReturn(1L);
		
		//when & then
		mvc.perform
		(
			delete("/api/v1/feed/good/{id}", "1")
				.param("memberId", "1")
		)
		.andExpect(jsonPath("$.data").value(true));
	}
}


