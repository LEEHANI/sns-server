package com.may.app.feed.api;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.may.app.common.ResponseListDto;
import com.may.app.feed.FeedService;
import com.may.app.feed.dto.FeedDto;
import com.may.app.feed.dto.FeedDto.Get;
import com.may.app.feed.entity.Feed;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/feed")
@RequiredArgsConstructor
public class FeedController {
	private final FeedService feedService;
	
	@PostMapping(value = "")
	public FeedDto.Get add(@RequestBody @Valid FeedDto.Post request, BindingResult errors) {
		if(errors.hasErrors()){
			throw new IllegalArgumentException(errors.getAllErrors().get(0).getDefaultMessage());
		}
		
		Feed entity = feedService.add
		(
			request.getContent(), 
			request.getMemberId(), 
			request.getImgs(), 
			request.getComments(), 
			request.getTags(), 
			request.getItemIds()
		);
		
		return new FeedDto.Get(entity);
	}
	
	@GetMapping(value = "/{id}") 
	public FeedDto.Get detail(@PathVariable Long id) {
		Feed entity = feedService.detail(id);
		
		return new FeedDto.Get(entity);
	}

	@GetMapping(value = "")
	public Page<FeedDto.Get> list
	(
		@RequestParam(defaultValue = "0", required = false) int page, 
		@RequestParam(defaultValue = "2", required = false) int size
	) {
		Page<Feed> entities = feedService.list(PageRequest.of(page, size));

		// open-session-in-view가 true라서 1차 캐시를 이용한 작업이 가능 !!
		Page<Get> pages = entities.map(FeedDto.Get::new); 

		return pages;
	}
		
	@PutMapping(value = "/{id}")
	public FeedDto.Get putOne
	(
		@PathVariable Long id, 
		@RequestBody @Valid FeedDto.Put request, 
		BindingResult errors
	) {
		if(errors.hasErrors()){
			throw new IllegalArgumentException(errors.getAllErrors().get(0).getDefaultMessage());
		}
		
		Feed entity = feedService.edit
		(
			id, 
			request.getContent(), 
			request.getMemberId(), 
			request.getImgs(), 
			request.getTags(), 
			request.getItemIds()
		);
		
		return new FeedDto.Get(entity);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseListDto<Boolean> delete(@PathVariable Long id, @RequestParam Long memberId) {
		Long result = feedService.delete(id, memberId);
		
		return new ResponseListDto<Boolean>(result==null? false: true);
	}
}
