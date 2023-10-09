# 오피스 파인더 백엔드 리포지토리
### 협업 툴 https://www.notion.so/1d1353b49cb5451792acf302fffe1685
개발 인원 : Backend 4명, Frontend 3명   
개발 기간 : 8월 11일 ~ 9월 14일 (약 한달)   

## 1. 주제 설명
- 오피스 파인더는 공유 오피스를 이용하는 사람들이 더 편리하게 공유 오피스를 검색하고, 임대할 수 있도록 돕는 서비스입니다.
- 공유 오피스를 제공하는 오피스 임대업자도 자신의 오피스를 등록하고, 공유 오피스 현황을 확인할 수 있습니다.

## 2. 사용 기술 스택
<img src="https://img.shields.io/badge/amazon_aws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white">
<img src="https://img.shields.io/badge/amazons3-569A31?style=for-the-badge&logo=amazons3&logoColor=white">
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/springsecurity-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white">
<img src="https://img.shields.io/badge/hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white">
<img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white">
<img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=Swagger&logoColor=black">
<img src="https://img.shields.io/badge/WebSocket-010101?style=for-the-badge&logo=&logoColor=white">

## 3. 주요 기능
### 3-1. 일반 회원 관리 기능
- 회원은 서비스에 로그인 및 회원가입을 수행할 수 있습니다.
    - 일반 회원의 경우, OAuth2.0을 사용하여 로그인 및 회원가입이 가능합니다.
      </br></br>
- 일반 회원은 마이페이지를 통해 현재 자신의 상황을 확인할 수 있습니다.
    - 일반 회원은 회원 페이지에서 자신의 정보를 변경할 수 있습니다.
    - 일반 회원은 회원 페이지에서 자신이 보유한 포인트를 충전할 수 있습니다.
    - 일반 회원은 회원 페이지에서 자신이 진행 중이거나 만료된 임대를 확인할 수 있습니다.
    - 일반 회원은 임대했던 공유 오피스에 대해 리뷰를 남길 수 있습니다.

### 3-2. 임대주 회원 관리 기능
- 임대주 회원은 서비스에 로그인 및 회원가입을 수행할 수 있습니다.
  </br></br>
- 임대주 회원은 마이페이지를 통해 현재 자신이 가진 건물들에 대한 정보를 확인할 수 있습니다.
    - 임대주 회원은 자신이 보유한 오피스를 서비스에 등록, 수정, 삭제를 할 수 있습니다.
    - 임대주 회원은 자신이 보유한 오피스에 대한 월 별 매출과, 월 별 임대 현황을 진행할 수 있습니다.
    - 임대주 회원은 자신이 보유한 오피스에 대해 각 오피스에 등록된 리뷰를 확인할 수 있습니다.
    - 임대주 회원은 자신이 보유한 포인트에 대해 출금할 수 있습니다.
    - 임대주 회원은 자신이 보유한 오피스에 대해 각 오피스가 진행 중인 임대 계약을 확인할 수 있습니다.
        - 임대주 회원은 자신이 보유한 오피스에 들어온 임대 계약 요청을 수락하거나 거절할 수 있습니다.

### 3-3. 오피스 조회 기능
- 사용자는 특정 조건들을 선택하거나, 선택하지 않고 오피스를 검색할 수 있습니다.


- 사용자는 현재 자신의 위치를 기반으로 위치가 가까운 오피스들을 검색할 수 있습니다.


- 사용자는 오피스 리스트에서 하나를 선택하여 상세 조회를 수행할 수 있습니다.

### 3-4. 오피스 임대 기능
- 사용자는 자신이 조회한 오피스에 대해 임대를 진행할 수 있습니다.
    - 임대를 하기 위해서 사용자는 임대 시작 날짜를 지정하고, 몇 개월간 머무를 지 선택해야 합니다.
    - 임대를 하기 위해서 사용자는 현재 자신이 소유한 포인트를 지불해야 합니다.
        - 계약에 필요한 포인트가 없는 경우, 임대를 진행할 수 없습니다.
    - 사용자는 임대 시작 날짜 이전에 한하여 임대 계약을 취소할 수 있습니다.

### 3-5. 자동 결재 및 알람 기능
- 시스템은 자동으로 회원의 포인트를 사용하여 임대 계약을 유지할 수 있습니다. (월세의 개념)
    - 회원의 포인트가 부족할 경우, 최대 5일동안 회원의 포인트를 사용하여 계약 유지를 수행하려고 시도하며 그 이후 오피스 소유주에게 알람을 보냅니다.
- 시스템은 회원이 임대 연장 신청된 임대에 대해 회원의 포인트를 사용하여 임대 계약을 연장할 수 있습니다.
    - 회원의 포인트가 부족할 경우 결재일 3일 전부터 알람을 진행합니다.
    - 연장 당일 포인트가 부족할 경우 기간이 연장되지 않습니다.

### 3-6. 회원 - 오피스 소유주간 1:1 채팅 기능
- 회원은 오피스 상세 조회에서 오피스 소유주와 1:1 채팅이 가능합니다.

     
## 4. 담당 역할
1. 회원 가입 및 로그인 기능 (OAuth 2.0 구글 로그인 포함)   
   - Spring Security, jwt 기반의 회원 가입 및 로그인 기능 구현 
2. 임대주의 오피스 월별 매출 및 임대 현황 조회 기능
   - 특정 오피스의 매출 및 임대 현황
   - 임대주가 보유한 모든 오피스의 총 매출 및 임대 현황 
3. 회원 - 임대주 간 1:1 채팅 기능 구현
   - 채팅방 생성하기
   - 채팅방 목록 조회
   - 새로 온 메세지 표시 
   WebSocket(SockJS) 사용
4. GlobalExceptionHandler 구현
5. 기타 프론트-백엔드 연결 작업시 나타나는 오류 해결 작업

## 5. Trouble Shooting
1. Filter 단계에서 CustomException 사용 불가   
   Filter 단계에서 jwt 토큰으로 회원 인증을 하는데, jwt토큰이 올바르지 않거나 만료된 토큰일 때 CustomException으로 에러를 응답해주려고 코드를 작성했다.
   그런데 CustomException이 Null로 처리되서 에러를 제대로 응답해주지 못하는 현상이 발생했다. 원인을 찾아보니 Filter단계는 아직 Spring Context 범위 밖이기 때문에
   Spring에서 구현해준 CustomException을 인식하지 못해서 발생하는 현상이었다. 그래서 CustomException을 사용하려면 Interceptor 단계에서 처리를 해줘야 했다.

2. JwtAuthenticationFilter가 디폴트 필터로 설정되는 현상
   Jwt 인증 필터는 회원 인증이 필요한 api url에서만 필터를 거쳐야 하는데, SecurityConfig에서 permit해준 url이 계속 JwtAuthenticationFilter를 거치는 현상을 발견했다.
   원인을 찾아보니 필터를 @Component로 스프링 빈으로 등록해주면 Security의 filterChain이 아닌 default filterChain에 등록되기 때문에 어떤 요청이든지 필터를 거치는 현상이
   생기게 된 것이다. 그래서 스프링 빈으로 등록하지 않고 직접 객체를 생성해서 필터에 등록해주니 permit한 url은 필터를 거치지 않게 되었다.

3. Cors 문제
   Cors 에러가 발생하였다. 스프링 백엔드 서버와 리액트 프론트 서버 연동시에 Origin이 달라서 발생기는 오류였다. Port가 달랐기 때문에 로컬서버 환경에서 테스트할때 Cors에러가 생겼고,
   http://localhost:5137 를 허용해주니 문제가 해결되었다. 이후 서버 배포시에 프론트 서버의 주소도 허용해주었다.

4. 웹소켓으로 채팅 구현시 Jwt 필터를 계속 거치는 현상
   채팅 구현시 웹소켓으로 구현했는데, 테스트 도중 서버 로그를 보니 ws:// url 루트로 jwt 필터를 과도하게 거치는 현상을 발견했다. 웹소켓 연결시 처음에는 http 프로토콜로 연결을 하기 때문에
   이때는 필터를 거쳐야 하지만 그 이후에 ws:// 통신은 거칠 필요가 없을거 같아서 ws:// 로 오는 모든 요청들을 jwt필터를 거치지 않게 설정해 주었다.
   
