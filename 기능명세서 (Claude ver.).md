
## 목차

1. [프로젝트 개요](https://claude.ai/chat/8dee9a06-85e8-455d-bd2a-9faafe63b340#1-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EA%B0%9C%EC%9A%94)
2. [시스템 아키텍처](https://claude.ai/chat/8dee9a06-85e8-455d-bd2a-9faafe63b340#2-%EC%8B%9C%EC%8A%A4%ED%85%9C-%EC%95%84%ED%82%A4%ED%85%8D%EC%B2%98)
3. [데이터 모델](https://claude.ai/chat/8dee9a06-85e8-455d-bd2a-9faafe63b340#3-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EB%AA%A8%EB%8D%B8)
4. [상세 기능 명세](https://claude.ai/chat/8dee9a06-85e8-455d-bd2a-9faafe63b340#4-%EC%83%81%EC%84%B8-%EA%B8%B0%EB%8A%A5-%EB%AA%85%EC%84%B8)
5. [API 명세](https://claude.ai/chat/8dee9a06-85e8-455d-bd2a-9faafe63b340#5-api-%EB%AA%85%EC%84%B8)
6. [UI/UX 명세](https://claude.ai/chat/8dee9a06-85e8-455d-bd2a-9faafe63b340#6-uiux-%EB%AA%85%EC%84%B8)
7. [보안 및 권한](https://claude.ai/chat/8dee9a06-85e8-455d-bd2a-9faafe63b340#7-%EB%B3%B4%EC%95%88-%EB%B0%8F-%EA%B6%8C%ED%95%9C)
8. [성능 요구사항](https://claude.ai/chat/8dee9a06-85e8-455d-bd2a-9faafe63b340#8-%EC%84%B1%EB%8A%A5-%EC%9A%94%EA%B5%AC%EC%82%AC%ED%95%AD)
9. [테스트 시나리오](https://claude.ai/chat/8dee9a06-85e8-455d-bd2a-9faafe63b340#9-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%8B%9C%EB%82%98%EB%A6%AC%EC%98%A4)

---

## 1. 프로젝트 개요

### 1.1 서비스 정의

**지도 기반 후기 공유 커뮤니티 플랫폼**

- 사용자가 주제별 모임(커뮤니티)을 생성하고 지리적 위치에 기반한 후기/정보를 공유하는 소셜 플랫폼
- 모임 가입자만 해당 모임의 콘텐츠를 열람할 수 있는 폐쇄형 커뮤니티 구조

### 1.2 핵심 가치

- **위치 기반 정보 공유**: 실제 경험한 장소에 대한 신뢰도 높은 후기
- **주제별 커뮤니티**: 관심사 기반 모임으로 양질의 콘텐츠 집중
- **프라이버시 보호**: 모임 가입자만 열람 가능한 제한적 공개

### 1.3 주요 타겟

- 맛집/카페 탐색을 좋아하는 사용자
- 지역 기반 취미 활동 참여자
- 특정 주제에 대한 정보를 공유하고 싶은 커뮤니티 운영자

---

## 2. 시스템 아키텍처

### 2.1 기술 스택 제안

#### Frontend

- **Framework**: React Native / Flutter (크로스 플랫폼)
- **상태관리**: Redux Toolkit / Zustand
- **지도**: Naver Maps SDK
- **UI 라이브러리**: React Native Paper / Flutter Material

#### Backend

- **Framework**: Spring Boot (Java)
- **Database**: PostgreSQL (PostGIS 확장 - 공간 데이터)
- **Cache**: Redis (세션, 실시간 데이터)
- **Storage**: AWS S3 / Cloudflare R2 (이미지)
- **Search**: Elasticsearch (모임/글 검색)

#### Infrastructure

- **Cloud**: AWS / GCP
- **CDN**: CloudFront
- **Push**: FCM (Firebase Cloud Messaging)

### 2.2 시스템 구성도

```
[Mobile App] 
    ↓
[API Gateway / Load Balancer]
    ↓
[Application Servers (Spring Boot)]
    ↓
[PostgreSQL + PostGIS] [Redis Cache] [S3 Storage]
    ↓
[Elasticsearch] [FCM]
```

---

## 3. 데이터 모델

### 3.1 ERD 주요 엔티티

#### User (사용자)

```sql
CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    provider VARCHAR(50), -- GOOGLE, APPLE, KAKAO, EMAIL
    provider_id VARCHAR(255),
    nickname VARCHAR(50) NOT NULL UNIQUE,
    profile_image_url VARCHAR(500),
    location_consent BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, SUSPENDED, DELETED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_nickname ON users(nickname);
```

#### Group (모임)

```sql
CREATE TABLE groups (
    group_id BIGSERIAL PRIMARY KEY,
    creator_id BIGINT NOT NULL REFERENCES users(user_id),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(50), -- RESTAURANT, CAFE, SAUNA, TOURISM, etc.
    cover_image_url VARCHAR(500),
    visibility VARCHAR(20) DEFAULT 'PUBLIC', -- PUBLIC, PRIVATE
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, CLOSED
    member_count INT DEFAULT 0,
    post_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_groups_category ON groups(category);
CREATE INDEX idx_groups_creator ON groups(creator_id);
CREATE INDEX idx_groups_status ON groups(status);
```

#### Membership (가입)

```sql
CREATE TABLE memberships (
    membership_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(user_id),
    group_id BIGINT NOT NULL REFERENCES groups(group_id),
    role VARCHAR(20) DEFAULT 'MEMBER', -- ADMIN, MEMBER
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, group_id)
);

CREATE INDEX idx_memberships_user ON memberships(user_id);
CREATE INDEX idx_memberships_group ON memberships(group_id);
CREATE INDEX idx_memberships_composite ON memberships(user_id, group_id);
```

#### Post (핀 글)

```sql
CREATE TABLE posts (
    post_id BIGSERIAL PRIMARY KEY,
    group_id BIGINT NOT NULL REFERENCES groups(group_id),
    author_id BIGINT NOT NULL REFERENCES users(user_id),
    title VARCHAR(200),
    content TEXT NOT NULL,
    rating DECIMAL(2,1), -- 0.0 ~ 5.0
    location GEOGRAPHY(POINT, 4326) NOT NULL, -- PostGIS
    address VARCHAR(500),
    place_name VARCHAR(200),
    view_count INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, DELETED, REPORTED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_posts_group ON posts(group_id);
CREATE INDEX idx_posts_author ON posts(author_id);
CREATE INDEX idx_posts_location ON posts USING GIST(location);
CREATE INDEX idx_posts_created ON posts(created_at DESC);
```

#### PostImage (게시글 이미지)

```sql
CREATE TABLE post_images (
    image_id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL REFERENCES posts(post_id) ON DELETE CASCADE,
    image_url VARCHAR(500) NOT NULL,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_post_images_post ON post_images(post_id);
```

#### PostTag (게시글 태그)

```sql
CREATE TABLE post_tags (
    tag_id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL REFERENCES posts(post_id) ON DELETE CASCADE,
    tag_name VARCHAR(50) NOT NULL
);

CREATE INDEX idx_post_tags_post ON post_tags(post_id);
CREATE INDEX idx_post_tags_name ON post_tags(tag_name);
```

#### Block (차단)

```sql
CREATE TABLE blocks (
    block_id BIGSERIAL PRIMARY KEY,
    blocker_id BIGINT NOT NULL REFERENCES users(user_id),
    blocked_id BIGINT NOT NULL REFERENCES users(user_id),
    block_type VARCHAR(20) DEFAULT 'ONE_WAY', -- ONE_WAY, MUTUAL
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(blocker_id, blocked_id),
    CHECK(blocker_id != blocked_id)
);

CREATE INDEX idx_blocks_blocker ON blocks(blocker_id);
CREATE INDEX idx_blocks_blocked ON blocks(blocked_id);
```

#### Report (신고)

```sql
CREATE TABLE reports (
    report_id BIGSERIAL PRIMARY KEY,
    reporter_id BIGINT NOT NULL REFERENCES users(user_id),
    target_type VARCHAR(20) NOT NULL, -- POST, USER
    target_id BIGINT NOT NULL,
    reason VARCHAR(50) NOT NULL, -- SPAM, INAPPROPRIATE, HARASSMENT, etc.
    description TEXT,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, REVIEWED, RESOLVED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP,
    reviewer_id BIGINT REFERENCES users(user_id)
);

CREATE INDEX idx_reports_reporter ON reports(reporter_id);
CREATE INDEX idx_reports_target ON reports(target_type, target_id);
CREATE INDEX idx_reports_status ON reports(status);
```

---

## 4. 상세 기능 명세

### 4.1 인증 및 계정 관리

#### 4.1.1 소셜 로그인

**기능 설명**

- Apple, Google, Kakao OAuth 2.0 기반 인증
- 이메일 기반 회원가입 및 로그인 지원

**입력 정보**

- Provider (APPLE, GOOGLE, KAKAO, EMAIL)
- OAuth Token (소셜 로그인) 또는 Email/Password

**처리 프로세스**

1. OAuth 토큰 검증 또는 이메일/비밀번호 검증
2. 기존 사용자 확인 (provider + provider_id)
3. 신규 사용자인 경우 회원가입 플로우 진행
4. JWT Access Token / Refresh Token 발급
5. 사용자 정보 반환

**출력 결과**

```json
{
  "userId": 12345,
  "email": "user@example.com",
  "nickname": "여행러버",
  "profileImageUrl": "https://...",
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "isNewUser": false
}
```

**에러 케이스**

- `AUTH_001`: 유효하지 않은 OAuth 토큰
- `AUTH_002`: 중복된 이메일
- `AUTH_003`: 정지된 계정

#### 4.1.2 닉네임 설정

**기능 설명**

- 신규 가입 시 또는 프로필 수정 시 닉네임 설정
- 중복 체크 및 유효성 검증

**유효성 규칙**

- 길이: 2~20자
- 허용 문자: 한글, 영문, 숫자, 밑줄(_)
- 금지어 필터링 적용

**처리 프로세스**

1. 닉네임 중복 체크 (DB 조회)
2. 유효성 검증 (길이, 문자, 금지어)
3. 닉네임 업데이트
4. 성공 응답

**출력 결과**

```json
{
  "success": true,
  "nickname": "여행러버"
}
```

**에러 케이스**

- `USER_001`: 중복된 닉네임
- `USER_002`: 유효하지 않은 닉네임 형식
- `USER_003`: 금지어 포함

#### 4.1.3 위치 권한 동의

**기능 설명**

- 앱 사용을 위한 필수 위치 권한 요청
- 권한 거부 시 서비스 이용 제한 안내

**권한 종류**

- iOS: `NSLocationWhenInUseUsageDescription`
- Android: `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`

**처리 프로세스**

1. 시스템 위치 권한 요청
2. 권한 상태 확인
3. 서버에 권한 동의 여부 저장
4. 거부 시 안내 모달 표시

**출력 결과**

```json
{
  "locationConsent": true,
  "accuracy": "PRECISE"
}
```

---

### 4.2 모임 관리

#### 4.2.1 모임 생성

**기능 설명**

- 로그인한 사용자가 새로운 모임 생성
- 생성자는 자동으로 관리자(ADMIN) 권한 부여

**입력 정보**

```json
{
  "name": "강남 맛집 탐방",
  "description": "강남역 인근 숨은 맛집을 공유하는 모임입니다.",
  "category": "RESTAURANT",
  "coverImage": "base64_or_file",
  "visibility": "PUBLIC"
}
```

**유효성 검증**

- name: 2~100자, 필수
- description: 최대 1000자
- category: 사전 정의된 카테고리 중 선택
- visibility: PUBLIC (MVP는 공개만 지원)

**처리 프로세스**

1. 입력 데이터 검증
2. 커버 이미지 업로드 (S3)
3. Group 테이블에 저장
4. Membership 생성 (role: ADMIN)
5. 생성된 모임 정보 반환

**출력 결과**

```json
{
  "groupId": 1001,
  "name": "강남 맛집 탐방",
  "description": "...",
  "category": "RESTAURANT",
  "coverImageUrl": "https://cdn.../cover.jpg",
  "creatorId": 12345,
  "memberCount": 1,
  "postCount": 0,
  "createdAt": "2025-02-13T10:30:00Z"
}
```

**에러 케이스**

- `GROUP_001`: 필수 항목 누락
- `GROUP_002`: 유효하지 않은 카테고리
- `GROUP_003`: 이미지 업로드 실패

#### 4.2.2 모임 목록 조회 및 검색

**기능 설명**

- 공개 모임 목록 조회
- 카테고리 필터 및 키워드 검색

**입력 파라미터**

```
GET /api/v1/groups?category=RESTAURANT&keyword=맛집&page=0&size=20
```

**필터 옵션**

- `category`: RESTAURANT, CAFE, SAUNA, TOURISM, etc. (선택)
- `keyword`: 모임명 검색 (선택)
- `page`: 페이지 번호 (기본값: 0)
- `size`: 페이지 크기 (기본값: 20, 최대: 100)

**처리 프로세스**

1. 파라미터 검증
2. DB 쿼리 실행 (visibility='PUBLIC', status='ACTIVE')
3. 카테고리 필터 적용
4. 키워드 검색 (name LIKE %keyword%)
5. 페이징 처리
6. 결과 반환

**출력 결과**

```json
{
  "content": [
    {
      "groupId": 1001,
      "name": "강남 맛집 탐방",
      "category": "RESTAURANT",
      "coverImageUrl": "https://...",
      "memberCount": 156,
      "postCount": 423,
      "createdAt": "2025-02-01T..."
    }
  ],
  "totalElements": 45,
  "totalPages": 3,
  "currentPage": 0,
  "size": 20
}
```

#### 4.2.3 모임 상세 조회

**기능 설명**

- 특정 모임의 상세 정보 조회
- 가입 여부에 따라 다른 정보 노출

**입력 파라미터**

```
GET /api/v1/groups/{groupId}
```

**처리 프로세스**

1. groupId로 모임 조회
2. 현재 사용자의 가입 여부 확인
3. 상세 정보 조합 (가입자 수, 최근 글 수 등)
4. 결과 반환

**출력 결과**

```json
{
  "groupId": 1001,
  "name": "강남 맛집 탐방",
  "description": "강남역 인근 숨은 맛집을 공유하는 모임입니다.",
  "category": "RESTAURANT",
  "coverImageUrl": "https://...",
  "creatorId": 12345,
  "memberCount": 156,
  "postCount": 423,
  "recentPostCount": 15, // 최근 7일
  "isMember": true,
  "isCreator": false,
  "createdAt": "2025-02-01T..."
}
```

#### 4.2.4 모임 가입

**기능 설명**

- 공개 모임에 즉시 가입
- 가입 완료 시 멤버십 생성

**입력 정보**

```json
{
  "groupId": 1001
}
```

**처리 프로세스**

1. 모임 존재 여부 확인
2. 모임 상태 확인 (ACTIVE인지)
3. 중복 가입 체크
4. Membership 생성
5. Group의 member_count 증가
6. 성공 응답

**출력 결과**

```json
{
  "success": true,
  "membershipId": 5001,
  "groupId": 1001,
  "joinedAt": "2025-02-13T11:00:00Z"
}
```

**에러 케이스**

- `MEMBERSHIP_001`: 존재하지 않는 모임
- `MEMBERSHIP_002`: 이미 가입한 모임
- `MEMBERSHIP_003`: 폐쇄된 모임

#### 4.2.5 모임 탈퇴

**기능 설명**

- 가입한 모임에서 탈퇴
- 탈퇴 후 해당 모임의 글 열람 불가

**입력 정보**

```json
{
  "groupId": 1001
}
```

**처리 프로세스**

1. 가입 여부 확인
2. 생성자인 경우 탈퇴 제한 (모임 폐쇄 필요)
3. Membership 삭제
4. Group의 member_count 감소
5. 성공 응답

**출력 결과**

```json
{
  "success": true,
  "message": "모임에서 탈퇴했습니다."
}
```

**에러 케이스**

- `MEMBERSHIP_004`: 가입하지 않은 모임
- `MEMBERSHIP_005`: 생성자는 탈퇴 불가

#### 4.2.6 모임 정보 수정 (관리자)

**기능 설명**

- 모임 생성자(관리자)만 정보 수정 가능
- 이름, 소개, 이미지, 카테고리 수정

**입력 정보**

```json
{
  "groupId": 1001,
  "name": "강남 맛집 탐방 (업데이트)",
  "description": "새로운 소개글",
  "category": "RESTAURANT",
  "coverImage": "base64_or_file"
}
```

**권한 검증**

- 요청자가 모임 생성자인지 확인

**처리 프로세스**

1. 권한 확인 (creator_id == 요청자)
2. 입력 데이터 검증
3. 이미지 변경 시 S3 업로드
4. Group 정보 업데이트
5. 성공 응답

**출력 결과**

```json
{
  "success": true,
  "updatedAt": "2025-02-13T12:00:00Z"
}
```

**에러 케이스**

- `GROUP_004`: 권한 없음
- `GROUP_005`: 존재하지 않는 모임

#### 4.2.7 모임 폐쇄 (관리자)

**기능 설명**

- 모임 생성자만 모임 폐쇄 가능
- 폐쇄 시 신규 글 작성 불가, 기존 글은 열람 가능

**입력 정보**

```json
{
  "groupId": 1001,
  "reason": "운영 종료"
}
```

**처리 프로세스**

1. 권한 확인
2. Group status를 'CLOSED'로 변경
3. 모든 멤버에게 알림 발송 (선택)
4. 성공 응답

**출력 결과**

```json
{
  "success": true,
  "status": "CLOSED"
}
```

---

### 4.3 핀 글 관리

#### 4.3.1 글 작성

**기능 설명**

- 모임 가입자만 해당 모임에 글 작성 가능
- 위치 좌표와 함께 후기/정보 등록

**입력 정보**

```json
{
  "groupId": 1001,
  "title": "숨은 맛집 발견!",
  "content": "강남역 뒷골목에 있는 파스타 맛집입니다. 크림 파스타가 정말 맛있어요.",
  "rating": 4.5,
  "latitude": 37.497942,
  "longitude": 127.027621,
  "address": "서울특별시 강남구 강남대로 지하 396",
  "placeName": "강남역",
  "images": ["base64_1", "base64_2"],
  "tags": ["파스타", "이탈리안", "데이트"]
}
```

**유효성 검증**

- groupId: 필수, 가입한 모임인지 확인
- content: 10~5000자
- rating: 0.0~5.0 (0.5 단위)
- latitude, longitude: 필수, 유효한 좌표
- images: 최대 10장
- tags: 최대 10개

**처리 프로세스**

1. 모임 가입 여부 확인
2. 모임 상태 확인 (CLOSED인 경우 작성 불가)
3. 입력 데이터 검증
4. 이미지 업로드 (S3)
5. Post 저장 (PostGIS로 location 저장)
6. PostImage 저장
7. PostTag 저장
8. Group의 post_count 증가
9. 성공 응답

**출력 결과**

```json
{
  "postId": 10001,
  "groupId": 1001,
  "authorId": 12345,
  "title": "숨은 맛집 발견!",
  "content": "...",
  "rating": 4.5,
  "latitude": 37.497942,
  "longitude": 127.027621,
  "images": [
    "https://cdn.../image1.jpg",
    "https://cdn.../image2.jpg"
  ],
  "tags": ["파스타", "이탈리안", "데이트"],
  "createdAt": "2025-02-13T13:00:00Z"
}
```

**에러 케이스**

- `POST_001`: 가입하지 않은 모임
- `POST_002`: 폐쇄된 모임
- `POST_003`: 유효하지 않은 좌표
- `POST_004`: 이미지 업로드 실패

#### 4.3.2 글 목록 조회 (근거리 탐색)

**기능 설명**

- 모임 내 글을 거리순 또는 최신순으로 조회
- 반경 필터 적용 (500m, 1km, 3km, 5km)

**입력 파라미터**

```
GET /api/v1/groups/{groupId}/posts?
  latitude=37.497942&
  longitude=127.027621&
  radius=1000&
  sort=distance&
  page=0&
  size=20
```

**파라미터 설명**

- `latitude`, `longitude`: 현재 위치 (필수)
- `radius`: 반경 (미터 단위, 기본값: 1000)
- `sort`: distance (거리순) 또는 recent (최신순)
- `page`, `size`: 페이징

**처리 프로세스**

1. 모임 가입 여부 확인
2. PostGIS ST_DWithin으로 반경 내 글 조회
3. 차단한 사용자 필터링
4. 정렬 적용
5. 페이징 처리
6. 거리 계산 (ST_Distance)
7. 결과 반환

**출력 결과**

```json
{
  "content": [
    {
      "postId": 10001,
      "title": "숨은 맛집 발견!",
      "authorId": 12345,
      "authorNickname": "여행러버",
      "authorProfileImage": "https://...",
      "rating": 4.5,
      "thumbnailImage": "https://...",
      "distance": 245.5,
      "viewCount": 156,
      "createdAt": "2025-02-13T13:00:00Z"
    }
  ],
  "totalElements": 42,
  "totalPages": 3,
  "currentPage": 0
}
```

#### 4.3.3 글 상세 조회

**기능 설명**

- 특정 글의 상세 정보 조회
- 조회 시 view_count 증가

**입력 파라미터**

```
GET /api/v1/posts/{postId}
```

**처리 프로세스**

1. postId로 글 조회
2. 모임 가입 여부 확인
3. 차단 여부 확인
4. view_count 증가
5. 상세 정보 반환

**출력 결과**

```json
{
  "postId": 10001,
  "groupId": 1001,
  "groupName": "강남 맛집 탐방",
  "authorId": 12345,
  "authorNickname": "여행러버",
  "authorProfileImage": "https://...",
  "title": "숨은 맛집 발견!",
  "content": "강남역 뒷골목에 있는 파스타 맛집입니다...",
  "rating": 4.5,
  "latitude": 37.497942,
  "longitude": 127.027621,
  "address": "서울특별시 강남구...",
  "placeName": "강남역",
  "images": ["https://...", "https://..."],
  "tags": ["파스타", "이탈리안", "데이트"],
  "viewCount": 157,
  "isAuthor": false,
  "createdAt": "2025-02-13T13:00:00Z",
  "updatedAt": "2025-02-13T13:00:00Z"
}
```

**에러 케이스**

- `POST_005`: 존재하지 않는 글
- `POST_006`: 권한 없음 (모임 미가입)
- `POST_007`: 차단한 사용자의 글

#### 4.3.4 글 수정

**기능 설명**

- 작성자만 본인이 작성한 글 수정 가능

**입력 정보**

```json
{
  "postId": 10001,
  "title": "수정된 제목",
  "content": "수정된 내용",
  "rating": 5.0,
  "images": ["base64_new"],
  "tags": ["파스타", "추천"]
}
```

**권한 검증**

- author_id == 요청자 확인

**처리 프로세스**

1. 권한 확인
2. 입력 데이터 검증
3. 기존 이미지 삭제 (필요 시)
4. 새 이미지 업로드
5. Post 업데이트
6. PostImage, PostTag 재생성
7. updated_at 갱신
8. 성공 응답

**출력 결과**

```json
{
  "success": true,
  "postId": 10001,
  "updatedAt": "2025-02-13T14:00:00Z"
}
```

**에러 케이스**

- `POST_008`: 권한 없음 (작성자 아님)
- `POST_009`: 삭제된 글

#### 4.3.5 글 삭제

**기능 설명**

- 작성자만 본인이 작성한 글 삭제 가능
- Soft Delete 방식 (status='DELETED')

**입력 정보**

```json
{
  "postId": 10001
}
```

**처리 프로세스**

1. 권한 확인
2. Post status를 'DELETED'로 변경
3. Group의 post_count 감소
4. 성공 응답

**출력 결과**

```json
{
  "success": true,
  "message": "글이 삭제되었습니다."
}
```

---

### 4.4 지도 및 핀 표시

#### 4.4.1 지도 핀 조회

**기능 설명**

- 지도 영역 내 핀들을 조회
- 클러스터링을 위한 데이터 제공

**입력 파라미터**

```
GET /api/v1/groups/{groupId}/pins?
  minLat=37.49&
  maxLat=37.51&
  minLng=127.02&
  maxLng=127.04&
  zoom=14
```

**파라미터 설명**

- `minLat`, `maxLat`, `minLng`, `maxLng`: 지도 표시 영역
- `zoom`: 지도 줌 레벨 (클러스터링 기준)

**처리 프로세스**

1. 모임 가입 여부 확인
2. 영역 내 글 조회 (PostGIS ST_Within)
3. 차단한 사용자 필터링
4. zoom 레벨에 따라 클러스터링 또는 개별 핀 반환

**출력 결과 (개별 핀)**

```json
{
  "pins": [
    {
      "postId": 10001,
      "latitude": 37.497942,
      "longitude": 127.027621,
      "title": "숨은 맛집 발견!",
      "rating": 4.5,
      "thumbnailImage": "https://..."
    }
  ],
  "clusters": []
}
```

**출력 결과 (클러스터)**

```json
{
  "pins": [],
  "clusters": [
    {
      "latitude": 37.4980,
      "longitude": 127.0276,
      "count": 15,
      "avgRating": 4.3
    }
  ]
}
```

#### 4.4.2 특정 위치 평균 평점 조회

**기능 설명**

- 특정 좌표(장소)에 대한 모임 내 평균 평점 계산
- 같은 장소에 여러 후기가 있을 경우 집계

**입력 파라미터**

```
GET /api/v1/groups/{groupId}/place-rating?
  latitude=37.497942&
  longitude=127.027621&
  radius=50
```

**파라미터 설명**

- `latitude`, `longitude`: 조회할 위치
- `radius`: 동일 장소로 인정할 반경 (미터, 기본값: 50)

**처리 프로세스**

1. 모임 가입 여부 확인
2. 반경 내 글들의 평점 조회
3. 평균 계산
4. 후기 수 집계
5. 결과 반환

**출력 결과**

```json
{
  "latitude": 37.497942,
  "longitude": 127.027621,
  "averageRating": 4.3,
  "reviewCount": 8,
  "recentReviews": [
    {
      "postId": 10001,
      "authorNickname": "여행러버",
      "rating": 4.5,
      "createdAt": "2025-02-13T..."
    }
  ]
}
```

---

### 4.5 차단 기능

#### 4.5.1 사용자 차단

**기능 설명**

- 특정 사용자를 차단하여 해당 사용자의 콘텐츠 숨김
- 단방향 차단 (기본) 또는 상호 차단 (옵션)

**입력 정보**

```json
{
  "blockedUserId": 67890,
  "blockType": "ONE_WAY"
}
```

**처리 프로세스**

1. 본인 차단 방지 (blocker_id != blocked_id)
2. 중복 차단 체크
3. Block 레코드 생성
4. blockType이 'MUTUAL'인 경우 역방향 Block도 생성
5. 성공 응답

**출력 결과**

```json
{
  "success": true,
  "blockId": 3001,
  "blockedUserId": 67890,
  "blockType": "ONE_WAY"
}
```

**에러 케이스**

- `BLOCK_001`: 본인 차단 불가
- `BLOCK_002`: 이미 차단한 사용자

#### 4.5.2 차단 목록 조회

**기능 설명**

- 현재 사용자가 차단한 사용자 목록 조회

**입력 파라미터**

```
GET /api/v1/users/blocks?page=0&size=20
```

**출력 결과**

```json
{
  "content": [
    {
      "blockId": 3001,
      "blockedUserId": 67890,
      "blockedNickname": "스팸유저",
      "blockedProfileImage": "https://...",
      "blockType": "ONE_WAY",
      "createdAt": "2025-02-10T..."
    }
  ],
  "totalElements": 5,
  "currentPage": 0
}
```

#### 4.5.3 차단 해제

**기능 설명**

- 차단한 사용자 차단 해제

**입력 정보**

```json
{
  "blockId": 3001
}
```

**처리 프로세스**

1. Block 존재 여부 확인
2. 권한 확인 (blocker_id == 요청자)
3. Block 레코드 삭제
4. MUTUAL인 경우 역방향 Block도 삭제
5. 성공 응답

**출력 결과**

```json
{
  "success": true,
  "message": "차단이 해제되었습니다."
}
```

---

### 4.6 신고 기능

#### 4.6.1 게시글/사용자 신고

**기능 설명**

- 부적절한 게시글 또는 사용자 신고

**입력 정보**

```json
{
  "targetType": "POST",
  "targetId": 10001,
  "reason": "SPAM",
  "description": "광고성 게시글입니다."
}
```

**신고 사유**

- `SPAM`: 광고/스팸
- `INAPPROPRIATE`: 부적절한 내용
- `HARASSMENT`: 괴롭힘/욕설
- `FALSE_INFO`: 허위 정보
- `ETC`: 기타

**처리 프로세스**

1. 대상 존재 여부 확인
2. 중복 신고 체크 (동일 사용자의 동일 대상 신고)
3. Report 레코드 생성
4. 신고 횟수 임계값 확인 (자동 조치 트리거)
5. 성공 응답

**출력 결과**

```json
{
  "success": true,
  "reportId": 5001,
  "message": "신고가 접수되었습니다."
}
```

---

## 5. API 명세

### 5.1 인증 API

#### POST /api/v1/auth/login

소셜 로그인 또는 이메일 로그인

**Request**

```json
{
  "provider": "GOOGLE",
  "token": "oauth_token_here"
}
```

**Response (200)**

```json
{
  "userId": 12345,
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "isNewUser": false
}
```

#### POST /api/v1/auth/refresh

Access Token 갱신

**Request**

```json
{
  "refreshToken": "eyJhbGc..."
}
```

**Response (200)**

```json
{
  "accessToken": "new_access_token"
}
```

#### POST /api/v1/auth/logout

로그아웃

**Response (200)**

```json
{
  "success": true
}
```

---

### 5.2 사용자 API

#### GET /api/v1/users/me

현재 사용자 정보 조회

**Response (200)**

```json
{
  "userId": 12345,
  "email": "user@example.com",
  "nickname": "여행러버",
  "profileImageUrl": "https://...",
  "locationConsent": true,
  "createdAt": "2025-01-01T..."
}
```

#### PATCH /api/v1/users/me

사용자 정보 수정

**Request**

```json
{
  "nickname": "새닉네임",
  "profileImage": "base64_or_url"
}
```

**Response (200)**

```json
{
  "success": true,
  "updatedAt": "2025-02-13T..."
}
```

---

### 5.3 모임 API

#### POST /api/v1/groups

모임 생성

#### GET /api/v1/groups

모임 목록 조회

#### GET /api/v1/groups/{groupId}

모임 상세 조회

#### PATCH /api/v1/groups/{groupId}

모임 정보 수정 (관리자)

#### DELETE /api/v1/groups/{groupId}

모임 폐쇄 (관리자)

#### POST /api/v1/groups/{groupId}/join

모임 가입

#### DELETE /api/v1/groups/{groupId}/leave

모임 탈퇴

#### GET /api/v1/users/me/groups

내가 가입한 모임 목록

---

### 5.4 게시글 API

#### POST /api/v1/groups/{groupId}/posts

글 작성

#### GET /api/v1/groups/{groupId}/posts

글 목록 조회 (근거리 탐색)

#### GET /api/v1/posts/{postId}

글 상세 조회

#### PATCH /api/v1/posts/{postId}

글 수정

#### DELETE /api/v1/posts/{postId}

글 삭제

#### GET /api/v1/groups/{groupId}/pins

지도 핀 조회

#### GET /api/v1/groups/{groupId}/place-rating

특정 위치 평균 평점

---

### 5.5 차단 API

#### POST /api/v1/users/blocks

사용자 차단

#### GET /api/v1/users/blocks

차단 목록 조회

#### DELETE /api/v1/users/blocks/{blockId}

차단 해제

---

### 5.6 신고 API

#### POST /api/v1/reports

신고 접수

#### GET /api/v1/users/me/reports

내 신고 내역

---

## 6. UI/UX 명세

### 6.1 화면 구조

#### 하단 탭 네비게이션

1. **근처 (Nearby)** - 내 주변 글 피드
2. **지도 (Map)** - 지도 탐색
3. **모임 (Groups)** - 모임 관리
4. **더보기 (More)** - 설정/프로필

### 6.2 주요 화면 상세

#### 온보딩 플로우

1. **시작 화면**: 로고 + "시작하기" 버튼
2. **로그인 선택**: Apple / Google / Kakao / Email
3. **닉네임 설정**: 신규 사용자만
4. **위치 권한**: 필수 권한 요청
5. **메인 화면**: 진입

#### 모임 탐색 화면

- **상단**: 검색바, 카테고리 필터
- **본문**: 모임 카드 그리드
    - 커버 이미지
    - 모임명
    - 카테고리 뱃지
    - 멤버 수 / 글 수
- **하단**: 탭 네비게이션

#### 모임 상세 화면

- **상단**: 커버 이미지 + 모임명
- **정보 섹션**: 소개, 멤버 수, 최근 글 수
- **CTA**: "가입하기" / "탈퇴하기" / "글 보기"
- **관리자 메뉴** (생성자만): 수정, 폐쇄

#### 모임 내부 화면 (핵심)

- **상단**: 모임명, 검색, 필터
- **토글**: 리스트 ↔ 지도
- **리스트 모드**:
    - 반경 선택 (500m ~ 5km)
    - 정렬 (거리/최신)
    - 글 카드 목록
- **지도 모드**:
    - 지도 + 핀 클러스터
    - 핀 탭 → 바텀시트 미리보기
- **플로팅 버튼**: "+ 글 작성"

#### 글 작성 화면

- **Step 1**: 위치 선택
    - 현재 위치 자동 설정
    - "지도에서 선택" 버튼
    - 주소 표시
- **Step 2**: 내용 입력
    - 평점 (별점)
    - 제목 (선택)
    - 본문
    - 사진 업로드 (최대 10장)
    - 태그 (최대 10개)
- **Step 3**: 확인
    - "등록" 버튼
    - 완료 토스트

#### 글 상세 화면

- **이미지**: 갤러리 (스와이프)
- **정보**: 평점, 작성자, 작성일, 거리
- **본문**: 내용
- **태그**: 태그 칩
- **액션**:
    - 공유
    - 신고 (작성자 아닌 경우)
    - 수정/삭제 (작성자만)
- **메뉴** (⋮):
    - 이 사용자 차단
    - 신고하기

#### 차단 관리 화면

- **위치**: 설정 > 차단 목록
- **목록**: 차단한 사용자 카드
    - 프로필 이미지
    - 닉네임
    - 차단일
    - "차단 해제" 버튼
- **옵션**: 차단 방식 설정 (단방향/상호)

---

## 7. 보안 및 권한

### 7.1 인증 방식

- **JWT (JSON Web Token)** 사용
- Access Token: 2시간 유효
- Refresh Token: 14일 유효
- HTTP-Only Cookie로 Refresh Token 관리 (선택)

### 7.2 권한 체계

- **비로그인**: 모임 목록 조회만 가능
- **로그인**: 모임 가입, 글 작성 가능
- **모임 가입자**: 해당 모임의 글 열람 가능
- **글 작성자**: 본인 글 수정/삭제 가능
- **모임 관리자**: 모임 정보 수정, 폐쇄 가능

### 7.3 데이터 보안

- **HTTPS** 필수
- **개인정보**: 암호화 저장 (이메일, 위치 이력 등)
- **이미지**: S3 Signed URL로 제한된 접근
- **Rate Limiting**: API 요청 제한 (예: 100req/min/user)

### 7.4 차단 정책 구현

- **서버 필터링**: 글 조회 API에서 차단 사용자 제외
- **클라이언트 필터링**: 추가 방어선
- **상호 차단**: 설정에서 활성화 가능

---

## 8. 성능 요구사항

### 8.1 응답 시간

- **API 응답**: 평균 200ms 이하
- **이미지 로드**: 1초 이내
- **지도 렌더링**: 2초 이내

### 8.2 동시 사용자

- **목표**: 10,000 DAU (MVP)
- **확장성**: 수평 확장 가능 아키텍처

### 8.3 데이터 처리

- **근거리 조회**: PostGIS 인덱스 활용
- **캐싱**: Redis로 자주 조회되는 데이터 캐싱
    - 모임 정보 (5분 TTL)
    - 인기 글 (10분 TTL)

### 8.4 이미지 최적화

- **업로드**: 리사이징 (최대 1920x1080)
- **썸네일**: 자동 생성 (320x240)
- **CDN**: CloudFront 사용

---

## 9. 테스트 시나리오

### 9.1 E2E 테스트 시나리오

#### 시나리오 1: 신규 사용자 가입 및 모임 탐색

1. 앱 설치 및 실행
2. Google 계정으로 로그인
3. 닉네임 설정 (중복 체크)
4. 위치 권한 허용
5. 모임 목록 조회
6. 카테고리 필터 적용 (맛집)
7. 모임 상세 보기
8. 모임 가입
9. **예상 결과**: 가입 완료, 모임 내부 화면 진입

#### 시나리오 2: 글 작성 및 조회

1. 모임 내부 화면 진입
2. "+ 글 작성" 버튼 탭
3. 현재 위치로 자동 설정
4. 평점 선택 (4.5)
5. 본문 입력
6. 사진 2장 업로드
7. 태그 추가 (파스타, 맛집)
8. "등록" 버튼 탭
9. **예상 결과**: 글 등록 성공, 리스트에 표시

#### 시나리오 3: 지도에서 글 탐색

1. 모임 내부 화면에서 "지도" 탭
2. 지도에서 핀 확인
3. 핀 탭하여 미리보기 표시
4. 미리보기 탭하여 상세 화면 진입
5. **예상 결과**: 글 상세 정보 표시

#### 시나리오 4: 사용자 차단

1. 글 상세 화면에서 ⋮ 메뉴
2. "이 사용자 차단" 선택
3. 확인 다이얼로그에서 "차단"
4. 글 목록으로 돌아가기
5. **예상 결과**: 해당 사용자의 글이 목록에서 사라짐

#### 시나리오 5: 모임 탈퇴

1. 모임 상세 화면 진입
2. "탈퇴하기" 버튼 탭
3. 확인 다이얼로그에서 "탈퇴"
4. **예상 결과**: 탈퇴 완료, 해당 모임의 글 열람 불가

### 9.2 단위 테스트

#### 권한 검증 테스트

- 비가입자의 글 작성 시도 → 실패
- 비가입자의 글 조회 시도 → 실패
- 작성자가 아닌 사용자의 글 수정 시도 → 실패

#### 차단 기능 테스트

- A가 B 차단 후 B의 글 조회 → 필터링됨
- MUTUAL 차단 시 A, B 상호 차단 확인

#### 위치 검색 테스트

- 반경 1km 내 글 조회 → 정확한 결과 반환
- 거리순 정렬 → 가까운 순서대로 정렬됨

---

## 10. 향후 확장 계획

### P1 (MVP 이후 우선 개발)

- 댓글 및 대댓글 기능
- 좋아요 / 도움돼요 기능
- 푸시 알림 (새 글, 댓글 등)
- 모임별 공지/고정글

### P2 (중기)

- 비공개/승인제 모임
- 초대 링크 기능
- 사용자 레벨/배지 시스템
- Naver/Kakao Map POI 연동
- 글 검색 기능 (Elasticsearch)

### P3 (장기)

- 실시간 채팅 (모임 내)
- 이벤트/모임 일정 기능
- 데이터 분석 및 추천 시스템
- 다국어 지원

---

## 부록

### A. 에러 코드 정의

|코드|메시지|설명|
|---|---|---|
|AUTH_001|Invalid OAuth token|OAuth 토큰 검증 실패|
|AUTH_002|Duplicate email|중복된 이메일|
|USER_001|Duplicate nickname|중복된 닉네임|
|GROUP_001|Missing required fields|필수 항목 누락|
|MEMBERSHIP_001|Group not found|존재하지 않는 모임|
|POST_001|Not a group member|모임 미가입|
|BLOCK_001|Cannot block yourself|본인 차단 불가|

### B. 데이터베이스 인덱스 전략

- `users.email`: B-Tree 인덱스
- `posts.location`: GiST 인덱스 (PostGIS)
- `posts.created_at`: B-Tree 인덱스 (DESC)
- `memberships(user_id, group_id)`: 복합 인덱스

### C. 캐싱 전략

- **모임 정보**: 5분 TTL
- **사용자 프로필**: 10분 TTL
- **인기 글**: 10분 TTL
- **차단 목록**: 사용자별 1시간 TTL

---

**문서 버전**: 1.0  
**작성일**: 2025-02-13  
**작성자**: 챈재