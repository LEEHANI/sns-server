package com.may.app.member;

import org.springframework.stereotype.Service;

import com.may.app.member.entity.Member;
import com.may.app.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;
	
	public Object member(Long m1) {
		Member result = memberRepository.getOne(m1);
		
		return result.getId();
	}
	
	public Object member(Member save) {
		Member member = memberRepository.save(save);
		
		return member.getId();
	}
}
