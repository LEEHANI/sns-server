package com.may.app.event;

import com.may.app.feed.entity.Feed;
import com.may.app.member.entity.Member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class FollowerPushedEvent {
	private Feed feed;
	
	public FollowerPushedEvent(Feed feed) {
		this.feed=feed;
	}
}
