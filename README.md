# 오피스 파인더 백엔드 리포지토리

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