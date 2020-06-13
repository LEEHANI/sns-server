package com.may.app.member;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.may.app.common.CreateEntity;
import com.may.app.member.entity.Member;
import com.may.app.member.repository.MemberRepository;

@SpringBootTest(classes = MemberService.class)
public class MemberServiceTest {
	@Autowired private MemberService memberService;
	@MockBean private MemberRepository memberRepository;
	
	Member member1 = CreateEntity.createMember(1L);
	/**
	 * memberRepository.getOne()
	 */
	@Test
	public void 멤버하나불러오기() throws Exception {
		// given
		given(memberRepository.getOne(member1.getId())).willReturn(member1);
		
		// when
		Object result = memberService.member(member1.getId());
		
		// then
		assertNotNull(result);
	}
	
	/**
	 * memberRepository.save()
	 */
	@Test
	public void 멤버저장() throws Exception {
		// given
		Member save = CreateEntity.createMember(null);
		
		given(memberRepository.save(save)).willReturn(member1);

		// when
		Object result = memberService.member(save);
		
		// then
		assertNotNull(result);
	}
}
