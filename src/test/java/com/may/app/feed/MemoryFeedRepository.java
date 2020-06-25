package com.may.app.feed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.may.app.feed.dto.FeedDto.Get;
import com.may.app.feed.entity.Feed;
import com.may.app.feed.repository.FeedRepository;


/**
 * fake repository
 * DB 대신에 메모리를 이용해 구현한다. 
 * @author hani
 *
 */
public class MemoryFeedRepository implements FeedRepository{
	private Map<Long, Feed> feeds = new HashMap<>();
	Long id = 0L;
	
	@Override
	public Optional<Feed> findByIdAndMemberId(Long id, Long memberId) {
		return Optional.of(feeds.get(id));
	}

	@Override
	public Optional<Feed> findFetchMemberById(Long id) {
		return Optional.of(feeds.get(id));
	}

	@Override
	public Page<Feed> findEntityGraphBy(Pageable pageable) {
		int skip = (pageable.getPageNumber()+1)*pageable.getPageSize(); 
		List<Feed> list = feeds.entrySet().stream().map(o->o.getValue()).skip(skip).limit(pageable.getPageSize()).collect(Collectors.toList());
		Page<Feed> pageFeeds = new PageImpl<>(list, pageable, list.size());
		return pageFeeds;
	}
	
	@Override
	public <S extends Feed> S save(S entity) {
		Feed feed = Feed.builder()
				.id(id++)
				.content(entity.getContent())
				.member(entity.getMember())
				.resources(entity.getResources())
				.build();
		
		feeds.put(entity.getId(), entity);
		return (S) feed;
	}
	
	@Override
	public List<Feed> findByMemberId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}


	
	@Override
	public List<Feed> findAll() {
		return feeds.entrySet().stream().map(o->o.getValue()).collect(Collectors.toList());
	}

	@Override
	public void delete(Feed entity) {
		feeds.remove(entity.getId());
	}
	
	@Override
	public List<Feed> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Feed> findAllById(Iterable<Long> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Feed> List<S> saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <S extends Feed> S saveAndFlush(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteInBatch(Iterable<Feed> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAllInBatch() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Feed getOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Feed> List<S> findAll(Example<S> example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Feed> List<S> findAll(Example<S> example, Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Feed> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Optional<Feed> findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean existsById(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void deleteById(Long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll(Iterable<? extends Feed> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <S extends Feed> Optional<S> findOne(Example<S> example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Feed> Page<S> findAll(Example<S> example, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Feed> long count(Example<S> example) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <S extends Feed> boolean exists(Example<S> example) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Optional<Feed> findDetailById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Get findDetailDtoById(Feed feed) {
		// TODO Auto-generated method stub
		return null;
	}

}
