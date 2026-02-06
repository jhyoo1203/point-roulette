import { Pencil, Trash2 } from 'lucide-react';
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
import type { ProductResponse } from '../types';

interface ProductTableProps {
  products: ProductResponse[];
  isLoading: boolean;
  onEdit: (product: ProductResponse) => void;
  onDelete: (product: ProductResponse) => void;
}

function formatPrice(price: number) {
  return price.toLocaleString('ko-KR');
}

function formatDate(dateStr: string) {
  return new Date(dateStr).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  });
}

export default function ProductTable({
  products,
  isLoading,
  onEdit,
  onDelete,
}: ProductTableProps) {
  if (isLoading) {
    return <ProductTableSkeleton />;
  }

  if (products.length === 0) {
    return (
      <div className="flex h-40 items-center justify-center text-muted-foreground">
        등록된 상품이 없습니다.
      </div>
    );
  }

  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead className="w-[60px]">ID</TableHead>
          <TableHead>상품명</TableHead>
          <TableHead className="w-[100px] text-right">가격</TableHead>
          <TableHead className="w-[80px] text-right">재고</TableHead>
          <TableHead className="w-[80px]">상태</TableHead>
          <TableHead className="w-[100px]">등록일</TableHead>
          <TableHead className="w-[100px] text-right">관리</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {products.map((product) => (
          <TableRow key={product.id}>
            <TableCell>{product.id}</TableCell>
            <TableCell className="font-medium">{product.name}</TableCell>
            <TableCell className="text-right">
              {formatPrice(product.price)}원
            </TableCell>
            <TableCell className="text-right">{product.stock}</TableCell>
            <TableCell>
              <Badge
                variant={product.status === 'ACTIVE' ? 'default' : 'secondary'}
              >
                {product.status === 'ACTIVE' ? '활성' : '비활성'}
              </Badge>
            </TableCell>
            <TableCell>{formatDate(product.createdAt)}</TableCell>
            <TableCell className="text-right">
              <div className="flex justify-end gap-1">
                <Button
                  variant="ghost"
                  size="icon-xs"
                  onClick={() => onEdit(product)}
                >
                  <Pencil />
                </Button>
                <Button
                  variant="ghost"
                  size="icon-xs"
                  onClick={() => onDelete(product)}
                >
                  <Trash2 className="text-destructive" />
                </Button>
              </div>
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
}

function ProductTableSkeleton() {
  return (
    <div className="space-y-3">
      {Array.from({ length: 5 }).map((_, i) => (
        <Skeleton key={i} className="h-10 w-full" />
      ))}
    </div>
  );
}
