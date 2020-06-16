package com.may.app.feed.dto;

import java.io.Serializable;

import com.may.app.feed.entity.Comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CommentDto {
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Get implements Serializable {
		private static final long serialVersionUID = 11919700195740160L;
		private String content;
		
		public Get(Comment comment) {
			this.content=comment.getContent();
		}
	}
}
