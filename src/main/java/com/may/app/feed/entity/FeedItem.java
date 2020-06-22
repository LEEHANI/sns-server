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

import org.hibernate.annotations.BatchSize;

import com.may.app.item.entity.Item;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter(value = AccessLevel.PROTECTED)
@Getter
@Table
(
	uniqueConstraints = 
	{
		@UniqueConstraint(name = "UNIQ_FEED_ID__ITEM_ID_IN_FEED_ITEM", columnNames = { "feed_id", "item_id" })
	}
)
@Entity
public class FeedItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = true, name = "feed_id")
	private Feed feed;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = true, name = "item_id")
	private Item item;
	
	public FeedItem(Feed feed, Item item) {
		this.feed=feed;
		this.item=item;
	}
}
