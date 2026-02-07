/**
 * 포인트 조회 Hook
 */

'use client';

import { useQuery } from '@tanstack/react-query';
import { get } from '@/shared/lib/apiClient';
import { queryKeys } from '@/shared/lib/queryKeys';
import { ENDPOINTS } from '@/shared/constants/endpoints';
import type { PointBalanceResponse } from '@/shared/types/models';

export function usePoints(userId: number) {
  return useQuery({
    queryKey: queryKeys.points.balance(userId),
    queryFn: async () => {
      const data = await get<PointBalanceResponse>(ENDPOINTS.POINTS(userId));
      return data;
    },
    staleTime: 5000, // 5초
    refetchOnWindowFocus: true,
    enabled: !!userId,
  });
}
