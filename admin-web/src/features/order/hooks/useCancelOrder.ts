import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { apiPost } from '@/shared/lib/apiClient';
import { queryKeys } from '@/shared/lib/queryKeys';
import type { ApiErrorResponse } from '@/shared/types/api';
import type { OrderResponse } from '../types';

export function useCancelOrder() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (orderId: number) =>
      apiPost<OrderResponse>(`/api/v1/admin/orders/${orderId}/cancel`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.orders.all });
      toast.success('주문이 취소되었습니다. 포인트가 환불되었습니다.');
    },
    onError: (error: ApiErrorResponse) => {
      const message =
        error.errors
          ? Object.values(error.errors).join(', ')
          : '주문 취소에 실패했습니다.';
      toast.error(message);
    },
  });
}
