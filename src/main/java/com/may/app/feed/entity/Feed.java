package com.may.app.feed.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.BatchSize;

import com.may.app.feed.exception.FeedOwnerMismatchException;
import com.may.app.item.entity.Item;
import com.may.app.member.entity.Member;
import com.may.app.tag.entity.Tag;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter(value = AccessLevel.PROTECTED)
@Entity
@EqualsAndHashCode
public class Feed
{
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Lob
	@Column(nullable = true, updatable = true, length = 500)
	private String content;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = true, name = "member_id")
	private Member member;
	
	@Default
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "feed_id")
	private List<Resource> resources = new ArrayList<>();
	
	@Default
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "feed")
	private List<FeedItem> items = new ArrayList<>();
	
	@Default
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "feed")
	private List<FeedTag> tags = new ArrayList<>();
	
//	@Default
//	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,  orphanRemoval = true)
//	@JoinColumn(name = "feed_id")
//	private List<Comment> comments = new ArrayList<>();
	
	public static Feed createFeed 
	(
		String content, 
		Member member, 
		List<String> imgDtos, 
		List<Tag> tags, 
		List<Item> items
	) {
		Feed feed = new Feed();
		
		List<Resource> imgResult = imgDtos.stream().map(r-> new Resource(r)).collect(Collectors.toList());
		List<FeedTag> tagResult = tags.stream().map(t-> new FeedTag(feed, t)).collect(Collectors.toList());
		List<FeedItem> itemResult = items.stream().map(i-> new FeedItem(feed, i)).collect(Collectors.toList());
		
		feed.content=content;
		feed.member=member;
		feed.resources=imgResult;
		feed.tags=tagResult;
		feed.items=itemResult;
		
		return feed;
	}
	
	public void edit 
	(
		String content, 
		List<String> imgDtos, 
		List<Tag> tags, 
		List<Item> items
	) {
		editResources(imgDtos);
		editTags(tags);
		editItems(items);
		
		setContent(content);
	}
	
	public void setTest(List<FeedTag> list) {
		setTags(list);
	}

	private void editTags(List<Tag> tags) { 
		//삭제
		List<FeedTag> removeTags = getTags().stream().filter(o->!tags.contains(o.getTag())).collect(Collectors.toList());
		removeTags.forEach(o->unbind(o));
		
		//추가
		List<Tag> containTags = getTags().stream().map(o->o.getTag()).collect(Collectors.toList());
		List<FeedTag> addTags = tags.stream().filter(o->!containTags.contains(o)).map(o-> new FeedTag(this, o)).collect(Collectors.toList());
		addTags.stream().collect(Collectors.toSet()).forEach(o->bind(o));
	}
	
	private void editResources(List<String> imgs) { 
		List<Resource> resources = getResources();
		List<Resource> removeResources = resources.stream().filter(o->!imgs.contains(o.getPath())).collect(Collectors.toList());
		this.resources.removeAll(removeResources);
		
		Set<Resource> addResources = imgs.stream().filter(o-> !getResources().contains(o)).map(Resource::new).collect(Collectors.toSet());
		getResources().stream().collect(Collectors.toSet()).addAll(addResources);
		if(getResources().isEmpty()) setResources(addResources.stream().collect(Collectors.toList()));
	}
	
	private void editItems(List<Item> items) {
		//삭제
		Set<FeedItem> removeItems = getItems().stream().filter(o->!items.contains(o.getItem())).collect(Collectors.toSet());
		removeItems.forEach(o->unbind(o));
		
		//추가
		Set<Item> containItems = getItems().stream().map(o->o.getItem()).collect(Collectors.toSet());
		Set<FeedItem> addItems = items.stream().filter(o->!containItems.contains(o)).map(i-> new FeedItem(this, i)).collect(Collectors.toSet());
		addItems.stream().collect(Collectors.toSet()).forEach(o->bind(o));
	}
	
	public void setContent(String content) {
		if(content!=null) this.content = content;
	}
	
	public void isOwner(Long memberId) {
		if(!member.getId().equals(memberId)) throw new FeedOwnerMismatchException(); 
	}

	public void bind(FeedItem item)
	{
		items.add(item);
		item.setFeed(this);
	}
	
	public void unbind(FeedItem item)
	{
		items.remove(item);
		item.setFeed(null);
	}
	
	public void bind(FeedTag tag)
	{
		tags.add(tag);
		tag.setFeed(this);
	}
	
	public void unbind(FeedTag tag)
	{
		tags.remove(tag);
		tag.setFeed(null);
	}
}
