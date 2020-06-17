package com.may.app.feed.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class GoodDto {
	private Boolean isGood;
	private Long goodCount;
	
	public GoodDto(Boolean isGood, Long goodCount) {
		this.isGood=isGood;
		this.goodCount=goodCount;
	}
}
