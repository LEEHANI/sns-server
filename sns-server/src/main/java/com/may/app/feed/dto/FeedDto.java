package com.may.app.feed.dto;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.may.app.feed.entity.Feed;
import com.may.app.item.ItemDto;
import com.may.app.member.MemberDto;
import com.may.app.tag.TagDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class FeedDto {
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Post {
		@NotBlank
		private String content;
		@NotNull
	    private Long memberId;
	    private List<String> imgs;
	    private List<String> comments;
	    private List<String> tags;
	    private List<Long> itemIds;
	}
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Put {
		@NotBlank
		private String content;
		@NotNull
		private Long memberId;
	    private List<String> imgs;
	    private List<String> tags;
	    private List<Long> itemIds;
	}
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Get {
		private String content;
	    private MemberDto.Get member;
	    private List<ResourceDto.Get> imgs;
	    private List<CommentDto.Get> comments;
	    private List<TagDto.Get> tags;
	    private List<ItemDto.Get> items;
	    
	    public Get(Feed feed) {
	    	this.content=feed.getContent();
	    	this.member=new MemberDto.Get(feed.getMember());
	    	this.imgs=feed.getResources().stream().map(ResourceDto.Get::new).collect(Collectors.toList());
	    	this.comments=feed.getComments().stream().map(CommentDto.Get::new).collect(Collectors.toList());
	    	this.tags=feed.getTags().stream().map(f->new TagDto.Get(f.getTag())).collect(Collectors.toList());
	    	this.items=feed.getItems().stream().map(f->new ItemDto.Get(f.getItem())).collect(Collectors.toList());
	    }
	}
}
