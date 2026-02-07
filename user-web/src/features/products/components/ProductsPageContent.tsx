/**
 * 상품 페이지 클라이언트 컴포넌트
 */

'use client';

import { useState } from 'react';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { useProducts } from '../hooks/useProducts';
import { usePurchaseProduct } from '../hooks/usePurchaseProduct';
import { usePoints } from '@/features/points/hooks/usePoints';
import { ProductCard } from './ProductCard';
import { PurchaseModal } from './PurchaseModal';
import type { ProductResponse, OrderCreateRequest } from '@/shared/types/models';
import { ApiError } from '@/shared/lib/apiClient';

export function ProductsPageContent() {
  const { user } = useAuth();
  const userId = user?.id || 0;

  const { data: products, isLoading, error } = useProducts({ status: 'ACTIVE' });
  const { data: pointData } = usePoints(userId);
  const purchaseMutation = usePurchaseProduct(userId);

  const [selectedProduct, setSelectedProduct] = useState<ProductResponse | null>(null);
  const [purchaseError, setPurchaseError] = useState<string | null>(null);
  const [purchaseSuccess, setPurchaseSuccess] = useState(false);

  const handlePurchaseClick = (product: ProductResponse) => {
    setSelectedProduct(product);
    setPurchaseError(null);
    setPurchaseSuccess(false);
  };

  const handlePurchaseConfirm = async (quantity: number) => {
    if (!selectedProduct) return;

    const request: OrderCreateRequest = {
      productId: selectedProduct.id,
      quantity,
    };

    try {
      await purchaseMutation.mutateAsync(request);
      setPurchaseSuccess(true);
      setTimeout(() => {
        setSelectedProduct(null);
        setPurchaseSuccess(false);
      }, 2000);
    } catch (error) {
      if (error instanceof ApiError) {
        setPurchaseError(error.message);
      } else {
        setPurchaseError('구매 중 오류가 발생했습니다.');
      }
    }
  };

  const handlePurchaseCancel = () => {
    setSelectedProduct(null);
    setPurchaseError(null);
    setPurchaseSuccess(false);
  };

  if (isLoading) {
    return (
      <div className="p-5 space-y-5">
        <div className="bg-indigo-600 rounded-2xl p-6 shadow-lg animate-pulse h-24" />
        <div className="grid grid-cols-2 gap-4">
          {[1, 2, 3, 4].map((i) => (
            <div key={i} className="bg-white rounded-xl shadow-md border border-gray-100 p-4 animate-pulse h-32" />
          ))}
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-5">
        <div className="bg-red-50 border border-red-200 rounded-xl p-4">
          <p className="text-red-800 font-semibold">상품 조회 실패</p>
          <p className="text-sm text-red-600 mt-1">잠시 후 다시 시도해주세요.</p>
        </div>
      </div>
    );
  }

  const userPoint = pointData?.currentPoint || 0;
  const productList = products?.content || [];

  return (
    <div className="p-5 space-y-5">
      {/* 헤더 */}
      <div className="bg-indigo-600 rounded-2xl p-6 text-white shadow-lg">
        <h2 className="text-2xl font-bold mb-2">상품 목록</h2>
        <p className="text-indigo-100 text-sm">포인트로 다양한 상품을 구매하세요</p>
      </div>

      {/* 상품 목록 */}
      {productList.length > 0 ? (
        <div className="grid grid-cols-2 gap-4">
          {productList.map((product) => (
            <ProductCard
              key={product.id}
              product={product}
              userPoint={userPoint}
              onPurchase={handlePurchaseClick}
            />
          ))}
        </div>
      ) : (
        <div className="bg-white rounded-xl shadow-md border border-gray-100 p-8 text-center">
          <div className="text-5xl mb-3">🏪</div>
          <p className="text-gray-500 font-medium mb-1">상품 준비 중입니다</p>
          <p className="text-sm text-gray-400">곧 다양한 상품을 만나보실 수 있습니다</p>
        </div>
      )}

      {/* 구매 확인 모달 */}
      {selectedProduct && (
        <PurchaseModal
          product={selectedProduct}
          userPoint={userPoint}
          onConfirm={handlePurchaseConfirm}
          onCancel={handlePurchaseCancel}
          isLoading={purchaseMutation.isPending}
        />
      )}

      {/* 구매 성공 알림 */}
      {purchaseSuccess && (
        <div className="fixed top-20 left-1/2 transform -translate-x-1/2 z-50 bg-green-600 text-white px-6 py-3 rounded-xl shadow-lg">
          <p className="font-semibold">✅ 구매가 완료되었습니다!</p>
        </div>
      )}

      {/* 구매 실패 알림 */}
      {purchaseError && (
        <div className="fixed top-20 left-1/2 transform -translate-x-1/2 z-50 bg-red-600 text-white px-6 py-3 rounded-xl shadow-lg">
          <p className="font-semibold">❌ {purchaseError}</p>
        </div>
      )}
    </div>
  );
}
