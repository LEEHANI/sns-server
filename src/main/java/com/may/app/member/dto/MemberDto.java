package com.may.app.member.dto;

import java.io.Serializable;
import java.util.List;

import com.may.app.feed.dto.FeedDto;
import com.may.app.item.ItemDto;
import com.may.app.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MemberDto {
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class GetInfo implements Serializable {
		private static final long serialVersionUID = 1L;
		private String userId;
		private String name;
		private List<FeedDto.Get> feeds;
		
		public GetInfo(Member member, List<FeedDto.Get> feeds) {
			this.userId=member.getUserId();
			this.name=member.getName();
			this.feeds=feeds;
		}
	}
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Get implements Serializable {
		private static final long serialVersionUID = 1L;
		private String userId;
	    private String name;
	    
	    public Get(Member member) {
	    	this.userId=member.getUserId();
	    	this.name=member.getName();
	    }
	}
}
