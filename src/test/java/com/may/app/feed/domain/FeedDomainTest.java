package com.may.app.feed.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import com.may.app.common.CreateEntity;
import com.may.app.feed.entity.Comment;
import com.may.app.feed.entity.Feed;
import com.may.app.feed.entity.Resource;
import com.may.app.feed.exception.FeedOwnerMismatchException;
import com.may.app.item.entity.Item;
import com.may.app.member.entity.Member;
import com.may.app.tag.entity.Tag;

public class FeedDomainTest {
	
	Member member1 = CreateEntity.createMember(1L);
	Member member2 = CreateEntity.createMember(2L);
	List<Resource> resources = CreateEntity.createResources(2, true);
	List<Comment> comments = CreateEntity.createComments(2, true);
	List<Item> items = CreateEntity.createItems(2, member1, true);
	List<Tag> tags = CreateEntity.createTags(0, 2, true);
	
	Feed feed1 = CreateEntity.createFeed(1L, member1, resources, comments, items, tags);
	
	@Test
	public void 두개_리스트를_한쪽으로_필터링() throws Exception {
		//given
		List<Integer> list1 = Lists.newArrayList(1,2,3);
		List<Integer> list2 = Lists.newArrayList(3,4,5);
		
		//then
		List<Integer> list3 = list1.stream().filter(o->!list2.contains(o)).collect(Collectors.toList());
		List<Integer> list4 = list2.stream().filter(o->!list1.contains(o)).collect(Collectors.toList());
		
		//when
		assertEquals(list3.size(), 2);
		assertEquals(list4.size(), 2);
	}
	
	@Test
	public void 피드_수정_성공() throws Exception {
		//given
		String content = "새로운 피드~";
		List<String> editImgs = Lists.newArrayList(resources.get(0).getPath());
		List<Tag> editTags = Lists.newArrayList(tags.get(0), tags.get(1), CreateEntity.createTag(100L)); 
		List<Item> editItems = Lists.newArrayList(items.get(0), CreateEntity.createItem(101L, member1));
		
		//when
		feed1.edit(content, editImgs, editTags, editItems);
		
		//then
		assertEquals(feed1.getContent(), content);
		assertEquals(feed1.getResources().size(), editImgs.size());
		assertEquals(feed1.getResources().get(0).getPath(), editImgs.get(0));
		assertEquals(feed1.getTags().size(), editTags.size());
		assertEquals(feed1.getTags().get(2).getTag().getTitle(), editTags.get(2).getTitle());
		assertEquals(feed1.getItems().size(), editItems.size());
	}
	
	@Test
	public void 피드_주인_확인_성공() throws Exception {
		//when & then
		assertDoesNotThrow(() -> feed1.isOwner(member1.getId()));
	}
	
	@Test
	public void 피드_주인_확인_실패() throws Exception {
		//when & then
		assertThrows(FeedOwnerMismatchException.class, ()-> feed1.isOwner(member2.getId()));
	}
}
