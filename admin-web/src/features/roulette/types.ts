import type { PaginationParams } from '@/shared/types/api';

/** 룰렛 참여 상태 */
export type RouletteStatus = 'SUCCESS' | 'CANCELLED';

/** 룰렛 참여 이력 응답 (관리자용) */
export interface RouletteParticipationResponse {
  id: number;
  userId: number;
  userName: string;
  participatedDate: string;
  wonAmount: number;
  status: RouletteStatus;
}

/** 룰렛 참여 이력 목록 조회 파라미터 */
export interface RouletteParticipationListParams extends PaginationParams {
  startDate: string;
  endDate: string;
}

/** 룰렛 이력 응답 (취소용) */
export interface RouletteHistoryResponse {
  id: number;
  participatedDate: string;
  wonAmount: number;
  status: string;
}
