package com.may.app.member;

import org.junit.jupiter.api.BeforeEach;

import com.may.app.common.CreateEntity;
import com.may.app.member.entity.Member;

public class MemberServiceTest {
	private MemberService memberService;
	private MemoryMemberRepository memberRepository = new MemoryMemberRepository();
	
	Member member1 = CreateEntity.createMember(1L);
	
	@BeforeEach
	public void setUp() throws Exception {
		memberService = new MemberService(memberRepository);
	}
}
