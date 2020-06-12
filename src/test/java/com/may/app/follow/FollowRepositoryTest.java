package com.may.app.follow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import com.may.app.common.CreateEntity;
import com.may.app.follow.entity.Follow;
import com.may.app.follow.repository.FollowRepository;
import com.may.app.member.entity.Member;
import com.may.app.member.repository.MemberRepository;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
public class FollowRepositoryTest {
	@Autowired
	private FollowRepository followRepository;
	@Autowired
	private MemberRepository memberRepository;
	
	Member member1 = CreateEntity.createMember(null);
	Member member2 = CreateEntity.createMember(null);
	Member member3 = CreateEntity.createMember(null);
	Member member4 = CreateEntity.createMember(null);
	
	Follow follow1 = CreateEntity.createFollow(null, member1, member2);
	Follow follow2 = CreateEntity.createFollow(null, member1, member3);
	Follow follow3 = CreateEntity.createFollow(null, member1, member4);
	
	@BeforeEach
	public void setUp() throws Exception {
		memberRepository.save(member1);
		memberRepository.save(member2);
		memberRepository.save(member3);
		memberRepository.save(member4);
		
		followRepository.save(follow1);
		followRepository.save(follow2);
		followRepository.save(follow3);
	}
	
	/**
	 * save() Test
	 * 정상작동
	 * @throws Exception
	 */
	@DisplayName("following 정상작동 테스트")
	@Test
	public void 팔로잉조회_정상() throws Exception {
		//when
		List<Member> result = followRepository.findByFollowing(member1.getId());
		
		//then
		assertNotNull(result);
		assertEquals(result.size(), 3);
		assertEquals(result.get(0).getId(), member2.getId());
		assertEquals(result.get(1).getName(), member3.getName());
		assertEquals(result.get(2).getUserId(), member4.getUserId());
	}
	
	/**
	 * save() Test
	 * 이미 팔로우한 상대를 다시 팔로우할 때는 DataIntegrityViolationException이 발생한다.
	 * @throws Exception
	 */
	@Test
	public void 팔로우_중복_추가_테스트_예외발생() throws Exception{
		//when & then
		assertThrows(DataIntegrityViolationException.class, ()-> followRepository.save(CreateEntity.createFollow(null, member1, member4)));
	}
	
	/**
	 * find() Test
	 * follower와 following으로 follow를 정상 조회한다. 
	 * @throws Exception
	 */
	@Test
	public void 팔로우_follower_following_조회_정상() throws Exception {
		//when
		Follow result = followRepository.findByFollowerAndFollowing(member1.getId(), member2.getId()).orElse(null);
		
		//then
		assertNotNull(result);
		assertEquals(result.getId(), follow1.getId());
		assertEquals(result.getFollower(), member1);
		assertEquals(result.getFollowing(), member2);
	}
	
}
