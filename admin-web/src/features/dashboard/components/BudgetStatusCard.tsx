import { memo } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/shared/components/ui/card';
import { Progress } from '@/shared/components/ui/progress';
import { Skeleton } from '@/shared/components/ui/skeleton';
import type { DashboardBudgetSummary } from '../types';

interface BudgetStatusCardProps {
  budget: DashboardBudgetSummary | null;
  isLoading: boolean;
}

/**
 * 예산 현황 카드 컴포넌트
 *
 * 오늘의 예산 현황을 표시하며, 소진율을 프로그레스 바로 시각화합니다.
 * - 전체 예산 / 잔여 예산 / 소진율 표시
 * - 프로그레스 바로 직관적인 예산 현황 제공
 *
 * React Best Practices:
 * - memo로 리렌더 최적화 (rerender-memo)
 * - 파생 상태는 부모에서 계산하여 전달받음 (rerender-derived-state)
 */
export const BudgetStatusCard = memo<BudgetStatusCardProps>(({ budget, isLoading }) => {
  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>오늘 예산 현황</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <Skeleton className="h-8 w-full" />
          <Skeleton className="h-4 w-3/4" />
          <Skeleton className="h-2 w-full" />
        </CardContent>
      </Card>
    );
  }

  if (!budget) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>오늘 예산 현황</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-sm text-muted-foreground">오늘의 예산 정보가 없습니다.</p>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>오늘 예산 현황</CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        {/* 예산 정보 */}
        <div className="space-y-2">
          <div className="flex items-baseline justify-between">
            <span className="text-2xl font-bold">{budget.remainingAmount.toLocaleString()}p</span>
            <span className="text-sm text-muted-foreground">
              / {budget.totalAmount.toLocaleString()}p
            </span>
          </div>
          <p className="text-sm text-muted-foreground">
            잔여 {budget.remainingAmount.toLocaleString()}p · 사용{' '}
            {budget.spentAmount.toLocaleString()}p
          </p>
        </div>

        {/* 소진율 프로그레스 바 */}
        <div className="space-y-2">
          <div className="flex items-center justify-between text-sm">
            <span className="text-muted-foreground">소진율</span>
            <span className="font-medium">{budget.usagePercentage.toFixed(1)}%</span>
          </div>
          <Progress
            value={budget.usagePercentage}
            className={budget.usagePercentage > 90 ? 'bg-destructive/20' : ''}
          />
        </div>
      </CardContent>
    </Card>
  );
});

BudgetStatusCard.displayName = 'BudgetStatusCard';
