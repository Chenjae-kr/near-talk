# Near Talk

지도 기반 후기 공유 커뮤니티 서비스입니다.

사용자는 주제별 모임(커뮤니티)에 가입한 뒤, 지도 좌표(핀)에 후기/평점/사진을 기록하고, 리스트와 지도 뷰로 주변 정보를 탐색할 수 있습니다.

## 핵심 기능 (MVP)

- 소셜 로그인/프로필
- 모임 생성/검색/가입/탈퇴
- 모임 가입자 전용 핀 글 CRUD
- 리스트(근거리/최신) + 지도(핀/클러스터) 탐색
- 사용자 차단(차단 사용자 콘텐츠 숨김)

## 기술 스택

| Layer | Technology |
|-------|-----------|
| Frontend | Expo SDK 54, React Native 0.81, Expo Router v6 |
| State | Zustand (client) + TanStack Query (server) |
| Styling | NativeWind (Tailwind CSS for RN) |
| Map | @mj-studio/react-native-naver-map |
| Backend | Spring Boot 3.5, Java 17, Gradle Kotlin DSL |
| DB | PostgreSQL + PostGIS (Hibernate Spatial) |
| Cache | Redis |
| Storage | AWS S3 compatible (S3/R2) |

## 디렉터리 구조

```text
.
├── 1.설계/                     # 설계 문서
├── frontend/                   # Expo (React Native) 앱
│   ├── app/                    # Expo Router 파일 기반 라우트
│   │   ├── (auth)/             # 인증 플로우
│   │   ├── (tabs)/             # 하단 탭 (근처/지도/모임/더보기)
│   │   ├── posts/              # 글 상세/작성
│   │   └── groups/             # 모임 상세/생성
│   └── src/                    # 소스 코드
│       ├── components/         # 공유 UI 컴포넌트
│       ├── hooks/              # 커스텀 훅
│       ├── services/           # API 클라이언트
│       ├── stores/             # Zustand 스토어
│       ├── queries/            # TanStack Query 훅
│       ├── types/              # TypeScript 타입
│       ├── constants/          # 상수
│       └── utils/              # 유틸리티
├── backend/                    # Spring Boot API 서버
│   └── src/main/java/com/neartalk/api/
│       ├── config/             # 설정 (Security 등)
│       ├── domain/             # 도메인별 entity/repo/service/controller/dto
│       │   ├── user/
│       │   ├── group/
│       │   ├── post/
│       │   ├── block/
│       │   └── report/
│       ├── auth/               # JWT/OAuth 인증
│       └── common/             # 공통 (exception, dto, entity)
└── README.md
```

## 사전 요구사항

- **Node.js** 18+ (프론트엔드)
- **Java** 17+ (백엔드)
- **PostgreSQL** 14+ with PostGIS extension
- **Redis** 7+

## 시작하기

### Frontend

```bash
cd frontend
npm install
npx expo start
```

> Expo Go 앱에서 QR 코드를 스캔하여 실행합니다.
> 네이버 지도 기능은 개발 빌드가 필요합니다 (`npx expo prebuild && npx expo run:ios`).

### Backend

```bash
cd backend

# .env 파일 생성
cp .env.example .env
# .env 파일을 수정하여 DB/Redis 정보 입력

# 빌드 및 실행
./gradlew build
./gradlew bootRun --args='--spring.profiles.active=local'
```

API 서버가 `http://localhost:8080/api/v1` 에서 실행됩니다.

헬스 체크: `curl http://localhost:8080/api/v1/actuator/health`

## 설계 문서

- `1.설계/기능명세서 (ChatGpt ver.).md`
- `1.설계/기능명세서 (Claude ver.).md`
- `1.설계/화면설계 ( lovable ver.).md`

## 참고

화면 설계 관련 참고: https://github.com/Chenjae-kr/local-pinboard
