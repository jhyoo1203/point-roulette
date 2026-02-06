import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { apiDelete } from '@/shared/lib/apiClient';
import { queryKeys } from '@/shared/lib/queryKeys';
import type { ApiErrorResponse } from '@/shared/types/api';

export function useDeleteProduct() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) =>
      apiDelete<void>(`/api/v1/admin/products/${id}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.products.all });
      toast.success('상품이 삭제되었습니다.');
    },
    onError: (error: ApiErrorResponse) => {
      const message =
        error.errors
          ? Object.values(error.errors).join(', ')
          : '상품 삭제에 실패했습니다.';
      toast.error(message);
    },
  });
}
