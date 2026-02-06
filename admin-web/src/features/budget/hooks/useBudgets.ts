import { useQuery } from '@tanstack/react-query';
import { apiGet } from '@/shared/lib/apiClient';
import { queryKeys } from '@/shared/lib/queryKeys';
import type { PaginatedData } from '@/shared/types/api';
import type { BudgetListParams, BudgetResponse } from '../types';

export function useBudgets(params: BudgetListParams) {
  return useQuery({
    queryKey: queryKeys.budget.daily(params),
    queryFn: () =>
      apiGet<PaginatedData<BudgetResponse>>('/api/v1/admin/budgets', {
        ...params,
      }),
    staleTime: 5000, // 5초 - 운영 의사결정에 치명적이므로 짧은 주기
    enabled: !!(params.startDate && params.endDate), // 날짜가 있을 때만 실행
  });
}
