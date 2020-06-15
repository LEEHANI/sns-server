package com.may.app.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.may.app.common.CreateEntity;
import com.may.app.feed.entity.Feed;
import com.may.app.feed.repository.FeedRepository;
import com.may.app.item.repository.ItemRepository;
import com.may.app.member.dto.MemberDto;
import com.may.app.member.dto.MemberDto.GetInfo;
import com.may.app.member.entity.Member;
import com.may.app.member.repository.MemberRepository;

@SpringBootTest(classes = MemberService.class)
public class MemberServiceTest {
	@Autowired private MemberService memberService;
	@MockBean private MemberRepository memberRepository;
	@MockBean private FeedRepository feedRepository;
	@MockBean private ItemRepository itemRepository;

	Member member1 = CreateEntity.createMember(1L);
	Feed feed1 = CreateEntity.createFeed
			(
				1L, 
				member1, 
				CreateEntity.createResources(1, true), 
				CreateEntity.createComments(1, true), 
				CreateEntity.createItems(1, member1, true), 
				CreateEntity.createTags(0, 1, true)
			);
	Feed feed2 = CreateEntity.createFeed
			(
				2L, 
				member1, 
				CreateEntity.createResources(1, true), 
				CreateEntity.createComments(1, true), 
				CreateEntity.createItems(1, member1, true), 
				CreateEntity.createTags(0, 1, true)
			);
	
	@BeforeEach
	public void setUp() throws Exception {
	}
	
	@Test
	public void 회원_상세_조회_성공() throws Exception {
		//given
		given(memberRepository.findById(member1.getId())).willReturn(Optional.of(member1));
		given(feedRepository.findByMemberId(member1.getId())).willReturn(Lists.newArrayList(feed1, feed2));
		
		//when
		GetInfo result = memberService.detail(member1.getId());
		
		//then
		assertEquals(result.getUserId(), member1.getUserId());
		assertEquals(result.getFeeds().get(0).getId(), feed1.getId());
		assertEquals(result.getFeeds().get(1).getId(), feed2.getId());
	}
	
}
