package com.may.app.feed.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.may.app.member.entity.Member;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter(value = AccessLevel.PROTECTED)
@Getter
@Entity
public class Comment
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = true, updatable = true, length = 300)
	private String content;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Comment parent;
	
	@Default
	@OneToMany(mappedBy = "parent")
	private List<Comment> children = new ArrayList<>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = true, name = "member_id")
	private Member member;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = true, name = "feed_id")
	private Feed feed;
	
	public Comment(String content, Member member, Feed feed, Comment parent) {
		this(content, member, feed);
		this.parent=parent;
	}
	
	public Comment(String content, Member member, Feed feed) {
		this.content = content;
		this.member = member;
		this.feed = feed;
	}
}
