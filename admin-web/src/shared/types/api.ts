/** API 성공 응답 Wrapper (Apidog ResponseData 스키마 기반) */
export interface ApiResponse<T> {
  httpStatus: number;
  timestamp: string;
  data: T;
}

/** API 에러 응답 */
export interface ApiErrorResponse {
  httpStatus: number;
  timestamp: string;
  errorCode?: ErrorCode;
  errors?: Record<string, string>;
}

/** 페이지네이션 응답 (Apidog PaginationResponse 스키마 기반) */
export interface PaginatedData<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

/** 페이지네이션 요청 파라미터 */
export interface PaginationParams {
  page?: number;
  size?: number;
  sort?: string;
}

/** 에러 코드 */
export type ErrorCode =
  | 'INTERNAL_SERVER_ERROR'
  | 'INVALID_PARAMETER'
  | 'BUSINESS_ERROR'
  | 'RESOURCE_NOT_FOUND'
  | 'ALREADY_ROULETTE_PARTICIPATED'
  | 'ROULETTE_PARTICIPATION_FAILED'
  | 'INSUFFICIENT_POINT'
  | 'POINT_ALREADY_USED';
