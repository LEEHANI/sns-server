package com.may.app.follow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.may.app.follow.entity.Follow;
import com.may.app.follow.repository.FollowRepository;
import com.may.app.member.entity.Member;

public class MemoryFollowRepository implements FollowRepository {
	private Map<Long, Follow> follows = new HashMap<>();
	Long count = 0L;
	
	@Override
	public Follow save(Follow entity) {
		Follow follow = Follow.builder()
			.id(entity.getId()==null?count++:entity.getId())
			.follower(entity.getFollower())
			.following(entity.getFollowing())
			.build();
		
		follows.put(follow.getId(), follow);
		return follow;
	}
	
	@Override
	public Optional<Follow> findByFollowerAndFollowing(Long followerId, Long followingId) {
		 Optional<Follow> findFirst = follows.values().stream()
		.filter(o->o.getFollower().getId().equals(followerId))
		.filter(o->o.getFollowing().getId().equals(followingId))
		.findFirst();
		 
		 return findFirst;
	}
	
	@Override
	public Optional<Follow> findById(Long id) {
		return Optional.ofNullable(follows.get(id));
	}

	
	
	@Override
	public List<Follow> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Follow> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Follow> findAllById(Iterable<Long> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Follow> List<S> saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <S extends Follow> S saveAndFlush(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteInBatch(Iterable<Follow> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAllInBatch() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Follow getOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Follow> List<S> findAll(Example<S> example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Follow> List<S> findAll(Example<S> example, Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Follow> findAll(Pageable pageable) {
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
	public void delete(Follow entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll(Iterable<? extends Follow> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <S extends Follow> Optional<S> findOne(Example<S> example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Follow> Page<S> findAll(Example<S> example, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Follow> long count(Example<S> example) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <S extends Follow> boolean exists(Example<S> example) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Member> findByFollowing(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Member> findByFollower(Long id) {
		// TODO Auto-generated method stub
		return null;
	}


}
