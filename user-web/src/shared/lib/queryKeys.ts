/**
 * TanStack Query Key 관리
 */

export const queryKeys = {
  // Roulette
  roulette: {
    all: ['roulette'] as const,
    status: (userId: number) => ['roulette', 'status', userId] as const,
  },

  // Points
  points: {
    all: ['points'] as const,
    balance: (userId: number) => ['points', 'balance', userId] as const,
  },

  // Products
  products: {
    all: ['products'] as const,
    list: (params?: { page?: number; size?: number; status?: string }) =>
      ['products', 'list', params] as const,
  },

  // Orders
  orders: {
    all: ['orders'] as const,
    list: (userId: number, params?: { page?: number; size?: number }) =>
      ['orders', 'list', userId, params] as const,
  },
} as const;
