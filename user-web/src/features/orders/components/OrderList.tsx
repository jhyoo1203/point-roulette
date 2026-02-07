/**
 * 주문 목록 컴포넌트
 */

'use client';

import type { OrderResponse } from '@/shared/types/models';
import { formatPointWithUnit, formatDateTime } from '@/features/points/utils/formatPoint';

interface OrderListProps {
  orders: OrderResponse[];
}

export function OrderList({ orders }: OrderListProps) {
  if (orders.length === 0) {
    return (
      <div className="bg-white rounded-xl shadow-md border border-gray-100 p-8 text-center">
        <div className="text-5xl mb-3">📦</div>
        <p className="text-gray-500 font-medium mb-1">주문 내역이 없습니다</p>
        <p className="text-sm text-gray-400">포인트로 상품을 구매해보세요</p>
      </div>
    );
  }

  return (
    <div className="space-y-3">
      {orders.map((order) => (
        <OrderItem key={order.id} order={order} />
      ))}
    </div>
  );
}

function OrderItem({ order }: { order: OrderResponse }) {
  const isCancelled = order.status === 'CANCELLED';

  return (
    <div
      className={`bg-white rounded-xl shadow-md border border-gray-100 p-5 ${
        isCancelled ? 'opacity-60' : ''
      }`}
    >
      <div className="flex items-start gap-4">
        <div className="flex-1 min-w-0">
          <div className="flex items-start justify-between mb-2">
            <div>
              <h3 className="font-bold text-gray-900">{order.productName}</h3>
              <p className="text-sm text-gray-500">{formatDateTime(order.createdAt)}</p>
              {order.quantity > 1 && (
                <p className="text-xs text-gray-500 mt-1">수량: {order.quantity}개</p>
              )}
            </div>
            <OrderStatusBadge status={order.status} />
          </div>

          <div className="flex items-center justify-between">
            <span className="text-sm text-gray-600">
              {isCancelled ? '환불 포인트' : '사용 포인트'}
            </span>
            <span className={`font-bold ${isCancelled ? 'text-gray-500' : 'text-indigo-600'}`}>
              {isCancelled ? '+' : ''}
              {formatPointWithUnit(order.totalPrice)}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}

function OrderStatusBadge({ status }: { status: string }) {
  const config = {
    COMPLETED: {
      label: '완료',
      className: 'bg-green-100 text-green-700',
    },
    CANCELLED: {
      label: '취소됨',
      className: 'bg-red-100 text-red-700',
    },
  };

  const { label, className } = config[status as keyof typeof config] || {
    label: status,
    className: 'bg-gray-100 text-gray-700',
  };

  return (
    <span className={`px-2.5 py-1 text-xs font-semibold rounded-full whitespace-nowrap ${className}`}>
      {label}
    </span>
  );
}
