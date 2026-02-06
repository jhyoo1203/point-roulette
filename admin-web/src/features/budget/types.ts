import type { PaginationParams } from '@/shared/types/api';

/** 일일 예산 응답 */
export interface BudgetResponse {
  id: number;
  budgetDate: string;
  totalAmount: number;
  remainingAmount: number;
  createdAt: string;
  updatedAt: string;
}

/** 일일 예산 생성 요청 */
export interface BudgetCreateRequest {
  startDate: string;
  endDate: string;
}

/** 일일 예산 목록 조회 파라미터 */
export interface BudgetListParams extends PaginationParams {
  startDate: string;
  endDate: string;
}
