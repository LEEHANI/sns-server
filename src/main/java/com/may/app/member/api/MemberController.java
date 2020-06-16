package com.may.app.member.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.may.app.member.MemberService;
import com.may.app.member.dto.MemberDto;
import com.may.app.member.dto.MemberDto.GetInfo;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {
	private final MemberService memberService;
	
	@GetMapping(value = "/{id}")
	public MemberDto.GetInfo detail(@PathVariable Long id) {
		return memberService.detail(id);
	}

}
