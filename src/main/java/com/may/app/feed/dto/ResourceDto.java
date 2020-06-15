package com.may.app.feed.dto;

import java.io.Serializable;

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
	public static class Get implements Serializable {
		private static final long serialVersionUID = 1L;
		private String path;
	    private FeedResourceType type;
	    
	    public Get(Resource resource) {
	    	this.path = resource.getPath();
	    	this.type = resource.getType();
	    }
	}
	
}
