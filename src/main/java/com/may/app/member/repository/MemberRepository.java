package com.may.app.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.may.app.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
//	Optional<Member> findById(Long id);
	
}
