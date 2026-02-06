import { useQuery } from '@tanstack/react-query';
import { queryKeys } from '@/shared/lib/queryKeys';
import { fetchTodayBudget } from '../api';
import type { DashboardBudgetSummary } from '../types';

/**
 * 오늘 예산 현황 조회 훅
 *
 * React Query를 사용하여 서버 상태를 관리합니다.
 * - staleTime: 5000ms (예산 정보는 실시간성이 중요)
 * - refetchOnWindowFocus: true (탭 전환 시 최신 데이터 보장)
 *
 * @param today - ISO 형식 날짜 (YYYY-MM-DD)
 */
export function useTodayBudget(today: string) {
  return useQuery({
    queryKey: queryKeys.dashboard.todayBudget(today),
    queryFn: async (): Promise<DashboardBudgetSummary | null> => {
      const budget = await fetchTodayBudget(today);

      if (!budget) {
        return null;
      }

      // 파생 상태 계산 (rerender-derived-state 패턴)
      const spentAmount = budget.totalAmount - budget.remainingAmount;
      const usagePercentage = budget.totalAmount > 0 ? (spentAmount / budget.totalAmount) * 100 : 0;
      const isLowBudget = budget.remainingAmount < 1000;

      return {
        totalAmount: budget.totalAmount,
        remainingAmount: budget.remainingAmount,
        spentAmount,
        usagePercentage,
        isLowBudget,
      };
    },
    staleTime: 5000, // 5초
    refetchOnWindowFocus: true,
  });
}
