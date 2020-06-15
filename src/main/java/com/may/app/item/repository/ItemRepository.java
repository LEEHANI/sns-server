package com.may.app.item.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.may.app.item.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long>{
	List<Item> findByMemberId(Long id);
}
