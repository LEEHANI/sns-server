package com.may.app.member;

public enum PasswordStrength {
	STRONG, NORMAL, WEAK, INVALID;
	
	public boolean pass() {
		if(PasswordStrength.STRONG.equals(this) || PasswordStrength.NORMAL.equals(this)) return true;
		
		return false;
	}
}
