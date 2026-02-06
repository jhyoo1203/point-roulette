import { memo } from 'react';
import { Coins } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/shared/components/ui/card';
import { Skeleton } from '@/shared/components/ui/skeleton';

interface TotalPointsCardProps {
  totalPoints: number;
  successCount: number;
  isLoading: boolean;
}

/**
 * 총 지급 포인트 카드 컴포넌트
 *
 * 오늘 총 지급된 포인트와 성공 건수를 표시합니다.
 *
 * React Best Practices:
 * - memo로 리렌더 최적화 (rerender-memo)
 * - 단순 표시 컴포넌트로 props 의존성 최소화
 */
export const TotalPointsCard = memo<TotalPointsCardProps>(
  ({ totalPoints, successCount, isLoading }) => {
    if (isLoading) {
      return (
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Coins className="h-5 w-5" />
              <span>지급 포인트</span>
            </CardTitle>
          </CardHeader>
          <CardContent>
            <Skeleton className="h-10 w-24" />
          </CardContent>
        </Card>
      );
    }

    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Coins className="h-5 w-5" />
            <span>지급 포인트</span>
          </CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-3xl font-bold">{totalPoints.toLocaleString()}p</p>
          <p className="mt-2 text-sm text-muted-foreground">
            오늘 총 지급 포인트 ({successCount.toLocaleString()}건)
          </p>
        </CardContent>
      </Card>
    );
  },
);

TotalPointsCard.displayName = 'TotalPointsCard';
