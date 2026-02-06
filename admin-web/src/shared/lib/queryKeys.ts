import type { PaginationParams } from '@/shared/types/api';
import type { ProductListParams } from '@/features/product/types';

export const queryKeys = {
  budget: {
    all: ['budget'] as const,
    daily: (params?: Record<string, unknown>) => ['budget', 'daily', params] as const,
  },
  products: {
    all: ['products'] as const,
    list: (params?: ProductListParams) => ['products', 'list', params] as const,
    detail: (id: number) => ['products', 'detail', id] as const,
  },
  orders: {
    all: ['orders'] as const,
    list: (params?: PaginationParams) => ['orders', 'list', params] as const,
  },
  roulette: {
    all: ['roulette'] as const,
    participations: (params?: Record<string, unknown>) => ['roulette', 'participations', params] as const,
  },
} as const;
