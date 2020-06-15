package com.may.app.feed.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.may.app.feed.entity.Feed;

public interface FeedRepository extends JpaRepository<Feed, Long> {
	@Query(value = "SELECT f FROM Feed f JOIN f.member m ON m.id=:memberId WHERE f.id=:id")
	Optional<Feed> findByIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId);
	
	@EntityGraph(attributePaths = {"member"})
	Optional<Feed> findFetchMemberById(Long id);
	
	@EntityGraph(attributePaths = {"member"})
	Page<Feed> findEntityGraphBy(Pageable pageable);
	
	List<Feed> findByMemberId(Long id);
}
