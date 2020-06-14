package com.may.app.follow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.may.app.common.CreateEntity;
import com.may.app.follow.entity.Follow;
import com.may.app.follow.exception.AlreadyFollowedException;
import com.may.app.follow.exception.NoFollowException;
import com.may.app.member.MemoryMemberRepository;
import com.may.app.member.entity.Member;
import com.may.app.member.exception.NoMemberException;

public class FollowServiceTest {
	private FollowService followService;
	private MemoryMemberRepository memberRepository = new MemoryMemberRepository();
	private MemoryFollowRepository followRepository = new MemoryFollowRepository();
	
	Member member1;
	Member member2;
	Follow follow;
	
	@BeforeEach
	public void setUp() throws Exception {
		followService = new FollowService(followRepository, memberRepository);
		member1=memberRepository.save(CreateEntity.createMember(1L));
		member2=memberRepository.save(CreateEntity.createMember(2L));
		follow = CreateEntity.createFollow(1L, member1, member2);
	}
	
	/**
	 * save() Test 성공
	 */
	@Test
	public void 팔로우_추가_정상() throws Exception {
		// when
		Long result = followService.follow(member1.getId(), member2.getId());
		
		// then
		assertNotNull(result);
	}
	
	/**
	 * save() Test 실패
	 * 이미 follow 했을 경우 AlreadyFollowedException이 발생한다.
	 */
	@Test
	public void 팔로우_추가_실패() throws Exception {
		// given
		followRepository.save(follow);
		
		// when & then
		assertThrows(AlreadyFollowedException.class, ()-> followService.follow(member1.getId(), member2.getId()));
	}
	
	/**
	 * save() Test 실패
	 * 저장되어 있지 않은 회원 일 때는 NoMemberException이 발생한다.
	 */
	@Order(value = 0)
	@Test
	public void 팔로우_없는_회원_실패() throws Exception {
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
		followRepository.save(follow);
		
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
		// when & then
		assertThrows(NoFollowException.class, ()->followService.unfollow(member1.getId(), member2.getId()));
	}
}
