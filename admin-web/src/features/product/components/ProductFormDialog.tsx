import { type FormEvent, useEffect, useState } from 'react';
import { Button } from '@/shared/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/shared/components/ui/dialog';
import { Input } from '@/shared/components/ui/input';
import { Label } from '@/shared/components/ui/label';
import { Textarea } from '@/shared/components/ui/textarea';
import type {
  ProductCreateRequest,
  ProductResponse,
  ProductUpdateRequest,
} from '../types';

interface ProductFormDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  product: ProductResponse | null;
  onSubmitCreate: (data: ProductCreateRequest) => void;
  onSubmitUpdate: (id: number, data: ProductUpdateRequest) => void;
  isPending: boolean;
}

export default function ProductFormDialog({
  open,
  onOpenChange,
  product,
  onSubmitCreate,
  onSubmitUpdate,
  isPending,
}: ProductFormDialogProps) {
  const isEdit = product !== null;

  const [name, setName] = useState('');
  const [price, setPrice] = useState('');
  const [stock, setStock] = useState('');
  const [description, setDescription] = useState('');

  useEffect(() => {
    if (open) {
      if (product) {
        setName(product.name);
        setPrice(String(product.price));
        setStock(String(product.stock));
        setDescription(product.description ?? '');
      } else {
        setName('');
        setPrice('');
        setStock('');
        setDescription('');
      }
    }
  }, [open, product]);

  function handleSubmit(e: FormEvent) {
    e.preventDefault();

    if (isEdit) {
      onSubmitUpdate(product.id, {
        name,
        price: Number(price),
        stock: Number(stock),
        description: description || undefined,
      });
    } else {
      onSubmitCreate({
        name,
        price: Number(price),
        stock: Number(stock),
        description: description || undefined,
      });
    }
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{isEdit ? '상품 수정' : '상품 등록'}</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit} className="grid gap-4">
          <div className="grid gap-2">
            <Label htmlFor="name">상품명 *</Label>
            <Input
              id="name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="상품명을 입력하세요"
              required
            />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div className="grid gap-2">
              <Label htmlFor="price">가격 *</Label>
              <Input
                id="price"
                type="number"
                min={0}
                value={price}
                onChange={(e) => setPrice(e.target.value)}
                placeholder="0"
                required
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="stock">재고 *</Label>
              <Input
                id="stock"
                type="number"
                min={0}
                value={stock}
                onChange={(e) => setStock(e.target.value)}
                placeholder="0"
                required
              />
            </div>
          </div>
          <div className="grid gap-2">
            <Label htmlFor="description">설명</Label>
            <Textarea
              id="description"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="상품 설명을 입력하세요 (선택)"
              rows={3}
            />
          </div>
          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => onOpenChange(false)}
            >
              취소
            </Button>
            <Button type="submit" disabled={isPending}>
              {isPending ? '처리 중...' : isEdit ? '수정' : '등록'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
