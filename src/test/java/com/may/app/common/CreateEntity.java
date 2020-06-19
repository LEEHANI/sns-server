package com.may.app.common;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.assertj.core.util.Lists;

import com.may.app.feed.FeedResourceType;
import com.may.app.feed.entity.Comment;
import com.may.app.feed.entity.Feed;
import com.may.app.feed.entity.FeedItem;
import com.may.app.feed.entity.FeedTag;
import com.may.app.feed.entity.Resource;
import com.may.app.follow.entity.Follow;
import com.may.app.item.entity.Item;
import com.may.app.member.entity.Member;
import com.may.app.tag.entity.Tag;

/**
 * 테스트할때만 Builder 사용 
 * @author hani
 *
 */
public class CreateEntity {
	
	static int count = 1;
	
	public static Member createMember(Long id) {
		return Member.builder()
				.id(id)
				.userId("멤버"+(id==null?count:id))
				.name("멤버이름"+(id==null?count++:id))
				.blocked(true)
				.build();
	}
	
	public static Follow createFollow(Long id, Member follower, Member following) {
		return Follow.builder()
				.id(id)
				.follower(follower)
				.following(following)
				.build();
	}
	
	public static Resource createResource(Long id) {
		return Resource.builder()
				.id(id)
				.path("경로"+(id==null?count++:id))
				.type(FeedResourceType.PHOTO)
				.build();
	}
	
	public static Feed createFeed
	(
		Long id, 
		Member member, 
		List<Resource> resources,
		List<Item> items,
		List<Tag> tags
	) {
		
		Feed feed = Feed.builder()
				.id(id)
				.content("피드내용"+(id==null?count:id))
				.member(member)
				.resources(resources==null ? new ArrayList<>() : resources)
				.build();
		
		if(items==null) items=Lists.newArrayList(); else items.forEach(i-> feed.bind(FeedItem.builder().item(i).build()));
		if(tags == null) items=Lists.newArrayList(); else tags.forEach(t-> feed.bind(FeedTag.builder().tag(t).build()));
				
		return feed;
	}
	
	public static Feed createFeed
	(
		Long id, 
		Member member, 
		int resourceCount, 
		int itemCount, 
		int tagCount
	) {
		Feed feed = Feed.builder()
				.id(id)
				.content("피드내용"+(id==null?count:id))
				.member(member)
				.resources(createResources(resourceCount, true))
				.build();
		createItems(itemCount, member, true).forEach(i-> feed.bind(FeedItem.builder().item(i).build()));
		createTags(0, tagCount, true).forEach(t-> feed.bind(FeedTag.builder().tag(t).build()));
		
		return feed;
	}
	
	public static Tag createTag(Long id) {
		return Tag.builder()
				.id(id)
				.title(UUID.randomUUID().toString())
				.build();
	}
	
	public static Item createItem(Long id, Member member) {
		return Item.builder()
				.id(id)
				.title("상품제목"+(id==null?count++:id))
				.content("상품내용"+(id==null?count++:id))
				.member(member)
				.build();
	}
	
	public static Comment createComment(Long id) {
		return Comment.builder()
				.content("댓글내용"+(id==null?count:id))
				.build();
	}
	
	// 컨트롤러, 서비스에서는 id를 사용하는 경우가 많음
	// 레파지토리에서는 id를 사용하지 않는 경우가 많음
	public static List<Comment> createComments(int count, boolean needId) {
		List<Comment> entities = new ArrayList<>();
		
		for(int i=0; i<count; i++) {
			entities.add(createComment(needId?Long.valueOf(i):null));
		}
		
		return entities;
	}
	
	public static List<Resource> createResources(int count, boolean needId) {
		List<Resource> entities = new ArrayList<>();
		
		for(int i=0; i<count; i++) {
			entities.add(createResource(needId?Long.valueOf(i):null));
		}
		
		return entities;
	}

	public static List<Item> createItems(int count, Member member, boolean needId) {
		List<Item> entities = new ArrayList<>();
		
		for(int i=0; i<count; i++) {
			entities.add(createItem(needId?Long.valueOf(i):null, member));
		}
		return entities;
	}
	
	public static List<Tag> createTags(int start, int end, boolean needId) {
		List<Tag> entities = new ArrayList<>();
		
		for(int i=start; i<end; i++) {
			entities.add(createTag(needId?Long.valueOf(i):null));
		}
		return entities;
	}
}
