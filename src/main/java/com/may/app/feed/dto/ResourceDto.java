package com.may.app.feed.dto;

import com.may.app.feed.FeedResourceType;
import com.may.app.feed.entity.Resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ResourceDto {
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Get {
		private String path;
	    private FeedResourceType type;
	    
	    public Get(Resource resource) {
	    	this.path = resource.getPath();
	    	this.type = resource.getType();
	    }
	}
	
}
