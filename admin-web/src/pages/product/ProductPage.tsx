import { useState } from 'react';
import { Plus } from 'lucide-react';
import { Button } from '@/shared/components/ui/button';
import ProductDeleteDialog from '@/features/product/components/ProductDeleteDialog';
import ProductFormDialog from '@/features/product/components/ProductFormDialog';
import ProductPagination from '@/features/product/components/ProductPagination';
import ProductStatusFilter from '@/features/product/components/ProductStatusFilter';
import ProductTable from '@/features/product/components/ProductTable';
import { useCreateProduct } from '@/features/product/hooks/useCreateProduct';
import { useDeleteProduct } from '@/features/product/hooks/useDeleteProduct';
import { useProducts } from '@/features/product/hooks/useProducts';
import { useUpdateProduct } from '@/features/product/hooks/useUpdateProduct';
import type { ProductResponse, ProductStatus } from '@/features/product/types';

export default function ProductPage() {
  const [page, setPage] = useState(0);
  const [statusFilter, setStatusFilter] = useState<ProductStatus | 'ALL'>('ALL');
  const [formOpen, setFormOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState<ProductResponse | null>(null);

  const { data, isLoading } = useProducts({
    page,
    size: 10,
    status: statusFilter === 'ALL' ? undefined : statusFilter,
  });

  const createMutation = useCreateProduct();
  const updateMutation = useUpdateProduct();
  const deleteMutation = useDeleteProduct();

  function handleStatusFilterChange(value: ProductStatus | 'ALL') {
    setStatusFilter(value);
    setPage(0);
  }

  function handleOpenCreate() {
    setSelectedProduct(null);
    setFormOpen(true);
  }

  function handleOpenEdit(product: ProductResponse) {
    setSelectedProduct(product);
    setFormOpen(true);
  }

  function handleOpenDelete(product: ProductResponse) {
    setSelectedProduct(product);
    setDeleteOpen(true);
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">상품 관리</h1>
        <Button onClick={handleOpenCreate}>
          <Plus />
          상품 등록
        </Button>
      </div>

      <div className="flex items-center gap-2">
        <ProductStatusFilter
          value={statusFilter}
          onValueChange={handleStatusFilterChange}
        />
      </div>

      <ProductTable
        products={data?.content ?? []}
        isLoading={isLoading}
        onEdit={handleOpenEdit}
        onDelete={handleOpenDelete}
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

      <ProductFormDialog
        open={formOpen}
        onOpenChange={setFormOpen}
        product={selectedProduct}
        onSubmitCreate={(reqData) => {
          createMutation.mutate(reqData, { onSuccess: () => setFormOpen(false) });
        }}
        onSubmitUpdate={(id, reqData) => {
          updateMutation.mutate(
            { id, data: reqData },
            { onSuccess: () => setFormOpen(false) },
          );
        }}
        isPending={createMutation.isPending || updateMutation.isPending}
      />

      <ProductDeleteDialog
        open={deleteOpen}
        onOpenChange={setDeleteOpen}
        product={selectedProduct}
        onConfirm={(id) => {
          deleteMutation.mutate(id, { onSuccess: () => setDeleteOpen(false) });
        }}
        isPending={deleteMutation.isPending}
      />
    </div>
  );
}
