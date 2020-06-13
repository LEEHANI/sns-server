package com.may.app.member.entity;

import javax.persistence.Embeddable;

import com.may.app.member.PasswordStrength;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Embeddable
public class Password {
    private String password;
    
    public Password(String password) {
    	PasswordStrength result = passwordValidation(password);
    	
    	if(result.pass()) 
    		this.password=password;
    }
    
    public PasswordStrength passwordValidation(String string) {
		if(string==null || string.isEmpty()) return PasswordStrength.INVALID;
		int metCounts = getMetCriteriaCounts(string);
		
		if(metCounts <= 1) return PasswordStrength.WEAK;
		if(metCounts == 2) return PasswordStrength.NORMAL;
		
		return PasswordStrength.STRONG;
	}
	
	private int getMetCriteriaCounts(String s) {
		int metCounts = 0;
		
		if(s.length() >= 8) metCounts++;
		if(meetsConainingNumberCriteria(s)) metCounts++;
		if(meetsContainingUppercaseCriteria(s)) metCounts++;
		
		return metCounts;
	}
	
	private boolean meetsContainingUppercaseCriteria(String s) {
		for(char ch:s.toCharArray()) {
			if(Character.isUpperCase(ch)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean meetsConainingNumberCriteria(String s) {
		for(char ch:s.toCharArray()) {
			if(ch >= '0' && ch<='9') {
				return true;
			}
		}
		return false;
	}
}
