package com.may.app.member.exception;

public class NoMemberException extends RuntimeException {
	public NoMemberException() {
		super("회원이 없습니다");
	}
}
