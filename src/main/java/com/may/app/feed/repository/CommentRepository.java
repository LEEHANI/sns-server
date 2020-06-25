package com.may.app.feed.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.may.app.feed.entity.Comment;
import com.may.app.feed.entity.Feed;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
	Page<Comment> findByFeed(Feed feed, Pageable pageable);
}
