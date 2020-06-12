package com.may.app.item.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.may.app.item.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long>{
//	@Query("SELECT i FROM Item i JOIN FETCH i.member m ON m.id=:memberId WHERE i.id IN (:ids)")
//	List<Item> findByIdAndMemberId(List<Long> ids, Long memberId);
}
