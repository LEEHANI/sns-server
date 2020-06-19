package com.may.app.member.api;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Collectors;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.may.app.common.CreateEntity;
import com.may.app.feed.dto.FeedDto;
import com.may.app.feed.entity.Feed;
import com.may.app.member.MemberService;
import com.may.app.member.dto.MemberDto;
import com.may.app.member.entity.Member;

@WebMvcTest(value = {MemberController.class, MemberService.class})
public class MemberControllorTest {
	@Autowired private MockMvc mvc;
	@MockBean private MemberService memberService;

	@Test
	public void 회원_상세_조회_성공() throws Exception {
		//given
		Member member1 = CreateEntity.createMember(1L);
		Feed feed1 = CreateEntity.createFeed(1L, member1, 1, 1, 1);
		Feed feed2 = CreateEntity.createFeed(2L, member1, 1, 1, 1);
		
		MemberDto.GetInfo dto = new MemberDto.GetInfo(member1, Lists.newArrayList(feed1, feed2).stream().map(FeedDto.Get::new).collect(Collectors.toList()));
		given(memberService.detail(member1.getId())).willReturn(dto);
		
		//when
		mvc.perform
		(
			get("/api/v1/member/{id}", member1.getId())
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
		)
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.name").value(dto.getName()))
		.andExpect(jsonPath("$.feeds.size()").value(dto.getFeeds().size()));
	}
}
