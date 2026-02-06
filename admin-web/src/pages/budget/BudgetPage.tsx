import { useState } from 'react';
import { Button } from '@/shared/components/ui/button';
import { Input } from '@/shared/components/ui/input';
import { Label } from '@/shared/components/ui/label';
import { useBudgets } from '@/features/budget/hooks/useBudgets';
import { useRouletteParticipations } from '@/features/roulette/hooks/useRouletteParticipations';
import { BudgetTable } from '@/features/budget/components/BudgetTable';
import { BudgetCreateDialog } from '@/features/budget/components/BudgetCreateDialog';
import { ParticipationTable } from '@/features/roulette/components/ParticipationTable';

export default function BudgetPage() {
  const today = new Date().toISOString().split('T')[0];

  // 예산 조회 상태
  const [budgetStartDate, setBudgetStartDate] = useState(today);
  const [budgetEndDate, setBudgetEndDate] = useState(today);
  const [createDialogOpen, setCreateDialogOpen] = useState(false);

  // 룰렛 참여 조회 상태
  const [participationStartDate, setParticipationStartDate] = useState(today);
  const [participationEndDate, setParticipationEndDate] = useState(today);

  // 데이터 조회
  const { data: budgetData, isLoading: budgetLoading } = useBudgets({
    startDate: budgetStartDate,
    endDate: budgetEndDate,
  });

  const { data: participationData, isLoading: participationLoading } =
    useRouletteParticipations({
      startDate: participationStartDate,
      endDate: participationEndDate,
    });

  return (
    <div className="space-y-8">
      {/* 헤더 */}
      <div>
        <h1 className="text-2xl font-bold">예산 관리</h1>
        <p className="mt-2 text-muted-foreground">
          일일 예산 설정 및 룰렛 참여 관리
        </p>
      </div>

      {/* 일일 예산 섹션 */}
      <section className="space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="text-xl font-semibold">일일 예산</h2>
          <Button onClick={() => setCreateDialogOpen(true)}>예산 생성</Button>
        </div>

        <div className="flex items-end gap-4">
          <div className="flex-1 space-y-2">
            <Label htmlFor="budgetStartDate">시작일</Label>
            <Input
              id="budgetStartDate"
              type="date"
              value={budgetStartDate}
              onChange={(e) => setBudgetStartDate(e.target.value)}
            />
          </div>
          <div className="flex-1 space-y-2">
            <Label htmlFor="budgetEndDate">종료일</Label>
            <Input
              id="budgetEndDate"
              type="date"
              value={budgetEndDate}
              onChange={(e) => setBudgetEndDate(e.target.value)}
              min={budgetStartDate}
            />
          </div>
        </div>

        <BudgetTable
          budgets={budgetData?.content || []}
          loading={budgetLoading}
        />
      </section>

      {/* 룰렛 참여 이력 섹션 */}
      <section className="space-y-4">
        <h2 className="text-xl font-semibold">룰렛 참여 이력</h2>

        <div className="flex items-end gap-4">
          <div className="flex-1 space-y-2">
            <Label htmlFor="participationStartDate">시작일</Label>
            <Input
              id="participationStartDate"
              type="date"
              value={participationStartDate}
              onChange={(e) => setParticipationStartDate(e.target.value)}
            />
          </div>
          <div className="flex-1 space-y-2">
            <Label htmlFor="participationEndDate">종료일</Label>
            <Input
              id="participationEndDate"
              type="date"
              value={participationEndDate}
              onChange={(e) => setParticipationEndDate(e.target.value)}
              min={participationStartDate}
            />
          </div>
        </div>

        <ParticipationTable
          participations={participationData?.content || []}
          loading={participationLoading}
        />
      </section>

      {/* 예산 생성 다이얼로그 */}
      <BudgetCreateDialog
        open={createDialogOpen}
        onOpenChange={setCreateDialogOpen}
      />
    </div>
  );
}
