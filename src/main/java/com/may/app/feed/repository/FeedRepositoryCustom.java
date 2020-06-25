package com.may.app.feed.repository;

import java.util.Optional;

import com.may.app.feed.dto.FeedDto;
import com.may.app.feed.entity.Feed;

public interface FeedRepositoryCustom {
	Optional<Feed> findDetailById(Long id);
	FeedDto.Get findDetailDtoById(Feed feed);
}
