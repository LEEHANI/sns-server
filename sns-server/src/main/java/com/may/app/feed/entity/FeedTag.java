package com.may.app.feed.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.may.app.tag.entity.Tag;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter(value = AccessLevel.PROTECTED)
@Table
(
	uniqueConstraints = 
	{
		@UniqueConstraint(name = "UNIQ_FEED_ID__TAG_ID_IN_FEED_TAG", columnNames = { "feed_id", "tag_id" })
	}
)
@Entity
public class FeedTag
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = true, name = "feed_id")
	private Feed feed;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = true, name = "tag_id")
	private Tag tag;
	
	public FeedTag(Feed feed, Tag tag) {
		this.feed=feed;
		this.tag=tag;
	}
}
