# 기술명세서 (ChatGpt ver.)

문서 목적: `local-pinboard` 프론트엔드 프로토타입의 기술 스택, 실행 절차, 스크립트, 라우팅 구조를 구현 기준으로 상세 정의한다.

적용 대상 저장소: `https://github.com/Chenjae-kr/local-pinboard`

---

## 1. 기술 스택 (상세 설계)

### 1.1 런타임/언어/빌드
- **Node.js 18+**: 로컬 개발 및 빌드 런타임
- **TypeScript 5.8.x**: 정적 타입 기반 개발
- **Vite 5.4.x**: 개발 서버/번들러
- **ES Module (`"type": "module"`)**: 모듈 시스템 표준화

### 1.2 UI/프론트엔드 프레임워크
- **React 18.3.x**: 컴포넌트 기반 UI
- **React Router DOM 6.30.x**: SPA 라우팅
- **Tailwind CSS 3.4.x**: 유틸리티 CSS
- **shadcn/ui + Radix UI**: 접근성 기반 UI primitive
- **Lucide React**: 아이콘 시스템

### 1.3 데이터/상태 관리
- **TanStack Query 5.x**: 서버 상태 캐싱/비동기 데이터 계층 준비
- 현재 단계는 `src/data/mockData.ts` 기반 목업 데이터 사용

### 1.4 지도/위치
- **Leaflet 1.9.x / React Leaflet 5.x**: 지도 렌더링 및 핀 상호작용
- **OpenStreetMap Tile**: 지도 타일 소스

### 1.5 폼/검증 및 보조 라이브러리
- **react-hook-form / zod / @hookform/resolvers**: 폼/스키마 검증 확장 준비
- **sonner**: 토스트 알림
- **date-fns**: 날짜 처리 유틸

### 1.6 품질/테스트
- **ESLint 9 + typescript-eslint**: 정적 분석
- **Vitest 3 + Testing Library + jsdom**: 컴포넌트 테스트

### 1.7 설정 표준
- Alias: `@/* -> ./src/*`
- Vite dev server:
  - `host: "::"` (로컬 네트워크 접근 허용)
  - `port: 8080`
  - `hmr.overlay: false`
- Vitest:
  - `environment: "jsdom"`
  - `setupFiles: ["./src/test/setup.ts"]`
  - include 패턴: `src/**/*.{test,spec}.{ts,tsx}`

---

## 2. 실행 방법 (상세 운영 절차)

### 2.1 사전 요구사항
- Node.js 18 이상
- npm
- Git

권장 확인 명령:
```bash
node -v
npm -v
git --version
```

### 2.2 설치 및 초기 실행
```bash
git clone https://github.com/Chenjae-kr/local-pinboard.git
cd local-pinboard
npm install
npm run dev
```

실행 후 접속 주소:
- 기본: `http://localhost:8080`
- 동일 네트워크 기기 접속: `<개발PC_IP>:8080`

### 2.3 빌드/배포 검증
```bash
npm run build
npm run preview
```

출력물:
- `dist/` 폴더에 정적 리소스 생성

미리보기 기본 주소(일반 Vite 기본값):
- `http://localhost:4173`

### 2.4 테스트/정적분석 실행
```bash
npm run lint
npm run test
```

### 2.5 문제 해결 가이드
- 포트 충돌(8080 사용 중): 점유 프로세스 종료 후 재실행
- 의존성 오류: `node_modules` 삭제 후 `npm install` 재시도
- 지도 표시 실패: 네트워크/타일 URL 접근 가능 여부 확인

---

## 3. 스크립트 명세 (운영 기준)

| 스크립트 | 명령 | 목적 | 결과물/동작 |
|---|---|---|---|
| 개발 서버 | `npm run dev` | 로컬 개발 실행 | HMR 활성화, `:8080`에서 앱 제공 |
| 프로덕션 빌드 | `npm run build` | 배포용 번들 생성 | `dist/` 출력 |
| 개발 모드 빌드 | `npm run build:dev` | 개발 모드 옵션으로 빌드 검증 | 개발 설정 기반 번들 생성 |
| 빌드 미리보기 | `npm run preview` | 빌드 산출물 실행 확인 | 정적 번들 서빙 |
| 린트 | `npm run lint` | 코드 품질 검사 | ESLint 리포트 |
| 테스트(1회) | `npm run test` | CI/로컬 단발 테스트 | Vitest run 결과 |
| 테스트(watch) | `npm run test:watch` | 개발 중 지속 테스트 | 파일 변경 시 재실행 |

운영 원칙:
- PR 전 최소 `lint`, `test`, `build` 순으로 검증 수행 권장

---

## 4. 주요 라우트 (상세 설계)

### 4.1 라우팅 계층 구조
- `AppLayout` 적용 라우트:
  - 하단 탭(`근처/지도/모임/더보기`) 공통 노출
  - FAB(`+ 글 작성`)은 `/more`를 제외한 경로에서 노출
- 단독 페이지 라우트:
  - 생성/상세 페이지는 레이아웃 외부에서 독립 렌더링

### 4.2 라우트 표

| 경로 | 화면 컴포넌트 | 레이아웃 | 주요 기능 | 상태 |
|---|---|---|---|---|
| `/` | `NearbyPage` | AppLayout | 반경 필터, 근처 글 리스트 | 구현 완료 |
| `/map` | `MapPage` | AppLayout | 지도 핀 탐색, 선택 카드 | 구현 완료 |
| `/groups` | `GroupsPage` | AppLayout | 모임 검색/카테고리 필터 | 구현 완료 |
| `/more` | `MorePage` | AppLayout | 프로필/설정 메뉴 | 구현 완료 |
| `/groups/create` | `CreateGroupPage` | 단독 | 모임 생성 폼 | 구현 완료(목업 저장) |
| `/groups/:groupId` | `GroupDetailPage` | 단독 | 모임 상세, 가입/탈퇴 UI | 구현 완료(목업 상태 변경) |
| `/posts/create` | `CreatePostPage` | 단독 | 글 작성(평점/태그/위치) | 구현 완료(목업 저장) |
| `/posts/:postId` | `PostDetailPage` | 단독 | 글 상세 조회 | 구현 완료 |
| `*` | `NotFound` | 단독 | 잘못된 경로 처리 | 구현 완료 |

### 4.3 동적 파라미터 규칙
- `:groupId`, `:postId`는 숫자 ID를 기대
- 유효하지 않은 ID 접근 시 "찾을 수 없음" 메시지 표시

### 4.4 네비게이션 정책
- 하단 탭: `BottomTabBar`에서 4개 핵심 화면 이동
- FAB: `/posts/create`로 이동
- 잘못된 경로: `NotFound`로 폴백

### 4.5 현재 라우팅 갭 (후속 보완)
- `GroupDetailPage` 내부 버튼이 `/groups/:groupId/posts`로 이동 시도하나,
  현재 해당 라우트가 등록되어 있지 않아 `NotFound`로 떨어짐
- 해결 방안:
  - 방안 A: `/groups/:groupId/posts` 라우트 추가
  - 방안 B: 기존 `/` 또는 그룹 필터 적용 리스트 화면으로 이동하도록 수정

---

## 5. 구현 제약 및 확장 설계

### 5.1 현재 제약
- 데이터 저장소: 전부 목업 데이터
- 인증/인가: 미구현
- API 통신: 미구현
- 차단/신고/알림: UI 일부만 존재, 서버 정책 미연동

### 5.2 확장 우선순위 제안
1. OpenAPI 계약 정의 및 타입 자동생성 도입
2. `TanStack Query` 기반 API 계층 도입
3. 인증/권한 정책(모임 가입자 열람, 작성자 수정/삭제) 구현
4. 그룹별 게시글 라우트 갭 보완
5. 지도 클러스터링/성능 튜닝

---

## 6. 참고 파일
- `src/App.tsx` (라우트 정의)
- `src/components/AppLayout.tsx` (공통 레이아웃/FAB)
- `src/components/BottomTabBar.tsx` (하단 탭)
- `package.json` (스크립트/의존성)
- `vite.config.ts` (개발 서버/alias)
- `vitest.config.ts` (테스트 설정)
- `eslint.config.js` (정적분석 규칙)
