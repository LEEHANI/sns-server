package com.may.app.feed.exception;

public class NoFeedException extends RuntimeException {
	public NoFeedException() {
		super("피드가 없습니다");
	}
}
