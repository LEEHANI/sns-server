package com.may.app.follow.intTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.may.app.common.CreateEntity;
import com.may.app.follow.repository.FollowRepository;
import com.may.app.member.entity.Member;
import com.may.app.member.repository.MemberRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional 
@Profile("dev")
public class FollowIntTest {
	@Autowired private MemberRepository memberRepository;
	@Autowired private FollowRepository followRepository;
	@Autowired private MockMvc mvc;
	
	@Test
	public void 회원은_팔로우_할_수_있다() throws Exception {
		Member member1 = memberRepository.save(CreateEntity.createMember(null));
		Member member2 = memberRepository.save(CreateEntity.createMember(null));
		
		mvc.perform
		(
			post("/api/v1/follow/{id}", String.valueOf(member1.getId()))
				.param("followingId", String.valueOf(member2.getId()))
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.data").value(true))
		.andReturn();
	}
	
	@Test
	public void 회원은_언팔로우_할_수_있다() throws Exception {
		Member member1 = memberRepository.save(CreateEntity.createMember(null));
		Member member2 = memberRepository.save(CreateEntity.createMember(null));
		followRepository.save(CreateEntity.createFollow(null, member1, member2));
		
		mvc.perform
		(
			post("/api/v1/follow/unfollow")
				.param("id", String.valueOf(member1.getId()))
				.param("unfollowId", String.valueOf(member2.getId()))
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.data").value(true))
		.andReturn();
	}
	
	@Test
	public void 팔로잉_목록을_조회할_수_있다() throws Exception {
		Member member1 = memberRepository.save(CreateEntity.createMember(null));
		Member member2 = memberRepository.save(CreateEntity.createMember(null));
		Member member3 = memberRepository.save(CreateEntity.createMember(null));
		followRepository.save(CreateEntity.createFollow(null, member1, member2));
		followRepository.save(CreateEntity.createFollow(null, member1, member3));
	
		mvc.perform(get("/api/v1/follow/following/{id}", String.valueOf(member1.getId())))
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
	public void 팔로워_목록을_조회할_수_있다() throws Exception { 
		Member member1 = memberRepository.save(CreateEntity.createMember(null));
		Member member2 = memberRepository.save(CreateEntity.createMember(null));
		Member member3 = memberRepository.save(CreateEntity.createMember(null));
		followRepository.save(CreateEntity.createFollow(null, member2, member1));
		followRepository.save(CreateEntity.createFollow(null, member3, member1));
		
		mvc.perform(get("/api/v1/follow/follower/{id}", String.valueOf(member1.getId())))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.data.length()").value(2))
		.andExpect(jsonPath("$.data[0].userId").value(member2.getUserId()))
		.andExpect(jsonPath("$.data[0].name").value(member2.getName()));
	}
}
