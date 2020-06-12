package com.may.app.item.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
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

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Getter
public class Item {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String title;

	@Column
	private String content;
	
	public Item(String title, String content, Member member) {
		this.title=title;
		this.content=content;
		this.member = member;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = true, name = "member_id")
	private Member member;
}
