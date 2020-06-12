package com.may.app.member;

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
	public static class Get {
		private String userId;
	    private String name;
	    
	    public Get(Member member) {
	    	this.userId=member.getUserId();
	    	this.name=member.getName();
	    }
	}
	
}
