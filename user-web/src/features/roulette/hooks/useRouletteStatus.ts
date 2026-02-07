/**
 * 룰렛 상태 조회 훅
 */

import { useQuery } from '@tanstack/react-query';
import { get } from '@/shared/lib/apiClient';
import { ENDPOINTS } from '@/shared/constants/endpoints';
import { queryKeys } from '@/shared/lib/queryKeys';
import type { RouletteStatusResponse } from '@/shared/types/models';

export function useRouletteStatus(userId: number) {
  return useQuery({
    queryKey: queryKeys.roulette.status(userId),
    queryFn: () => get<RouletteStatusResponse>(ENDPOINTS.ROULETTE_STATUS(userId)),
    staleTime: 0, // 항상 최신 상태 유지
    refetchOnWindowFocus: true, // 탭 전환 시 최신 데이터 보장
  });
}
