# near-talk

지도 기반 후기 공유 커뮤니티 서비스 기획 프로젝트입니다.

사용자는 주제별 모임(커뮤니티)에 가입한 뒤, 지도 좌표(핀)에 후기/평점/사진을 기록하고, 리스트와 지도 뷰로 주변 정보를 탐색할 수 있습니다.

## 현재 상태

이 저장소는 **설계 문서 중심**으로 구성되어 있으며, 구현 코드는 아직 포함되어 있지 않습니다.

## 핵심 기능 (MVP)

- 소셜 로그인/프로필
- 모임 생성/검색/가입/탈퇴
- 모임 가입자 전용 핀 글 CRUD
- 리스트(근거리/최신) + 지도(핀/클러스터) 탐색
- 사용자 차단(차단 사용자 콘텐츠 숨김)

## 디렉터리 구조

```text
.
├── 1.설계/
│   ├── 기능명세서 (ChatGpt ver.).md
│   ├── 기능명세서 (Claude ver.).md
│   └── 화면설계 ( lovable ver.).md
└── README.md
```

## 설계 문서

- `/Users/chenjae/dev/project/near-talk/1.설계/기능명세서 (ChatGpt ver.).md`
- `/Users/chenjae/dev/project/near-talk/1.설계/기능명세서 (Claude ver.).md`
- `/Users/chenjae/dev/project/near-talk/1.설계/화면설계 ( lovable ver.).md`

## 제안 기술 스택 (초안)

- Frontend: React Native 또는 Flutter
- Backend: Spring Boot
- Database: PostgreSQL + PostGIS
- Cache: Redis
- Storage: S3 또는 R2
- Map: Naver Maps SDK

## 다음 작업 제안

1. 단일 기능명세서로 통합 (버전 간 차이 정리)
2. API 명세서(OpenAPI) 초안 작성
3. ERD 확정 및 마이그레이션 초기 세팅
4. 클라이언트/서버 레포 구조 생성

## 참고

화면 설계 관련 참고 링크:

- <https://github.com/Chenjae-kr/local-pinboard>
