package com.may.app.feed.entity;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.may.app.feed.FeedResourceType;
import com.may.app.feed.FeedResourceTypeConverter;

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
@Getter
@Setter(value = AccessLevel.PROTECTED)
@Entity
public class Resource
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NonNull
	@Column(nullable = false, updatable = true, length = 255)
	private String path;
	
	@NonNull
	@Convert(converter = FeedResourceTypeConverter.class)
	@Column
	private FeedResourceType type;
	
	protected Resource(String img) {
		this.path=img;
		this.type=FeedResourceType.PHOTO;
	}
}
