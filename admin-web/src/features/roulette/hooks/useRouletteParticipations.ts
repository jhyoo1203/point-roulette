import { useQuery } from '@tanstack/react-query';
import { apiGet } from '@/shared/lib/apiClient';
import { queryKeys } from '@/shared/lib/queryKeys';
import type { PaginatedData } from '@/shared/types/api';
import type { RouletteParticipationResponse, RouletteParticipationListParams } from '../types';

export function useRouletteParticipations(params: RouletteParticipationListParams) {
  return useQuery({
    queryKey: queryKeys.roulette.participations(params),
    queryFn: () =>
      apiGet<PaginatedData<RouletteParticipationResponse>>('/api/v1/admin/roulette/participations', {
        ...params,
      }),
    staleTime: 5000, // 5초 - 실시간성이 중요한 데이터
    enabled: !!(params.startDate && params.endDate), // 날짜가 있을 때만 실행
  });
}
