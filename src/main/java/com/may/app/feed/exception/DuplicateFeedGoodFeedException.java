package com.may.app.feed.exception;

public class DuplicateFeedGoodFeedException extends RuntimeException {
	public DuplicateFeedGoodFeedException() {
		super("피드 좋아요 중복입니다");
	}
}
