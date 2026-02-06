import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/shared/components/ui/alert-dialog';
import type { OrderResponse } from '../types';

interface OrderCancelDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  order: OrderResponse | null;
  onConfirm: (id: number) => void;
  isPending: boolean;
}

function formatPrice(price: number) {
  return price.toLocaleString('ko-KR');
}

export default function OrderCancelDialog({
  open,
  onOpenChange,
  order,
  onConfirm,
  isPending,
}: OrderCancelDialogProps) {
  if (!order) return null;

  return (
    <AlertDialog open={open} onOpenChange={onOpenChange}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>주문 취소 (포인트 환불)</AlertDialogTitle>
          <AlertDialogDescription asChild>
            <div className="space-y-3">
              <p>
                <strong>주문 ID {order.id}</strong> 를 취소하시겠습니까?
              </p>
              <div className="rounded-md bg-muted p-3 text-sm">
                <p className="font-semibold">영향 범위:</p>
                <ul className="mt-2 space-y-1 list-disc list-inside">
                  <li>주문 상태: COMPLETED → CANCELLED</li>
                  <li>재고 복구: +{order.quantity}개</li>
                  <li>
                    포인트 환불: {formatPrice(order.totalPrice)}원 (유효기간 30일)
                  </li>
                </ul>
              </div>
              <p className="text-destructive text-xs">
                이 작업은 되돌릴 수 없습니다. 환불된 포인트는 30일간 유효합니다.
              </p>
            </div>
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel disabled={isPending}>취소</AlertDialogCancel>
          <AlertDialogAction
            variant="destructive"
            disabled={isPending}
            onClick={() => onConfirm(order.id)}
          >
            {isPending ? '처리 중...' : '주문 취소'}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
