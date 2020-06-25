package com.may.app.feed.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.may.app.feed.entity.Comment;
import com.may.app.feed.entity.Feed;

public interface CommentRepositoryCustom {

	Page<Comment> findComments(Feed feed, Pageable pageable);
}
