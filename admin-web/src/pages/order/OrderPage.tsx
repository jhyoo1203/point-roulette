import { useState } from 'react';
import OrderCancelDialog from '@/features/order/components/OrderCancelDialog';
import OrderFilters from '@/features/order/components/OrderFilters';
import OrderTable from '@/features/order/components/OrderTable';
import { useCancelOrder } from '@/features/order/hooks/useCancelOrder';
import { useOrders } from '@/features/order/hooks/useOrders';
import type { OrderResponse, OrderStatus } from '@/features/order/types';
import ProductPagination from '@/features/product/components/ProductPagination';

export default function OrderPage() {
  const [page, setPage] = useState(0);
  const [userIdFilter, setUserIdFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState<OrderStatus | 'ALL'>('ALL');
  const [cancelOpen, setCancelOpen] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState<OrderResponse | null>(null);

  const { data, isLoading } = useOrders({
    page,
    size: 10,
    userId: userIdFilter ? Number(userIdFilter) : undefined,
    status: statusFilter === 'ALL' ? undefined : statusFilter,
  });

  const cancelMutation = useCancelOrder();

  function handleUserIdChange(value: string) {
    setUserIdFilter(value);
    setPage(0);
  }

  function handleStatusChange(value: OrderStatus | 'ALL') {
    setStatusFilter(value);
    setPage(0);
  }

  function handleOpenCancel(order: OrderResponse) {
    setSelectedOrder(order);
    setCancelOpen(true);
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">주문 관리</h1>
      </div>

      <OrderFilters
        userId={userIdFilter}
        onUserIdChange={handleUserIdChange}
        status={statusFilter}
        onStatusChange={handleStatusChange}
      />

      <OrderTable
        orders={data?.content ?? []}
        isLoading={isLoading}
        onCancel={handleOpenCancel}
      />

      {data && (
        <ProductPagination
          currentPage={data.currentPage}
          totalPages={data.totalPages}
          hasPrevious={data.hasPrevious}
          hasNext={data.hasNext}
          onPageChange={setPage}
        />
      )}

      <OrderCancelDialog
        open={cancelOpen}
        onOpenChange={setCancelOpen}
        order={selectedOrder}
        onConfirm={(id) => {
          cancelMutation.mutate(id, { onSuccess: () => setCancelOpen(false) });
        }}
        isPending={cancelMutation.isPending}
      />
    </div>
  );
}
