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
import type { ProductResponse } from '../types';

interface ProductDeleteDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  product: ProductResponse | null;
  onConfirm: (id: number) => void;
  isPending: boolean;
}

export default function ProductDeleteDialog({
  open,
  onOpenChange,
  product,
  onConfirm,
  isPending,
}: ProductDeleteDialogProps) {
  if (!product) return null;

  return (
    <AlertDialog open={open} onOpenChange={onOpenChange}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>상품 삭제</AlertDialogTitle>
          <AlertDialogDescription>
            <strong>{product.name}</strong> 상품을 삭제하시겠습니까? 삭제된
            상품은 비활성 상태로 전환됩니다.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel disabled={isPending}>취소</AlertDialogCancel>
          <AlertDialogAction
            variant="destructive"
            disabled={isPending}
            onClick={() => onConfirm(product.id)}
          >
            {isPending ? '처리 중...' : '삭제'}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
