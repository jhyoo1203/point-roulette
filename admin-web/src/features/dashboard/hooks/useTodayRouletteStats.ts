import { useQuery } from '@tanstack/react-query';
import { queryKeys } from '@/shared/lib/queryKeys';
import { fetchTodayRouletteParticipations } from '../api';
import type { DashboardRouletteStats } from '../types';

/**
 * 오늘 룰렛 통계 조회 훅
 *
 * 룰렛 참여 이력을 조회하여 통계를 계산합니다.
 * - 참여 인원수: totalElements 사용
 * - 총 지급 포인트: SUCCESS 상태의 wonAmount 합산
 * - 성공/취소 건수 집계
 *
 * @param today - ISO 형식 날짜 (YYYY-MM-DD)
 */
export function useTodayRouletteStats(today: string) {
  return useQuery({
    queryKey: queryKeys.dashboard.todayRouletteStats(today),
    queryFn: async (): Promise<DashboardRouletteStats> => {
      const result = await fetchTodayRouletteParticipations(today);

      // 통계 계산 (프론트엔드 집계)
      let totalPointsGiven = 0;
      let successCount = 0;
      let cancelledCount = 0;

      for (const participation of result.content) {
        if (participation.status === 'SUCCESS') {
          totalPointsGiven += participation.wonAmount;
          successCount++;
        } else if (participation.status === 'CANCELLED') {
          cancelledCount++;
        }
      }

      return {
        participantCount: result.totalElements, // 전체 참여 인원수 (페이지네이션 메타데이터 활용)
        totalPointsGiven,
        successCount,
        cancelledCount,
      };
    },
    staleTime: 5000, // 5초
    refetchOnWindowFocus: true,
  });
}
