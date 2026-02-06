import { ConfirmDialog } from '@/shared/components/ui/confirm-dialog';
import { useCancelParticipation } from '../hooks/useCancelParticipation';
import type { RouletteParticipationResponse } from '../types';

interface ParticipationCancelDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  participation: RouletteParticipationResponse | null;
}

export function ParticipationCancelDialog({
  open,
  onOpenChange,
  participation,
}: ParticipationCancelDialogProps) {
  const { mutate: cancelParticipation, isPending } = useCancelParticipation();

  const handleConfirm = () => {
    if (!participation) return;

    cancelParticipation(participation.id, {
      onSuccess: () => {
        onOpenChange(false);
      },
    });
  };

  if (!participation) return null;

  return (
    <ConfirmDialog
      open={open}
      onOpenChange={onOpenChange}
      title="룰렛 참여 취소"
      description={`${participation.userName}님의 룰렛 참여를 취소하시겠습니까?`}
      confirmText="취소 처리"
      cancelText="닫기"
      onConfirm={handleConfirm}
      variant="destructive"
      loading={isPending}
      impactWarning={
        <div>
          <p className="font-semibold">영향 범위</p>
          <ul className="mt-1 list-inside list-disc space-y-1">
            <li>예산이 복구됩니다 (+{participation.wonAmount.toLocaleString()}p)</li>
            <li>사용자 포인트가 회수됩니다 (-{participation.wonAmount.toLocaleString()}p)</li>
            <li>참여 상태가 CANCELLED로 변경됩니다</li>
          </ul>
        </div>
      }
    />
  );
}
