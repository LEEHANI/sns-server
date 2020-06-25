package com.may.app.feed.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.may.app.feed.dto.FeedDto;
import com.may.app.feed.entity.Feed;
import com.may.app.feed.entity.FeedTag;
import com.may.app.feed.entity.QFeed;
import com.may.app.feed.entity.QFeedItem;
import com.may.app.feed.entity.QFeedTag;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@Transactional
@RequiredArgsConstructor
public class FeedRepositoryCustomImpl implements FeedRepositoryCustom {
	private final JPAQueryFactory query;
	
	@Override
	public Optional<Feed> findDetailById(Long id) {
		QFeed feed = QFeed.feed;
		QFeedItem feedItem = QFeedItem.feedItem;
		
		Feed result = query.select(feed)
		.from(feed)
		.leftJoin(feed.member).fetchJoin()
		.leftJoin(feed.items, feedItem).fetchJoin()
		.leftJoin(feedItem.item).fetchJoin()
		.where(feed.id.eq(id))
		.fetchOne();
		
		return Optional.of(result);
	}

	@Override
	public FeedDto.Get findDetailDtoById(Feed feed) {
		QFeedTag feedTag = QFeedTag.feedTag;
		
		List<FeedTag> feedTags = query.select(feedTag)
				.from(feedTag)
				.leftJoin(feedTag.tag).fetchJoin()
				.where(feedTag.feed.id.eq(feed.getId()))
				.fetch();
		
		return new FeedDto.Get(feed, feedTags);
	}

}
