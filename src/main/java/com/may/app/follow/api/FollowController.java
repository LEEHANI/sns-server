package com.may.app.follow.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.may.app.common.ResponseListDto;
import com.may.app.follow.FollowService;
import com.may.app.follow.repository.FollowRepository;
import com.may.app.member.MemberDto;
import com.may.app.member.MemberDto.Get;
import com.may.app.member.entity.Member;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/follow")
@RequiredArgsConstructor
public class FollowController {
	private final FollowService followService;
	private final FollowRepository followRepository;
	
	@PostMapping(value = "/{id}")
	public ResponseListDto<Boolean> follow(@PathVariable Long id, @RequestParam Long followingId) {
		Long result = followService.follow(id, followingId);
		
		return new ResponseListDto<Boolean>(result==null? false: true);
	}
	
	@PostMapping(value = "/unfollow")
	public ResponseListDto<Boolean> unfollow(@RequestParam Long id, @RequestParam Long unfollowId) {
		Long result = followService.unfollow(id, unfollowId);
		
		return new ResponseListDto<Boolean>(result==null? false: true);
	}
	
	@GetMapping(value = "/following/{id}") 
	public ResponseListDto<List<MemberDto.Get>> followingList(@PathVariable Long id) {
		List<Member> followings = followRepository.findByFollowing(id);
		List<MemberDto.Get> dtos = followings.stream().map(f-> new MemberDto.Get(f.getUserId(), f.getName())).collect(Collectors.toList());
		
		return new ResponseListDto<List<Get>>(dtos);
	}
	
	@GetMapping(value = "/follower/{id}")
	public ResponseListDto<List<MemberDto.Get>> followerList(@PathVariable Long id) {
		List<Member> followers = followRepository.findByFollower(id);
		List<MemberDto.Get> dtos = followers.stream().map(f-> new MemberDto.Get(f.getUserId(), f.getName())).collect(Collectors.toList());
		
		return new ResponseListDto<List<Get>>(dtos);
	}
}
