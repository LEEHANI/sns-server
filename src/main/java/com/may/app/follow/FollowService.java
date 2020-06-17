package com.may.app.follow;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.may.app.follow.entity.Follow;
import com.may.app.follow.exception.DuplicateFollowException;
import com.may.app.follow.exception.NoFollowException;
import com.may.app.follow.repository.FollowRepository;
import com.may.app.member.entity.Member;
import com.may.app.member.exception.NoMemberException;
import com.may.app.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {
	private final FollowRepository followRepository;
	private final MemberRepository memberRepository;
	
	@Transactional
	public Long follow(Long followerId, Long followingId) {
		Member follower = memberRepository.findById(followerId).orElseThrow(()->new NoMemberException());
		Member following = memberRepository.findById(followingId).orElseThrow(()->new NoMemberException());
		
		Follow follow = followRepository.findByFollowerAndFollowing(followerId, followingId).orElse(null);
		
		if(follow!=null) throw new DuplicateFollowException();
		
		Follow result = followRepository.save(new Follow(follower, following));
		
		return result.getId();
	}

	@Transactional
	public Long unfollow(Long memberId, Long unfollowId) {
		Follow follow = followRepository.findByFollowerAndFollowing(memberId, unfollowId).orElseThrow(()->new NoFollowException());
		
		followRepository.delete(follow);
		
		return follow.getId();
	}
}
