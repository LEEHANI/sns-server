package com.may.app.feed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.may.app.feed.entity.Comment;
import com.may.app.feed.entity.Feed;
import com.may.app.feed.exception.NoCommentException;
import com.may.app.feed.exception.NoFeedException;
import com.may.app.feed.repository.CommentRepository;
import com.may.app.feed.repository.FeedRepository;
import com.may.app.member.entity.Member;
import com.may.app.member.exception.NoMemberException;
import com.may.app.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final FeedRepository feedRepository;
	private final MemberRepository memberRepository;
	
	@Transactional(readOnly = true)
	public Page<Comment> comments(Long feedId, Pageable pageable) {
		Feed feed = feedRepository.findById(feedId).orElseThrow(()-> new NoFeedException());
		
		Page<Comment> findComments = commentRepository.findComments(feed, pageable);
		
		return findComments;
	}
	
	@Transactional
	public Comment add(Long id, Long memberId, String content, Long parentId) {
		Feed feed = feedRepository.findById(id).orElseThrow(()-> new NoFeedException());
		Member member = memberRepository.findById(memberId).orElseThrow(()-> new NoMemberException());
		
		if(parentId==null) {
			return commentRepository.save(new Comment(content, member, feed));
		}
		else {
			Comment parent = commentRepository.findById(parentId).orElseThrow(()-> new NoCommentException());
			return commentRepository.save(new Comment(content, member, feed, parent));
		}
	}
}
