import { useTodayBudget, useTodayRouletteStats } from '@/features/dashboard/hooks';
import {
  BudgetStatusCard,
  ParticipantCountCard,
  TotalPointsCard,
  BudgetWarning,
} from '@/features/dashboard/components';
import { getTodayInKST } from '@/shared/lib/dateUtils';

/**
 * 대시보드 페이지
 *
 * 오늘의 예산 현황, 룰렛 참여 통계를 한눈에 보여줍니다.
 * - 오늘 예산 현황 (전체/잔여 예산, 소진율)
 * - 오늘 룰렛 참여 인원수
 * - 오늘 총 지급 포인트
 * - 예산 소진 경고 (잔여 1,000p 미만 시)
 *
 * React Best Practices:
 * - 병렬 데이터 페칭 (async-parallel): 두 개의 독립적인 API 호출을 동시에 실행
 * - 파생 상태는 커스텀 훅에서 계산하여 전달 (rerender-derived-state)
 * - 컴포넌트는 memo로 최적화되어 불필요한 리렌더 방지 (rerender-memo)
 */
export default function DashboardPage() {
  // 오늘 날짜 (한국 시간대 기준, ISO 형식)
  const today = getTodayInKST();

  // 병렬 데이터 페칭 (async-parallel 패턴)
  const { data: budgetData, isLoading: isBudgetLoading } = useTodayBudget(today);
  const { data: rouletteStats, isLoading: isRouletteLoading } = useTodayRouletteStats(today);

  return (
    <div className="space-y-6">
      {/* 페이지 헤더 */}
      <div>
        <h1 className="text-3xl font-bold">대시보드</h1>
        <p className="mt-2 text-muted-foreground">오늘의 예산 및 룰렛 참여 현황을 확인하세요.</p>
      </div>

      {/* 예산 소진 경고 (잔여 1,000p 미만 시 표시) */}
      {budgetData?.isLowBudget && (
        <BudgetWarning
          remainingAmount={budgetData.remainingAmount}
          isLowBudget={budgetData.isLowBudget}
        />
      )}

      {/* 통계 카드 그리드 */}
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        {/* 오늘 예산 현황 */}
        <BudgetStatusCard budget={budgetData ?? null} isLoading={isBudgetLoading} />

        {/* 참여자 수 */}
        <ParticipantCountCard
          count={rouletteStats?.participantCount ?? 0}
          isLoading={isRouletteLoading}
        />

        {/* 총 지급 포인트 */}
        <TotalPointsCard
          totalPoints={rouletteStats?.totalPointsGiven ?? 0}
          successCount={rouletteStats?.successCount ?? 0}
          isLoading={isRouletteLoading}
        />
      </div>
    </div>
  );
}
