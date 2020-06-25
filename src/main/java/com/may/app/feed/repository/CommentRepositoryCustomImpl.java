package com.may.app.feed.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.may.app.feed.entity.Comment;
import com.may.app.feed.entity.Feed;
import com.may.app.feed.entity.QComment;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@Transactional
@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {
	private final JPAQueryFactory query;

	@Override
	public Page<Comment> findComments(Feed feed, Pageable pageable) {
		QComment comment = QComment.comment;
		
		List<Comment> comments = query.select(comment)
		.from(comment)
		.leftJoin(comment.children).fetchJoin()
		.where(comment.parent.isNull().and(comment.feed.id.eq(feed.getId())))
		.orderBy(comment.id.desc())
		.offset(pageable.getOffset())
		.limit(pageable.getPageSize())
		.fetch();
		
		long commentCount = query.select(comment)
				.from(comment)
				.where(comment.parent.isNull().and(comment.feed.id.eq(feed.getId())))
				.fetchCount();
		
		return new PageImpl<>(comments, pageable, commentCount);
	}
	
	
	
}
