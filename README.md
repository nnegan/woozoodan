# woozoodan (2020년 Toy Project)

###필수 설치 프로그램
1. lombok (인텔리J는 기본제공) -> set/get 자동생성

    https://projectlombok.org/download
    
    다운로드 후에 java -jar lombok.jar 실행 후 이클립스 실행파일 인스톨 
2. PostMan 설치 -> api 호출 

   https://www.getpostman.com/ 
   
### 구성
1. common-lib
    1) DB 접속 (rw/ro 객체를 분리해서 사용 가능하도록 구성)
    2) MyBatis 설정
    3) Redis 접속 설정 및 RedisTemplate 의 편의성 추가 구성
    4) 전체 메세지의 포맷 설정 (일반, 페이징처리)
    5) 메시지 프로퍼티 설정
    6) RabbitMQ send, listener 설정
    7) RestClient 설정 및 RestClientTemplate 의 편의성 추가 구성
 
2. backend  
    1) 비즈니스 프로젝트
    2) SampleController 에 관련 예제 추가
    
    
