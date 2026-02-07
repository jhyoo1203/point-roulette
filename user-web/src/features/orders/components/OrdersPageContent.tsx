/**
 * 주문 내역 페이지 클라이언트 컴포넌트
 */

'use client';

import { useState } from 'react';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { useOrders } from '../hooks/useOrders';
import { OrderList } from './OrderList';

export function OrdersPageContent() {
  const { user } = useAuth();
  const [page, setPage] = useState(0);
  const pageSize = 10;

  const { data, isLoading, error } = useOrders(user?.id || 0, {
    page,
    size: pageSize,
    sort: 'createdAt,desc',
  });

  if (isLoading) {
    return (
      <div className="p-5 space-y-5">
        <div className="bg-indigo-600 rounded-2xl p-6 shadow-lg animate-pulse h-24" />
        <div className="space-y-3">
          {[1, 2, 3].map((i) => (
            <div key={i} className="bg-white rounded-xl shadow-md border border-gray-100 p-5 animate-pulse h-24" />
          ))}
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-5">
        <div className="bg-red-50 border border-red-200 rounded-xl p-4">
          <p className="text-red-800 font-semibold">주문 내역 조회 실패</p>
          <p className="text-sm text-red-600 mt-1">잠시 후 다시 시도해주세요.</p>
        </div>
      </div>
    );
  }

  const orders = data?.content || [];
  const hasNext = data?.hasNext || false;
  const hasPrevious = data?.hasPrevious || false;
  const totalPages = data?.totalPages || 0;

  return (
    <div className="p-5 space-y-5">
      {/* 헤더 */}
      <div className="bg-indigo-600 rounded-2xl p-6 text-white shadow-lg">
        <h2 className="text-2xl font-bold mb-2">주문 내역</h2>
        <p className="text-indigo-100 text-sm">포인트로 구매한 상품을 확인하세요</p>
      </div>

      {/* 주문 목록 */}
      <OrderList orders={orders} />

      {/* 페이지네이션 */}
      {totalPages > 1 && (
        <div className="flex items-center justify-center gap-2">
          <button
            onClick={() => setPage(page - 1)}
            disabled={!hasPrevious}
            className="px-4 py-2 rounded-lg border border-gray-300 text-sm font-medium text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            이전
          </button>
          <span className="px-4 py-2 text-sm text-gray-700">
            {page + 1} / {totalPages}
          </span>
          <button
            onClick={() => setPage(page + 1)}
            disabled={!hasNext}
            className="px-4 py-2 rounded-lg border border-gray-300 text-sm font-medium text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            다음
          </button>
        </div>
      )}
    </div>
  );
}
