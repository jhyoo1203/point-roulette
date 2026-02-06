import { useQuery } from '@tanstack/react-query';
import { apiGet } from '@/shared/lib/apiClient';
import { queryKeys } from '@/shared/lib/queryKeys';
import type { ProductResponse } from '../types';

export function useProduct(id: number | null) {
  return useQuery({
    queryKey: queryKeys.products.detail(id!),
    queryFn: () => apiGet<ProductResponse>(`/api/v1/admin/products/${id}`),
    enabled: id !== null,
  });
}
