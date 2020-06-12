package com.may.app.tag.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.may.app.tag.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
	List<Tag> findByTitleIn(List<String> titles);
}
