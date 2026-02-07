/**
 * 주문 내역 조회 Hook
 */

'use client';

import { useQuery } from '@tanstack/react-query';
import { get } from '@/shared/lib/apiClient';
import { queryKeys } from '@/shared/lib/queryKeys';
import { ENDPOINTS } from '@/shared/constants/endpoints';
import type { PaginationResponse } from '@/shared/types/api';
import type { OrderResponse } from '@/shared/types/models';

export function useOrders(
  userId: number,
  params?: { page?: number; size?: number; sort?: string }
) {
  return useQuery({
    queryKey: queryKeys.orders.list(userId, params),
    queryFn: async () => {
      const searchParams = new URLSearchParams();
      if (params?.page !== undefined) searchParams.append('page', params.page.toString());
      if (params?.size !== undefined) searchParams.append('size', params.size.toString());
      if (params?.sort) searchParams.append('sort', params.sort);

      const url = `${ENDPOINTS.ORDERS(userId)}${searchParams.toString() ? `?${searchParams.toString()}` : ''}`;
      const data = await get<PaginationResponse<OrderResponse>>(url);
      return data;
    },
    staleTime: 30000, // 30초
    refetchOnWindowFocus: true,
    enabled: !!userId,
  });
}
