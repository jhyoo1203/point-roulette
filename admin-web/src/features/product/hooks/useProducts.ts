import { useQuery } from '@tanstack/react-query';
import { apiGet } from '@/shared/lib/apiClient';
import { queryKeys } from '@/shared/lib/queryKeys';
import type { PaginatedData } from '@/shared/types/api';
import type { ProductListParams, ProductResponse } from '../types';

export function useProducts(params: ProductListParams = {}) {
  return useQuery({
    queryKey: queryKeys.products.list(params),
    queryFn: () =>
      apiGet<PaginatedData<ProductResponse>>('/api/v1/admin/products', {
        ...params,
      }),
    staleTime: 30_000,
  });
}
