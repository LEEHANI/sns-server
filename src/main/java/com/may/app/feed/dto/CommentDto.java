package com.may.app.feed.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
		private String memberName;
		private Long parentId;
		
		public Get(Comment comment) {
			this.content=comment.getContent();
			this.memberName=comment.getMember().getName();
			this.parentId=comment.getParent()==null?null:comment.getParent().getId();
		}
	}
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Child implements Serializable {
		private static final long serialVersionUID = 11919700195740160L;
		private String content;
		private String memberName;
		
		public Child(Comment comment) {
			this.content=comment.getContent();
			this.memberName=comment.getMember().getName();
		}
	}
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Parent implements Serializable {
		private static final long serialVersionUID = 11919700195740160L;
		private String content;
		private String memberName;
		private List<Child> children;
		
		public Parent(Comment comment) {
			this.content=comment.getContent();
			this.memberName=comment.getMember().getName();
			this.children = comment.getChildren().stream()
					.map(Child::new).collect(Collectors.toList());
		}
	}
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Post {
		@NotNull
		private Long memberId;
		@NotBlank
		private String content;
		private Long parentId;
		
		public Post(Long memberId, String content) {
			this.memberId = memberId;
			this.content = content;
		}
	}
}
