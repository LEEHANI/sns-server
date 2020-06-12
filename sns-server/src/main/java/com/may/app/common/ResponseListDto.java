package com.may.app.common;

import lombok.Data;

@Data
public class ResponseListDto<T> {
	private T data;
	public ResponseListDto(T t) {
		this.data = t;
	}
}
