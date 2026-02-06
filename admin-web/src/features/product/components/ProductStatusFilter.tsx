import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/shared/components/ui/select';
import type { ProductStatus } from '../types';

interface ProductStatusFilterProps {
  value: ProductStatus | 'ALL';
  onValueChange: (value: ProductStatus | 'ALL') => void;
}

export default function ProductStatusFilter({
  value,
  onValueChange,
}: ProductStatusFilterProps) {
  return (
    <Select value={value} onValueChange={onValueChange}>
      <SelectTrigger className="w-[140px]">
        <SelectValue placeholder="상태 필터" />
      </SelectTrigger>
      <SelectContent>
        <SelectItem value="ALL">전체</SelectItem>
        <SelectItem value="ACTIVE">활성</SelectItem>
        <SelectItem value="INACTIVE">비활성</SelectItem>
      </SelectContent>
    </Select>
  );
}
