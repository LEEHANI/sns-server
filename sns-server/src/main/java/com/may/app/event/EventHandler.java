package com.may.app.event;

import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.may.app.follow.repository.FollowRepository;
import com.may.app.member.entity.Member;
import com.may.app.member.repository.MemberRepository;
import com.may.app.push.Push;
import com.may.app.push.repository.PushRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventHandler {
	private final FollowRepository followRepository;
	private final PushRepository pushRepository;
	
	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handle(FollowerPushedEvent e) {
		
		List<Member> followers = followRepository.findByFollower(e.getFeed().getMember().getId());
		
		List<Push> pushs = new ArrayList<Push>();
		String content = e.getFeed().getMember().getName()+"님이 새로운 글 "+e.getFeed().getContent()+" 를 등록했어요!";

		followers.forEach(o->pushs.add(new Push(content, o)));
		
		pushRepository.saveAll(pushs);
	}
	
//	@Async("threadPoolTaskExecutor")
//    public Future<String> method1(String message) throws Exception {
//        // do something
//        return new AsyncResult<String>("Success");
//    }
}
