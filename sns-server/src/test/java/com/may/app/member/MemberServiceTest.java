package com.may.app.member;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.may.app.member.entity.Member;
import com.may.app.member.repository.MemberRepository;

@SpringBootTest()
public class MemberServiceTest {
	@Autowired private MemberService memberService;
	@MockBean private MemberRepository memberRepository;
	
	/**
	 * memberRepository.getOne()
	 */
	@Test
	public void 멤버하나불러오기() throws Exception {
		// given
		Member follower = Member.builder()
				.id(1L)
				.userId("abc")
				.name("abc")
				.blocked(false)
				.build();
		
		given(memberRepository.getOne(1L)).willReturn(follower);
		
		// when
		Object result = memberService.member(1L);
		
		// then
		assertNotNull(result);
	}
	
	/**
	 * memberRepository.save()
	 */
	@Test
	public void 멤버저장() throws Exception {
		// given
		Member save = Member.builder()
				.name("may")
				.password("may")
				.build();
		
		Member entity = Member.builder()
				.id(1L)
				.name(save.getPassword())
				.password(save.getName())
				.build();
		
		given(memberRepository.save(save)).willReturn(entity);

		// when
		Object result = memberService.member(save);
		
		// then
		assertNotNull(result);
	}
}
