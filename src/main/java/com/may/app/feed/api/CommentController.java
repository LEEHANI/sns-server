package com.may.app.feed.api;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.may.app.feed.CommentService;
import com.may.app.feed.FeedService;
import com.may.app.feed.dto.CommentDto;
import com.may.app.feed.entity.Comment;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/feed")
@RequiredArgsConstructor
public class CommentController {
	private final CommentService commentService;
	
	@PostMapping(value = "/{id}/comment")
	public CommentDto.Get add
	(
		@PathVariable Long id, 
		@RequestBody @Valid CommentDto.Post request, 
		BindingResult errors
	) {
		if(errors.hasErrors()) {
			throw new IllegalArgumentException(errors.getAllErrors().get(0).getDefaultMessage());
		}
		
		Comment entity = commentService.add(id, request.getMemberId(), request.getContent(), request.getParentId());
		
		return new CommentDto.Get(entity);
	}

	
	@GetMapping(value = "/{id}/comments")
	public Page<CommentDto.Parent> comments(@PathVariable(name = "id") Long feedId, Pageable pageable) {
		Page<Comment> comments = commentService.comments(feedId, pageable);
		
		return comments.map(CommentDto.Parent::new);
	}
}
