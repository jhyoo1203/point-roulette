import { useState } from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/shared/components/ui/table';
import { Button } from '@/shared/components/ui/button';
import { Badge } from '@/shared/components/ui/badge';
import { Skeleton } from '@/shared/components/ui/skeleton';
import { ParticipationCancelDialog } from './ParticipationCancelDialog';
import type { RouletteParticipationResponse } from '../types';

interface ParticipationTableProps {
  participations: RouletteParticipationResponse[];
  loading?: boolean;
}

export function ParticipationTable({ participations, loading }: ParticipationTableProps) {
  const [selectedParticipation, setSelectedParticipation] =
    useState<RouletteParticipationResponse | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);

  const handleCancel = (participation: RouletteParticipationResponse) => {
    setSelectedParticipation(participation);
    setDialogOpen(true);
  };

  if (loading) {
    return (
      <div className="space-y-3">
        {[...Array(5)].map((_, i) => (
          <Skeleton key={i} className="h-12 w-full" />
        ))}
      </div>
    );
  }

  if (participations.length === 0) {
    return (
      <div className="py-8 text-center text-muted-foreground">
        조회된 참여 이력이 없습니다.
      </div>
    );
  }

  return (
    <>
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>참여일</TableHead>
            <TableHead>사용자</TableHead>
            <TableHead className="text-right">획득 포인트</TableHead>
            <TableHead>상태</TableHead>
            <TableHead className="text-right">액션</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {participations.map((participation) => (
            <TableRow key={participation.id}>
              <TableCell className="font-medium">
                {participation.participatedDate}
              </TableCell>
              <TableCell>
                {participation.userName} (ID: {participation.userId})
              </TableCell>
              <TableCell className="text-right">
                {participation.wonAmount.toLocaleString()}p
              </TableCell>
              <TableCell>
                <Badge
                  variant={
                    participation.status === 'SUCCESS' ? 'default' : 'secondary'
                  }
                >
                  {participation.status === 'SUCCESS' ? '성공' : '취소됨'}
                </Badge>
              </TableCell>
              <TableCell className="text-right">
                {participation.status === 'SUCCESS' && (
                  <Button
                    size="sm"
                    variant="destructive"
                    onClick={() => handleCancel(participation)}
                  >
                    취소
                  </Button>
                )}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      <ParticipationCancelDialog
        open={dialogOpen}
        onOpenChange={setDialogOpen}
        participation={selectedParticipation}
      />
    </>
  );
}
