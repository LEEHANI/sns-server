package com.may.app.member;

import org.springframework.stereotype.Service;

import com.may.app.member.entity.Member;
import com.may.app.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;
}
