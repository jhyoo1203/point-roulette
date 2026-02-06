import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { apiPost } from '@/shared/lib/apiClient';
import { queryKeys } from '@/shared/lib/queryKeys';
import type { ApiErrorResponse } from '@/shared/types/api';
import type { RouletteHistoryResponse } from '../types';

export function useCancelParticipation() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (historyId: number) =>
      apiPost<RouletteHistoryResponse>(`/api/v1/admin/roulette/participations/${historyId}/cancel`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.roulette.all });
      queryClient.invalidateQueries({ queryKey: queryKeys.budget.all });
      toast.success('룰렛 참여가 취소되고 포인트가 회수되었습니다.');
    },
    onError: (error: ApiErrorResponse) => {
      const message = error.errors
        ? Object.values(error.errors).join(', ')
        : '참여 취소에 실패했습니다.';
      toast.error(message);
    },
  });
}
