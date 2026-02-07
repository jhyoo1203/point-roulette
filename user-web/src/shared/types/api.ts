/**
 * API 공통 응답 타입
 */

// 공통 API 응답 래퍼
export interface ApiResponse<T = unknown> {
  timestamp: string;
  httpStatus: number;
  data?: T;
  errorCode?: ErrorCode;
}

// 에러 코드
export type ErrorCode =
  | 'INTERNAL_SERVER_ERROR'
  | 'INVALID_PARAMETER'
  | 'BUSINESS_ERROR'
  | 'RESOURCE_NOT_FOUND'
  | 'ALREADY_ROULETTE_PARTICIPATED'
  | 'ROULETTE_PARTICIPATION_FAILED'
  | 'INSUFFICIENT_POINT';

// 페이지네이션 응답
export interface PaginationResponse<T = unknown> {
  content: T[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

// 페이지네이션 요청 파라미터
export interface PaginationParams {
  page?: number;
  size?: number;
  sort?: string;
}
