import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/shared/components/ui/table';
import { Skeleton } from '@/shared/components/ui/skeleton';
import type { BudgetResponse } from '../types';

interface BudgetTableProps {
  budgets: BudgetResponse[];
  loading?: boolean;
}

export function BudgetTable({ budgets, loading }: BudgetTableProps) {
  if (loading) {
    return (
      <div className="space-y-3">
        {[...Array(5)].map((_, i) => (
          <Skeleton key={i} className="h-12 w-full" />
        ))}
      </div>
    );
  }

  if (budgets.length === 0) {
    return (
      <div className="py-8 text-center text-muted-foreground">
        조회된 예산이 없습니다.
      </div>
    );
  }

  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>날짜</TableHead>
          <TableHead className="text-right">총 예산</TableHead>
          <TableHead className="text-right">남은 예산</TableHead>
          <TableHead className="text-right">소진율</TableHead>
          <TableHead className="text-right">소진 금액</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {budgets.map((budget) => {
          const usedAmount = budget.totalAmount - budget.remainingAmount;
          const usedRate = (usedAmount / budget.totalAmount) * 100;

          return (
            <TableRow key={budget.id}>
              <TableCell className="font-medium">{budget.budgetDate}</TableCell>
              <TableCell className="text-right">
                {budget.totalAmount.toLocaleString()}p
              </TableCell>
              <TableCell className="text-right">
                {budget.remainingAmount.toLocaleString()}p
              </TableCell>
              <TableCell className="text-right">
                <span
                  className={
                    usedRate >= 80
                      ? 'text-red-600'
                      : usedRate >= 50
                        ? 'text-amber-600'
                        : 'text-green-600'
                  }
                >
                  {usedRate.toFixed(1)}%
                </span>
              </TableCell>
              <TableCell className="text-right">
                {usedAmount.toLocaleString()}p
              </TableCell>
            </TableRow>
          );
        })}
      </TableBody>
    </Table>
  );
}
