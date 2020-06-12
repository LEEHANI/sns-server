package com.may.app.item;

import java.util.List;

import com.may.app.item.entity.Item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ItemDto {
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Post {
		private String title;
		private String content;
	}
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Get {
		private String title;
		private String content;
		
		public Get(Item item) {
			this.title=item.getTitle();
			this.content=item.getContent();
		}
	}
	
}
