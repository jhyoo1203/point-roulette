import { memo } from 'react';
import { AlertTriangle } from 'lucide-react';
import { Alert, AlertDescription, AlertTitle } from '@/shared/components/ui/alert';

interface BudgetWarningProps {
  remainingAmount: number;
  isLowBudget: boolean;
}

/**
 * 예산 소진 경고 컴포넌트
 *
 * 잔여 예산이 1,000p 미만일 때 시각적 경고를 표시합니다.
 *
 * React Best Practices:
 * - memo로 리렌더 최적화 (rerender-memo)
 * - 조건부 렌더링은 컴포넌트 외부에서 처리 (rendering-conditional-render)
 */
export const BudgetWarning = memo<BudgetWarningProps>(({ remainingAmount, isLowBudget }) => {
  // 조건부 렌더링 (부모 컴포넌트에서 isLowBudget이 true일 때만 렌더)
  if (!isLowBudget) {
    return null;
  }

  return (
    <Alert variant="destructive">
      <AlertTriangle className="h-4 w-4" />
      <AlertTitle>예산 소진 경고</AlertTitle>
      <AlertDescription>
        오늘의 잔여 예산이 {remainingAmount.toLocaleString()}p로 1,000p 미만입니다. 예산을 확인해
        주세요.
      </AlertDescription>
    </Alert>
  );
});

BudgetWarning.displayName = 'BudgetWarning';
