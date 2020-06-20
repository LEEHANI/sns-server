package com.may.app.tag.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.may.app.common.CreateEntity;
import com.may.app.tag.entity.Tag;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
public class TagRepositoryTest {
	@Autowired
	private TagRepository tagRepository;
	
	Tag tag1 = CreateEntity.createTag(null);
	Tag tag2 = CreateEntity.createTag(null);
	
	@BeforeEach
	public void setUp() throws Exception {
		tag1 = tagRepository.save(tag1);
		tag2 = tagRepository.save(tag2);
	}
	
	/**
	 * tagRepository.findByTitleIn()
	 * 태그 제목으로 정상 조회한다.
	 */
	@Test
	public void 제목으로_IN_검색_성공() {
		//given
		List<String> titles = new ArrayList<>(Arrays.asList("tagTest",tag1.getTitle(),tag2.getTitle()));
		
		//when
		List<Tag> result = tagRepository.findByTitleIn(titles);
		
		//then
		assertNotNull(result);
		assertEquals(result.size(), 2);
		assertEquals(result.get(0).getTitle(), tag1.getTitle());
		assertEquals(result.get(1).getTitle(), tag2.getTitle());
	}
}
