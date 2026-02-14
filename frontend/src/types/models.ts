/** 사용자 */
export interface User {
  id: number;
  email: string;
  nickname: string;
  profileImageUrl: string | null;
  provider: "KAKAO" | "NAVER" | "GOOGLE";
  createdAt: string;
}

/** 모임 */
export interface Group {
  id: number;
  name: string;
  description: string;
  imageUrl: string | null;
  region: string;
  memberCount: number;
  createdAt: string;
}

/** 모임 멤버십 */
export interface Membership {
  id: number;
  userId: number;
  groupId: number;
  role: "OWNER" | "ADMIN" | "MEMBER";
  joinedAt: string;
}

/** 게시글 */
export interface Post {
  id: number;
  groupId: number;
  authorId: number;
  author: Pick<User, "id" | "nickname" | "profileImageUrl">;
  title: string;
  content: string;
  latitude: number;
  longitude: number;
  placeName: string | null;
  images: PostImage[];
  tags: string[];
  likeCount: number;
  commentCount: number;
  createdAt: string;
  updatedAt: string;
}

/** 게시글 이미지 */
export interface PostImage {
  id: number;
  imageUrl: string;
  order: number;
}

/** 차단 */
export interface Block {
  id: number;
  blockerUserId: number;
  blockedUserId: number;
  createdAt: string;
}

/** 신고 */
export interface Report {
  id: number;
  reporterUserId: number;
  targetType: "POST" | "COMMENT" | "USER";
  targetId: number;
  reason: string;
  createdAt: string;
}

/** API 공통 응답 */
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string | null;
}

/** 페이지네이션 응답 */
export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}
