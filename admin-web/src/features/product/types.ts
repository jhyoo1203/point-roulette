import type { PaginationParams } from '@/shared/types/api';

export type ProductStatus = 'ACTIVE' | 'INACTIVE';

export interface ProductResponse {
  id: number;
  name: string;
  price: number;
  stock: number;
  description?: string;
  status: ProductStatus;
  createdAt: string;
  updatedAt: string;
}

export interface ProductCreateRequest {
  name: string;
  price: number;
  stock: number;
  description?: string;
}

export interface ProductUpdateRequest {
  name?: string;
  price?: number;
  stock?: number;
  description?: string;
}

export interface ProductListParams extends PaginationParams {
  status?: ProductStatus;
}
