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
	
	@Test
	public void 피드_수정_성공() throws Exception {
		//given
		Member member1 = CreateEntity.createMember(1L);
		List<Resource> resources = CreateEntity.createResources(2, true);
		List<Item> items = CreateEntity.createItems(2, member1, true);
		List<Tag> tags = CreateEntity.createTags(0, 2, true);
		
		Feed feed1 = CreateEntity.createFeed(1L, member1, resources, items, tags);
		
		String content = "새로운 피드~";
		List<String> editImgs = Lists.newArrayList(resources.get(0).getPath());
		List<Tag> editTags = Lists.newArrayList(tags.get(0), tags.get(1), CreateEntity.createTag(100L)); 
		List<Item> editItems = CreateEntity.createItems(1, member1, true);
		
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
	public void 피드_이미지만_수정_성공() throws Exception {
		//given
		Member member1 = CreateEntity.createMember(1L);
		List<Resource> resources = CreateEntity.createResources(2, true);
		
		Feed feed1 = CreateEntity.createFeed(1L, member1, resources, null, null);
		List<String> editImgs = Lists.newArrayList("new img");
		
		//when
		feed1.edit("새로운 피드~", editImgs, Lists.newArrayList(), Lists.newArrayList());
		
		//then
		assertEquals(feed1.getResources().size(), editImgs.size());
		assertEquals(feed1.getResources().get(0).getPath(), editImgs.get(0));
	}
	
	@Test
	public void 피드_태그만_수정_성공() throws Exception {
		//given
		Member member1 = CreateEntity.createMember(1L);
		List<Tag> tags = CreateEntity.createTags(0, 2, true);
		
		Feed feed1 = CreateEntity.createFeed(1L, member1, null, null, tags);
		List<Tag> editTags = Lists.newArrayList(tags.get(1), CreateEntity.createTag(10L));
		
		//when
		feed1.edit("새로운 피드~", Lists.newArrayList(), editTags, Lists.newArrayList());
		
		//then
		assertEquals(feed1.getTags().size(), editTags.size());
		assertEquals(feed1.getTags().get(0).getTag().getId(), editTags.get(0).getId());
		assertEquals(feed1.getTags().get(1).getTag().getId(), editTags.get(1).getId());
	}
	
	@Test
	public void 피드_상품만_수정_성공() throws Exception {
		//given
		Member member1 = CreateEntity.createMember(1L);
		List<Item> items = CreateEntity.createItems(2, member1, true);
		
		Feed feed1 = CreateEntity.createFeed(1L, member1, null, items, null);
		
		//when
		feed1.edit("새로운 피드~", Lists.newArrayList(), Lists.newArrayList(), items);
		
		//then
		assertEquals(feed1.getItems().size(), items.size());
		assertEquals(feed1.getItems().get(0).getItem().getId(), items.get(0).getId());
		assertEquals(feed1.getItems().get(1).getItem().getId(), items.get(1).getId());
	}
	
	@Test
	public void 피드_주인_확인_성공() throws Exception {
		//given
		Member member1 = CreateEntity.createMember(1L);
		Feed feed1 = CreateEntity.createFeed(1L, member1, 0,0,0);
		
		//when & then
		assertDoesNotThrow(() -> feed1.isOwner(member1.getId()));
	}
	
	@Test
	public void 피드_주인_확인_실패() throws Exception {
		//given
		Member member1 = CreateEntity.createMember(1L);
		Feed feed1 = CreateEntity.createFeed(1L, member1, 0,0,0);
		
		//when & then
		assertThrows(FeedOwnerMismatchException.class, ()-> feed1.isOwner(10L));
	}
}
