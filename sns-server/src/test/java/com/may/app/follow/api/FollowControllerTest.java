package com.may.app.follow.api;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.may.app.common.CreateEntity;
import com.may.app.follow.FollowService;
import com.may.app.follow.api.FollowController;
import com.may.app.follow.repository.FollowRepository;
import com.may.app.member.entity.Member;

@WebMvcTest(value = {FollowController.class, FollowService.class})
public class FollowControllerTest {
	
	@Autowired private MockMvc mvc;
	@MockBean private FollowService followService;
	@MockBean private FollowRepository followRepository;
	
	Member member1 = CreateEntity.createMember(1L);
	Member member2 = CreateEntity.createMember(2L);
	Member member3 = CreateEntity.createMember(3L);
	
	@Test
	public void 팔로우_성공() throws Exception {
		// given
		given(followService.follow(member1.getId(), member2.getId())).willReturn(member1.getId());
		
		// when & then
		mvc.perform
		(
			post("/api/v1/follow/{id}", member1.getId())
				.param("followingId", member2.getId().toString())
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.data").value(true))
		.andReturn();
	}
	
	@Test
	public void 언팔로우_성공() throws Exception {
		// given
		given(followService.unfollow(member1.getId(), member2.getId())).willReturn(member1.getId());
		
		// when & then
		mvc.perform
		(
			post("/api/v1/follow/unfollow")
				.param("id", member1.getId().toString())
				.param("unfollowId", member2.getId().toString())
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.data").value(true))
		.andReturn();
	}
	
	/**
	 * followings() Test
	 * @throws Exception
	 */
	@Test
	public void 팔로잉_리스트_성공() throws Exception {
		//given
		List<Member> followings = Lists.newArrayList(member2, member3);
		given(followRepository.findByFollowing(member1.getId())).willReturn(followings);
		
		//when & then
		mvc.perform(get("/api/v1/follow/following/{id}", member1.getId()))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.data.length()").value(2))
		.andExpect(jsonPath("$.data[0].userId").value(member2.getUserId()))
		.andExpect(jsonPath("$.data[0].name").value(member2.getName()))
		.andExpect(jsonPath("$.data[1].userId").value(member3.getUserId()))
		.andExpect(jsonPath("$.data[1].name").value(member3.getName()));
	}
	
	@Test
	public void 팔로잉_리스트_데이터_없을때() throws Exception {
		//given
		List<Member> followings = Lists.newArrayList();
		given(followRepository.findByFollowing(1L)).willReturn(followings);
		
		//when & then
		mvc.perform(get("/api/v1/follow/following/{id}", member1.getId()))
		.andDo(print())
		.andExpect(status().isOk())
		;
	}

	/**
	 * followings() Test
	 * @throws Exception
	 */
	@Test
	public void 팔로워_리스트_성공() throws Exception {
		//given
		List<Member> followers = Lists.newArrayList(member2);
		given(followRepository.findByFollower(member1.getId())).willReturn(followers);
		
		//when & then
		mvc.perform(get("/api/v1/follow/follower/{id}", member1.getId()))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.data.length()").value(1))
		.andExpect(jsonPath("$.data[0].userId").value(member2.getUserId()))
		.andExpect(jsonPath("$.data[0].name").value(member2.getName()))
		;
	}
}
