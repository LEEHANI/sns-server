package com.may.app.feed.exception;

public class NoFeedGoodException extends RuntimeException {
	public NoFeedGoodException() {
		super("좋아요를 누른적이 없습니다.");
	}
}
