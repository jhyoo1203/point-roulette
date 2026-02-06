import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { apiPost } from '@/shared/lib/apiClient';
import { queryKeys } from '@/shared/lib/queryKeys';
import type { ApiErrorResponse } from '@/shared/types/api';
import type { BudgetCreateRequest, BudgetResponse } from '../types';

export function useCreateBudget() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: BudgetCreateRequest) =>
      apiPost<BudgetResponse[]>('/api/v1/admin/budgets', data),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: queryKeys.budget.all });
      toast.success(`${data.length}개의 일일 예산이 생성되었습니다.`);
    },
    onError: (error: ApiErrorResponse) => {
      const message = error.errors
        ? Object.values(error.errors).join(', ')
        : '예산 생성에 실패했습니다.';
      toast.error(message);
    },
  });
}
