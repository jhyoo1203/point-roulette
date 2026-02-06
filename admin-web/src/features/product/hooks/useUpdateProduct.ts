import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { apiPut } from '@/shared/lib/apiClient';
import { queryKeys } from '@/shared/lib/queryKeys';
import type { ApiErrorResponse } from '@/shared/types/api';
import type { ProductResponse, ProductUpdateRequest } from '../types';

export function useUpdateProduct() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: ProductUpdateRequest }) =>
      apiPut<ProductResponse>(`/api/v1/admin/products/${id}`, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.products.all });
      toast.success('상품이 수정되었습니다.');
    },
    onError: (error: ApiErrorResponse) => {
      const message =
        error.errors
          ? Object.values(error.errors).join(', ')
          : '상품 수정에 실패했습니다.';
      toast.error(message);
    },
  });
}
