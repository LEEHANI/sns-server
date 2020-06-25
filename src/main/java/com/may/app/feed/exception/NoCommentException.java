package com.may.app.feed.exception;

public class NoCommentException extends RuntimeException {
	private static final long serialVersionUID = -1714749852451741754L;

	public NoCommentException() {
		super("댓글이 없습니다");
	}
}
