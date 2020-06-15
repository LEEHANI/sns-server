package com.may.app.member;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.may.app.feed.dto.FeedDto;
import com.may.app.feed.dto.FeedDto.Get;
import com.may.app.feed.repository.FeedRepository;
import com.may.app.item.ItemDto;
import com.may.app.item.entity.Item;
import com.may.app.item.repository.ItemRepository;
import com.may.app.member.dto.MemberDto;
import com.may.app.member.entity.Member;
import com.may.app.member.exception.NoMemberException;
import com.may.app.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;
	private final FeedRepository feedRepository;
	
	@Cacheable(cacheNames = "members", key="#id")//, condition = "#result.feeds > 0")
	public MemberDto.GetInfo detail(Long id) {
		Member member = memberRepository.findById(id).orElseThrow(()->new NoMemberException());
		List<FeedDto.Get> feeds = feedRepository.findByMemberId(id).stream().map(FeedDto.Get::new).collect(Collectors.toList());
		
		return new MemberDto.GetInfo(member, feeds);
	}
}
