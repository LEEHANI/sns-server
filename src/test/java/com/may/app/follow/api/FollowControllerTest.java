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
import org.mockito.Mock;
import org.mockito.Mockito;
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
	
	@Test
	public void 팔로우_성공() throws Exception {
		// given
		given(followService.follow(Mockito.any(), Mockito.any())).willReturn(1L);
		
		// when & then
		mvc.perform
		(
			post("/api/v1/follow/{id}", 1)
				.param("followingId", "2")
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.data").value(true))
		.andReturn();
	}
	
	@Test
	public void 언팔로우_성공() throws Exception {
		// given
		given(followService.unfollow(Mockito.any(), Mockito.any())).willReturn(1L);
		
		// when & then
		mvc.perform
		(
			post("/api/v1/follow/unfollow")
				.param("id", "1")
				.param("unfollowId", "2")
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
		List<Member> members = Lists.newArrayList(CreateEntity.createMember(2L), CreateEntity.createMember(3L));
		given(followRepository.findByFollowing(Mockito.any())).willReturn(members);
		
		//when & then
		mvc.perform(get("/api/v1/follow/following/{id}", 1))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.data.length()").value(2))
		.andExpect(jsonPath("$.data[0].userId").value(members.get(0).getUserId()))
		.andExpect(jsonPath("$.data[0].name").value(members.get(0).getName()))
		.andExpect(jsonPath("$.data[1].userId").value(members.get(1).getUserId()))
		.andExpect(jsonPath("$.data[1].name").value(members.get(1).getName()));
	}
	
	@Test
	public void 팔로잉_리스트_데이터_없을때() throws Exception {
		//given
		given(followRepository.findByFollowing(Mockito.any())).willReturn(Lists.newArrayList());
		
		//when & then
		mvc.perform(get("/api/v1/follow/following/{id}", 1))
		.andDo(print())
		.andExpect(status().isOk());
	}

	/**
	 * followings() Test
	 * @throws Exception
	 */
	@Test
	public void 팔로워_리스트_성공() throws Exception {
		//given
		List<Member> members = Lists.newArrayList(CreateEntity.createMember(2L));
		given(followRepository.findByFollower(Mockito.any())).willReturn(members);
		
		//when & then
		mvc.perform(get("/api/v1/follow/follower/{id}", 1))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.data.length()").value(1))
		.andExpect(jsonPath("$.data[0].userId").value(members.get(0).getUserId()))
		.andExpect(jsonPath("$.data[0].name").value(members.get(0).getName()));
	}
}
