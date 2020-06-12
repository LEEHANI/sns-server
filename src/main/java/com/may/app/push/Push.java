package com.may.app.push;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.may.app.member.entity.Member;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter(value = AccessLevel.PROTECTED)
@Getter
@Entity
public class Push 
{
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@NonNull
	@Column
	private String content;
	
	@ManyToOne
	@JoinColumn(nullable = false, updatable = true, name = "member_id")
	private Member member;
	
	public Push(String content, Member member) {
		this.content = content;
		this.member = member;
	}
}
