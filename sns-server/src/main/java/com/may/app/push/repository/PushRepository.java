package com.may.app.push.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.may.app.push.Push;

public interface PushRepository extends JpaRepository<Push, Long> {
}
