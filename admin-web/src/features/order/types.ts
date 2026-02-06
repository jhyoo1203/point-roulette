import type { PaginationParams } from '@/shared/types/api';

export type OrderStatus = 'COMPLETED' | 'CANCELLED';

export interface OrderResponse {
  id: number;
  userId: number;
  productId: number;
  productName: string;
  quantity: number;
  totalPrice: number;
  status: OrderStatus;
  createdAt: string;
}

export interface OrderListParams extends PaginationParams {
  userId?: number;
  status?: OrderStatus;
}
