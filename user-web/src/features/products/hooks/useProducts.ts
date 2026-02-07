/**
 * 상품 목록 조회 Hook
 */

'use client';

import { useQuery } from '@tanstack/react-query';
import { get } from '@/shared/lib/apiClient';
import { queryKeys } from '@/shared/lib/queryKeys';
import { ENDPOINTS } from '@/shared/constants/endpoints';
import type { PaginationResponse } from '@/shared/types/api';
import type { ProductResponse } from '@/shared/types/models';

export function useProducts(params?: { page?: number; size?: number; status?: string }) {
  return useQuery({
    queryKey: queryKeys.products.list(params),
    queryFn: async () => {
      const searchParams = new URLSearchParams();
      if (params?.page !== undefined) searchParams.append('page', params.page.toString());
      if (params?.size !== undefined) searchParams.append('size', params.size.toString());
      if (params?.status) searchParams.append('status', params.status);

      const url = `${ENDPOINTS.PRODUCTS}${searchParams.toString() ? `?${searchParams.toString()}` : ''}`;
      const data = await get<PaginationResponse<ProductResponse>>(url);
      return data;
    },
    staleTime: 60000, // 1분 - 상품 목록은 자주 변하지 않음
    refetchOnWindowFocus: true,
  });
}
