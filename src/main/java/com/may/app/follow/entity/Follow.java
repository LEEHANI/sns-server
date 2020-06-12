package com.may.app.follow.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.may.app.member.entity.Member;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter(value = AccessLevel.PRIVATE)
@Entity
@Table
(
	uniqueConstraints = 
	@UniqueConstraint(name = "FOLLOWER_FOLLOWING_UNIQUE_KEY", columnNames = {"follower_id", "following_id"})
)
@EqualsAndHashCode
public class Follow 
{
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = true, name = "following_id")
	private Member following;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = true, name = "follower_id")
	private Member follower;
	
	public Follow(Member follower, Member following) {
		this.follower = follower;
		this.following = following;
	}
}
