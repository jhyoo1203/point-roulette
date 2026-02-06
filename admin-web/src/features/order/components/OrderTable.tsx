import { XCircle } from 'lucide-react';
import { Badge } from '@/shared/components/ui/badge';
import { Button } from '@/shared/components/ui/button';
import { Skeleton } from '@/shared/components/ui/skeleton';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/shared/components/ui/table';
import type { OrderResponse } from '../types';

interface OrderTableProps {
  orders: OrderResponse[];
  isLoading: boolean;
  onCancel: (order: OrderResponse) => void;
}

function formatPrice(price: number) {
  return price.toLocaleString('ko-KR');
}

function formatDate(dateStr: string) {
  return new Date(dateStr).toLocaleString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
}

export default function OrderTable({
  orders,
  isLoading,
  onCancel,
}: OrderTableProps) {
  if (isLoading) {
    return <OrderTableSkeleton />;
  }

  if (orders.length === 0) {
    return (
      <div className="flex h-40 items-center justify-center text-muted-foreground">
        주문 내역이 없습니다.
      </div>
    );
  }

  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead className="w-[60px]">주문ID</TableHead>
          <TableHead className="w-[80px]">사용자ID</TableHead>
          <TableHead>상품명</TableHead>
          <TableHead className="w-[60px] text-right">수량</TableHead>
          <TableHead className="w-[100px] text-right">총가격</TableHead>
          <TableHead className="w-[80px]">상태</TableHead>
          <TableHead className="w-[140px]">주문일시</TableHead>
          <TableHead className="w-[80px] text-right">관리</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {orders.map((order) => (
          <TableRow key={order.id}>
            <TableCell>{order.id}</TableCell>
            <TableCell>{order.userId}</TableCell>
            <TableCell className="font-medium">{order.productName}</TableCell>
            <TableCell className="text-right">{order.quantity}</TableCell>
            <TableCell className="text-right">
              {formatPrice(order.totalPrice)}원
            </TableCell>
            <TableCell>
              <Badge
                variant={order.status === 'COMPLETED' ? 'default' : 'secondary'}
              >
                {order.status === 'COMPLETED' ? '완료' : '취소'}
              </Badge>
            </TableCell>
            <TableCell>{formatDate(order.createdAt)}</TableCell>
            <TableCell className="text-right">
              {order.status === 'COMPLETED' && (
                <Button
                  variant="ghost"
                  size="icon-xs"
                  onClick={() => onCancel(order)}
                >
                  <XCircle className="text-destructive" />
                </Button>
              )}
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
}

function OrderTableSkeleton() {
  return (
    <div className="space-y-3">
      {Array.from({ length: 5 }).map((_, i) => (
        <Skeleton key={i} className="h-10 w-full" />
      ))}
    </div>
  );
}
