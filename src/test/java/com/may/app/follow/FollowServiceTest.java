package com.may.app.follow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.may.app.common.CreateEntity;
import com.may.app.follow.entity.Follow;
import com.may.app.follow.exception.DuplicateFollowException;
import com.may.app.follow.exception.NoFollowException;
import com.may.app.member.MemoryMemberRepository;
import com.may.app.member.entity.Member;
import com.may.app.member.exception.NoMemberException;

public class FollowServiceTest {
	private FollowService followService;
	private MemoryMemberRepository memberRepository = new MemoryMemberRepository();
	private MemoryFollowRepository followRepository = new MemoryFollowRepository();
	
	@BeforeEach
	public void setUp() throws Exception {
		followService = new FollowService(followRepository, memberRepository);
	}
	
	/**
	 * save() Test 성공
	 */
	@Test
	public void 팔로우_추가_정상() throws Exception {
		//given
		Member member1=memberRepository.save(CreateEntity.createMember(1L));
		Member member2=memberRepository.save(CreateEntity.createMember(2L));
		
		//when
		Long result = followService.follow(member1.getId(), member2.getId());
		
		//then
		assertNotNull(result);
	}
	
	/**
	 * save() Test 실패
	 * 이미 follow 했을 경우 AlreadyFollowedException이 발생한다.
	 */
	@Test
	public void 팔로우_추가_실패() throws Exception {
		// given
		Member member1=memberRepository.save(CreateEntity.createMember(1L));
		Member member2=memberRepository.save(CreateEntity.createMember(2L));
		
		followRepository.save(CreateEntity.createFollow(1L, member1, member2));
		
		// when & then
		assertThrows(DuplicateFollowException.class, ()-> followService.follow(member1.getId(), member2.getId()));
	}
	
	/**
	 * save() Test 실패
	 * 저장되어 있지 않은 회원 일 때는 NoMemberException이 발생한다.
	 */
	@Order(value = 0)
	@Test
	public void 팔로우_없는_회원_실패() throws Exception {
		//given
		Member member1=memberRepository.save(CreateEntity.createMember(1L));
		
		// when & then
		assertThrows(NoMemberException.class, ()-> followService.follow(member1.getId(), 3L));
	}
	
	/**
	 * delete() Test 정상
	 * @throws Exception
	 */
	@Test
	public void 언팔로우_정상() throws Exception {
		//given
		Member member1=memberRepository.save(CreateEntity.createMember(1L));
		Member member2=memberRepository.save(CreateEntity.createMember(2L));
		Follow follow = followRepository.save(CreateEntity.createFollow(1L, member1, member2));
		
		// when
		Long result = followService.unfollow(member1.getId(), member2.getId());
		
		// then
		assertNotNull(result);
		assertEquals(result, follow.getId());
	}
	
	/**
	 * delete() Test 실패
	 * 저장되어 있지 않은 follow 일 때는 NoFollowException이 발생한다.
	 * @throws Exception
	 */
	@Test
	public void 언팔로우_실패() throws Exception {
		//given
		Member member1=memberRepository.save(CreateEntity.createMember(1L));
		Member member2=memberRepository.save(CreateEntity.createMember(2L));
		
		// when & then
		assertThrows(NoFollowException.class, ()->followService.unfollow(member1.getId(), member2.getId()));
	}
}
