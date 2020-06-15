# SNS Project
- SNS 서비스를 위한 API 서버

## 계층간 규칙
- DTO 변환은 되도록 controller에서 진행하지만, 어쩔 수 없는 경우 service에서도 허용한다. (ex) memberService.detail())

## 사용 기술 및 개발 환경
java8, Spring Boot, Gradle, Spring Data JPA, MySQL 

## 주요 기능 
1. 피드 작성, 조회, 수정, 삭제
2. 피드 목록 조회, 피드 상세 조회  
3. 팔로우, 언팔로우
4. 팔로잉, 팔로워 리스트 
5. 비동기 푸쉬 알림

## 집중 요소 
- Slice 테스트 코드 작성 
- 주요 기능은 도메인에서 진행