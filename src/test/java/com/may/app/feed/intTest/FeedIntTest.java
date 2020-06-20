package com.may.app.feed.intTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.may.app.common.CreateEntity;
import com.may.app.feed.FeedService;
import com.may.app.feed.dto.FeedDto;
import com.may.app.feed.entity.Feed;
import com.may.app.feed.repository.FeedRepository;
import com.may.app.item.entity.Item;
import com.may.app.item.repository.ItemRepository;
import com.may.app.member.entity.Member;
import com.may.app.member.repository.MemberRepository;
import com.may.app.tag.entity.Tag;
import com.may.app.tag.repository.TagRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional 
@Profile("dev")
public class FeedIntTest {
	@Autowired private FeedRepository feedRepository;
	@Autowired private ItemRepository itemRepository;
	@Autowired private TagRepository tagRepository;
	@Autowired private MemberRepository memberRepository;
	@Autowired private FeedService feedService;
	@Autowired private MockMvc mvc;
	@Autowired private ObjectMapper objMapper;
	
	@Test
	public void 회원은_피드를_작성할_수_있다() throws Exception {
		//given
		Member member1 = memberRepository.save(CreateEntity.createMember(null));
		List<Item> items = itemRepository.saveAll(CreateEntity.createItems(1, member1, false));
		List<Tag> tags = tagRepository.saveAll(CreateEntity.createTags(0, 1, false));
		
		FeedDto.Post request = FeedDto.Post
				.builder()
				.content ("피드내용")
				.memberId(member1.getId())
				.imgs(Lists.newArrayList("img1"))
				.tags(Lists.newArrayList(tags.get(0).getTitle()))
				.itemIds(Lists.newArrayList(items.get(0).getId()))
				.build();
		
		//then & when
		mvc.perform
		(
			post("/api/v1/feed")
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objMapper.writeValueAsString(request))
		
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content").value(request.getContent()))
		.andExpect(jsonPath("$.imgs.size()").value(request.getImgs().size()))
		.andExpect(jsonPath("$.items.size()").value(request.getImgs().size()))
		.andExpect(jsonPath("$.tags.size()").value(request.getTags().size()));
	}
	
	@Test
	public void 회원은_피드를_수정할_수_있다() throws Exception {
		//given
		Member member1 = memberRepository.save(CreateEntity.createMember(null));
		Feed feed1 = feedRepository.save(CreateEntity.createFeed(null, member1, CreateEntity.createResources(1, false), null, null));
		List<Tag> tags = tagRepository.saveAll(CreateEntity.createTags(0, 1, false));
		
		FeedDto.Put request = FeedDto.Put.builder()
				.content("수정")
				.memberId(member1.getId())
				.imgs(Lists.newArrayList("img0"))
				.tags(Lists.newArrayList(tags.get(0).getTitle()))
				.itemIds(Lists.newArrayList())
				.build();
		
		//then & when
		mvc.perform
		(
			put("/api/v1/feed/{id}", feed1.getId())
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objMapper.writeValueAsString(request))
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content").value(request.getContent()))
		.andExpect(jsonPath("$.imgs.size()").value(request.getImgs().size()))
		.andExpect(jsonPath("$.items.size()").value(request.getItemIds().size()))
		.andExpect(jsonPath("$.tags.size()").value(request.getTags().size()));
	}
	
	@Test
	public void 회원은_피드를_삭제할_수_있다() throws Exception {
		//given
		Member member1 = memberRepository.save(CreateEntity.createMember(null));
		Feed feed1 = feedRepository.save(CreateEntity.createFeed(null, member1, null, null, null));
		
		//then & when
		mvc.perform
		(
			delete("/api/v1/feed/{id}", feed1.getId())
				.param("memberId",String.valueOf(member1.getId()))
		)
		.andDo(print())
		.andExpect(jsonPath("$.data").value(true));
	}
	
	@Test
	public void 피드_목록을_조회할_수_있다() throws Exception {
		//given
		Member member1 = CreateEntity.createMember(1L);
		List<Item> items = itemRepository.saveAll(CreateEntity.createItems(1, member1, false));
		List<Tag> tags = tagRepository.saveAll(CreateEntity.createTags(0, 1, false));
		List<Feed> saveFeeds = Lists.newArrayList
				(
					CreateEntity.createFeed(null, member1, CreateEntity.createResources(1, false), items, tags),
					CreateEntity.createFeed(null, member1, CreateEntity.createResources(2, false), items, tags),
					CreateEntity.createFeed(null, member1, CreateEntity.createResources(1, false), items, null),
					CreateEntity.createFeed(null, member1, CreateEntity.createResources(4, false), null, null)
				);
		List<Feed> feeds = feedRepository.saveAll(saveFeeds);

		//when & then
		mvc.perform
		(
			get("/api/v1/feed")
				.param("page", "1")
				.param("size", "2")
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.content[0].member.userId").value(feeds.get(3).getMember().getUserId()))
		.andExpect(jsonPath("$.number").value("1"));

	}
	
	@Test
	public void 피드를_상세_조회할_수_있다() throws Exception {
		//given
		Member member1 = memberRepository.save(CreateEntity.createMember(null));
		List<Item> items = itemRepository.saveAll(CreateEntity.createItems(1, member1, false));
		List<Tag> tags = tagRepository.saveAll(CreateEntity.createTags(0, 1, false));
		Feed feed1 = feedRepository.save(CreateEntity.createFeed(null, member1, CreateEntity.createResources(1, false), items, tags));
		
		//then & when
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
	
	@Test
	public void 회원은_피드_좋아요를_할_수_있다() throws Exception {
		//given
		Member member1 = memberRepository.save(CreateEntity.createMember(null));
		Feed feed1 = feedRepository.save(CreateEntity.createFeed(null, member1, null, null, null));
		
		//then & when
		mvc.perform
		(
			post("/api/v1/feed/good/"+feed1.getId())
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("memberId", String.valueOf(member1.getId()))
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.data").value(true));
	}
	
	@Test
	public void 회원은_피드_좋아요_해제를_할_수_있다() throws Exception {
		//given
		Member member1 = memberRepository.save(CreateEntity.createMember(null));
		Feed feed1 = feedRepository.save(CreateEntity.createFeed(null, member1, null, null, null));
		feedService.good(feed1.getId(), member1.getId());
		
		//then & when
		mvc.perform
		(
				delete("/api/v1/feed/good/"+feed1.getId())
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.param("memberId", String.valueOf(member1.getId()))
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.data").value(true));
	}
}
