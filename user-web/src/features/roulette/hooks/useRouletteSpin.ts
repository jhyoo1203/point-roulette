/**
 * 룰렛 참여 훅
 */

import { useMutation, useQueryClient } from '@tanstack/react-query';
import { post } from '@/shared/lib/apiClient';
import { ENDPOINTS } from '@/shared/constants/endpoints';
import { queryKeys } from '@/shared/lib/queryKeys';
import type { RouletteParticipateResponse } from '@/shared/types/models';

export function useRouletteSpin(userId: number) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () =>
      post<RouletteParticipateResponse>(ENDPOINTS.ROULETTE_PARTICIPATE(userId)),
    onSuccess: () => {
      // 룰렛 상태 캐시 무효화
      queryClient.invalidateQueries({ queryKey: queryKeys.roulette.status(userId) });
      // 포인트 잔액 캐시 무효화
      queryClient.invalidateQueries({ queryKey: queryKeys.points.balance(userId) });
    },
  });
}
