import { ChevronLeft, ChevronRight } from 'lucide-react';
import { Button } from '@/shared/components/ui/button';

interface ProductPaginationProps {
  currentPage: number;
  totalPages: number;
  hasPrevious: boolean;
  hasNext: boolean;
  onPageChange: (page: number) => void;
}

export default function ProductPagination({
  currentPage,
  totalPages,
  hasPrevious,
  hasNext,
  onPageChange,
}: ProductPaginationProps) {
  if (totalPages <= 1) return null;

  return (
    <div className="flex items-center justify-center gap-4 pt-4">
      <Button
        variant="outline"
        size="sm"
        disabled={!hasPrevious}
        onClick={() => onPageChange(currentPage - 1)}
      >
        <ChevronLeft />
        이전
      </Button>
      <span className="text-sm text-muted-foreground">
        {currentPage + 1} / {totalPages}
      </span>
      <Button
        variant="outline"
        size="sm"
        disabled={!hasNext}
        onClick={() => onPageChange(currentPage + 1)}
      >
        다음
        <ChevronRight />
      </Button>
    </div>
  );
}
