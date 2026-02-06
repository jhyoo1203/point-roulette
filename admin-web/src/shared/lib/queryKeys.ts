import type { ProductListParams } from '@/features/product/types';
import type { OrderListParams } from '@/features/order/types';
import type { BudgetListParams } from '@/features/budget/types';
import type { RouletteParticipationListParams } from '@/features/roulette/types';

export const queryKeys = {
  budget: {
    all: ['budget'] as const,
    daily: (params?: BudgetListParams) => ['budget', 'daily', params] as const,
  },
  products: {
    all: ['products'] as const,
    list: (params?: ProductListParams) => ['products', 'list', params] as const,
    detail: (id: number) => ['products', 'detail', id] as const,
  },
  orders: {
    all: ['orders'] as const,
    list: (params?: OrderListParams) => ['orders', 'list', params] as const,
  },
  roulette: {
    all: ['roulette'] as const,
    participations: (params?: RouletteParticipationListParams) => ['roulette', 'participations', params] as const,
  },
} as const;
