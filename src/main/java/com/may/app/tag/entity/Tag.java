package com.may.app.tag.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
@Table
(
	uniqueConstraints = 
	@UniqueConstraint(name = "TITLE_UNIQUE_KEY", columnNames = {"title"})
)
public class Tag 
{
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@NonNull
	@Column(nullable = false, updatable = true, length = 20)
	private String title;
	
	public Tag(String title) {
		this.title = title;
	}
}
