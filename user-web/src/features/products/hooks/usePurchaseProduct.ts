/**
 * 상품 구매 Mutation Hook
 */

'use client';

import { useMutation, useQueryClient } from '@tanstack/react-query';
import { post } from '@/shared/lib/apiClient';
import { queryKeys } from '@/shared/lib/queryKeys';
import { ENDPOINTS } from '@/shared/constants/endpoints';
import type { OrderCreateRequest, OrderResponse } from '@/shared/types/models';

export function usePurchaseProduct(userId: number) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (request: OrderCreateRequest) => {
      const data = await post<OrderResponse>(
        ENDPOINTS.PRODUCT_PURCHASE(userId),
        request
      );
      return data;
    },
    onSuccess: () => {
      // 포인트 잔액 캐시 무효화
      queryClient.invalidateQueries({
        queryKey: queryKeys.points.balance(userId),
      });

      // 주문 내역 캐시 무효화
      queryClient.invalidateQueries({
        queryKey: queryKeys.orders.all,
      });

      // 상품 목록 캐시 무효화 (재고 변경)
      queryClient.invalidateQueries({
        queryKey: queryKeys.products.all,
      });
    },
  });
}
