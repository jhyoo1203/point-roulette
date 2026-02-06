import { useQuery } from '@tanstack/react-query';
import { apiGet } from '@/shared/lib/apiClient';
import { queryKeys } from '@/shared/lib/queryKeys';
import type { PaginatedData } from '@/shared/types/api';
import type { OrderListParams, OrderResponse } from '../types';

export function useOrders(params: OrderListParams = {}) {
  return useQuery({
    queryKey: queryKeys.orders.list(params),
    queryFn: () =>
      apiGet<PaginatedData<OrderResponse>>('/api/v1/admin/orders', {
        ...params,
      }),
    staleTime: 30_000,
  });
}
