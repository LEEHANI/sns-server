package com.may.app.tag;

import java.io.Serializable;

import com.may.app.tag.entity.Tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class TagDto {
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Get implements Serializable {
		private static final long serialVersionUID = 1L;
		private String title;
		
		public Get(Tag tag) {
			this.title=tag.getTitle();
		}
	}
}
