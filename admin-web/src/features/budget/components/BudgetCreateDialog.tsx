import { useState } from 'react';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/shared/components/ui/dialog';
import { Button } from '@/shared/components/ui/button';
import { Input } from '@/shared/components/ui/input';
import { Label } from '@/shared/components/ui/label';
import { useCreateBudget } from '../hooks/useCreateBudget';
import type { BudgetCreateRequest } from '../types';

interface BudgetCreateDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function BudgetCreateDialog({ open, onOpenChange }: BudgetCreateDialogProps) {
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [showConfirm, setShowConfirm] = useState(false);

  const { mutate: createBudget, isPending } = useCreateBudget();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setShowConfirm(true);
  };

  const handleConfirm = () => {
    const request: BudgetCreateRequest = {
      startDate,
      endDate,
    };

    createBudget(request, {
      onSuccess: () => {
        setShowConfirm(false);
        onOpenChange(false);
        setStartDate('');
        setEndDate('');
      },
    });
  };

  const handleCancel = () => {
    if (showConfirm) {
      setShowConfirm(false);
    } else {
      onOpenChange(false);
      setStartDate('');
      setEndDate('');
    }
  };

  const getDayCount = () => {
    if (!startDate || !endDate) return 0;
    const start = new Date(startDate);
    const end = new Date(endDate);
    const diffTime = Math.abs(end.getTime() - start.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays + 1;
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>
            {showConfirm ? '일일 예산 생성 확인' : '일일 예산 생성'}
          </DialogTitle>
          <DialogDescription>
            {showConfirm
              ? '다음 내용으로 예산을 생성하시겠습니까?'
              : '날짜 범위를 선택하여 일일 예산을 생성합니다.'}
          </DialogDescription>
        </DialogHeader>

        {!showConfirm ? (
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="startDate">시작일</Label>
              <Input
                id="startDate"
                type="date"
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="endDate">종료일</Label>
              <Input
                id="endDate"
                type="date"
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
                min={startDate}
                required
              />
            </div>

            <DialogFooter>
              <Button type="button" variant="outline" onClick={handleCancel}>
                취소
              </Button>
              <Button type="submit" disabled={!startDate || !endDate}>
                다음
              </Button>
            </DialogFooter>
          </form>
        ) : (
          <>
            <div className="space-y-3">
              <div className="rounded-md bg-muted p-3 text-sm">
                <p>
                  <strong>기간:</strong> {startDate} ~ {endDate}
                </p>
                <p>
                  <strong>생성될 예산 수:</strong> {getDayCount()}개
                </p>
                <p>
                  <strong>일일 예산:</strong> 100,000p
                </p>
              </div>

              <div className="rounded-md bg-amber-50 p-3 text-sm text-amber-800 dark:bg-amber-900/20 dark:text-amber-200">
                <p className="font-semibold">영향 범위</p>
                <ul className="mt-1 list-inside list-disc space-y-1">
                  <li>각 날짜별로 100,000p의 예산이 생성됩니다</li>
                  <li>이미 존재하는 날짜는 건너뜁니다</li>
                </ul>
              </div>
            </div>

            <DialogFooter>
              <Button variant="outline" onClick={handleCancel} disabled={isPending}>
                취소
              </Button>
              <Button onClick={handleConfirm} disabled={isPending}>
                {isPending ? '생성 중...' : '확인'}
              </Button>
            </DialogFooter>
          </>
        )}
      </DialogContent>
    </Dialog>
  );
}
