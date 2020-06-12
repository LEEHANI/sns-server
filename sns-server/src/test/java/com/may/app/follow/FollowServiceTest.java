package com.may.app.follow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.may.app.common.CreateEntity;
import com.may.app.follow.entity.Follow;
import com.may.app.follow.exception.AlreadyFollowedException;
import com.may.app.follow.exception.NoFollowException;
import com.may.app.follow.repository.FollowRepository;
import com.may.app.member.entity.Member;
import com.may.app.member.exception.NoMemberException;
import com.may.app.member.repository.MemberRepository;

@SpringBootTest(classes = FollowService.class)
public class FollowServiceTest {
	@MockBean private FollowRepository followRepository;
	@MockBean private MemberRepository memberRepository;
	@Autowired private FollowService followService;
	
	Member member1 = CreateEntity.createMember(1L);
	Member member2 = CreateEntity.createMember(2L);
	Member member3 = CreateEntity.createMember(3L);

	Optional<Member> optionalFollower = Optional.of(member1);
	Optional<Member> optionalFollowing = Optional.of(member2);
	
	Follow follow = CreateEntity.createFollow(1L, member1, member2);
	Optional<Follow> followOptional = Optional.of(follow);
	
	@BeforeEach
	public void setUp() throws Exception {
		given(memberRepository.findById(member1.getId())).willReturn(optionalFollower);
		given(memberRepository.findById(member2.getId())).willReturn(optionalFollowing);
	}
	
	/**
	 * save() Test 성공
	 */
	@Test
	public void 팔로우_추가_정상() throws Exception {
		// given
		Follow saveFollow = CreateEntity.createFollow(null, member1, member2);
		given(followRepository.save(saveFollow)).willReturn(follow);
		
		// when
		Long result = followService.follow(member1.getId(), member2.getId());
		
		// then
		assertNotNull(result);
		assertEquals(result, follow.getId());
	}
	
	/**
	 * save() Test 실패
	 * 이미 follow 했을 경우 AlreadyFollowedException이 발생한다.
	 */
	@Test
	public void 팔로우_추가_실패() throws Exception {
		// given
		given(followRepository.findByFollowerAndFollowing(member1.getId(), member2.getId())).willReturn(followOptional);
				
		// when & then
		assertThrows(AlreadyFollowedException.class, ()-> followService.follow(member1.getId(), member2.getId()));
	}
	
	/**
	 * save() Test 실패
	 * 저장되어 있지 않은 회원 일 때는 NoMemberException이 발생한다.
	 */
	@Test
	public void 팔로우_없는_회원_실패() throws Exception {
		// when & then
		assertThrows(NoMemberException.class, ()-> followService.follow(member1.getId(), member3.getId()));
	}
	
	/**
	 * delete() Test 정상
	 * @throws Exception
	 */
	@Test
	public void 언팔로우_정상() throws Exception {
		// given
		given(followRepository.findByFollowerAndFollowing(member1.getId(), member2.getId())).willReturn(followOptional);
		
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
