import { Input } from '@/shared/components/ui/input';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/shared/components/ui/select';
import type { OrderStatus } from '../types';

interface OrderFiltersProps {
  userId: string;
  onUserIdChange: (value: string) => void;
  status: OrderStatus | 'ALL';
  onStatusChange: (value: OrderStatus | 'ALL') => void;
}

export default function OrderFilters({
  userId,
  onUserIdChange,
  status,
  onStatusChange,
}: OrderFiltersProps) {
  return (
    <div className="flex items-center gap-2">
      <Input
        type="number"
        placeholder="사용자 ID로 검색"
        className="w-[180px]"
        value={userId}
        onChange={(e) => onUserIdChange(e.target.value)}
      />
      <Select value={status} onValueChange={onStatusChange}>
        <SelectTrigger className="w-[140px]">
          <SelectValue placeholder="주문 상태" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem value="ALL">전체</SelectItem>
          <SelectItem value="COMPLETED">완료</SelectItem>
          <SelectItem value="CANCELLED">취소</SelectItem>
        </SelectContent>
      </Select>
    </div>
  );
}
