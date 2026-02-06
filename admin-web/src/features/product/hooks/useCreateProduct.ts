import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { apiPost } from '@/shared/lib/apiClient';
import { queryKeys } from '@/shared/lib/queryKeys';
import type { ApiErrorResponse } from '@/shared/types/api';
import type { ProductCreateRequest, ProductResponse } from '../types';

export function useCreateProduct() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: ProductCreateRequest) =>
      apiPost<ProductResponse>('/api/v1/admin/products', data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.products.all });
      toast.success('상품이 등록되었습니다.');
    },
    onError: (error: ApiErrorResponse) => {
      const message =
        error.errors
          ? Object.values(error.errors).join(', ')
          : '상품 등록에 실패했습니다.';
      toast.error(message);
    },
  });
}
