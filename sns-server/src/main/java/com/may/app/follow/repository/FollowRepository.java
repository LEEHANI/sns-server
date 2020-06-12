package com.may.app.follow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.may.app.follow.entity.Follow;
import com.may.app.member.entity.Member;

public interface FollowRepository extends JpaRepository<Follow, Long> {
	@Query("SELECT f.following FROM Follow f WHERE f.follower.id=:id")
	List<Member> findByFollowing(@Param(value = "id") Long id);
	
	@Query("SELECT f.follower FROM Follow f WHERE f.following.id=:id")
	List<Member> findByFollower(@Param(value = "id") Long id);
	
	@Query("SELECT f FROM Follow f WHERE f.follower.id=:memberId AND f.following.id=:unfollowId")
	Optional<Follow> findByFollowerAndFollowing(@Param(value = "memberId") Long followerId, @Param(value = "unfollowId") Long unfollowId);
}
