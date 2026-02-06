import { memo } from 'react';
import { Users } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/shared/components/ui/card';
import { Skeleton } from '@/shared/components/ui/skeleton';

interface ParticipantCountCardProps {
  count: number;
  isLoading: boolean;
}

/**
 * 참여 인원 카드 컴포넌트
 *
 * 오늘 룰렛 참여 인원수를 표시합니다.
 *
 * React Best Practices:
 * - memo로 리렌더 최적화 (rerender-memo)
 * - 단순 표시 컴포넌트로 props 의존성 최소화
 */
export const ParticipantCountCard = memo<ParticipantCountCardProps>(({ count, isLoading }) => {
  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Users className="h-5 w-5" />
            <span>참여자 수</span>
          </CardTitle>
        </CardHeader>
        <CardContent>
          <Skeleton className="h-10 w-20" />
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Users className="h-5 w-5" />
          <span>참여자 수</span>
        </CardTitle>
      </CardHeader>
      <CardContent>
        <p className="text-3xl font-bold">{count.toLocaleString()}명</p>
        <p className="mt-2 text-sm text-muted-foreground">오늘 룰렛 참여 인원</p>
      </CardContent>
    </Card>
  );
});

ParticipantCountCard.displayName = 'ParticipantCountCard';
