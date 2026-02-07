/**
 * 상품 카드 컴포넌트
 */

'use client';

import type { ProductResponse } from '@/shared/types/models';
import { formatPointWithUnit } from '@/features/points/utils/formatPoint';

interface ProductCardProps {
  product: ProductResponse;
  userPoint: number;
  onPurchase: (product: ProductResponse) => void;
}

export function ProductCard({ product, userPoint, onPurchase }: ProductCardProps) {
  const isOutOfStock = product.stock === 0 || product.status === 'INACTIVE';
  const isInsufficientPoint = userPoint < product.price;
  const isDisabled = isOutOfStock || isInsufficientPoint;

  const handleClick = () => {
    if (!isDisabled) {
      onPurchase(product);
    }
  };

  return (
    <div className="bg-white rounded-xl shadow-md border border-gray-100 overflow-hidden">
      <div className="p-4">
        <h3 className="font-bold text-gray-900 mb-1">{product.name}</h3>
        {product.description && (
          <p className="text-xs text-gray-500 mb-2 line-clamp-2">{product.description}</p>
        )}

        {/* 재고 정보 */}
        {product.stock > 0 && product.stock <= 10 && (
          <p className="text-xs text-amber-600 mb-2">재고 {product.stock}개 남음</p>
        )}

        <div className="flex items-center justify-between mt-3">
          <span className="text-lg font-bold text-indigo-600">{formatPointWithUnit(product.price)}</span>

          {/* 구매 버튼 */}
          <div className="relative group">
            <button
              onClick={handleClick}
              disabled={isDisabled}
              className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition ${
                isDisabled
                  ? 'bg-gray-300 text-gray-500 cursor-not-allowed'
                  : 'bg-indigo-600 text-white hover:bg-indigo-700'
              }`}
            >
              {isOutOfStock ? '품절' : '구매'}
            </button>

            {/* 툴팁: 포인트 부족 시 */}
            {isInsufficientPoint && !isOutOfStock && (
              <div className="absolute bottom-full right-0 mb-2 hidden group-hover:block z-10">
                <div className="bg-gray-900 text-white text-xs rounded-lg py-2 px-3 whitespace-nowrap">
                  포인트가 부족합니다
                  <br />
                  필요: {formatPointWithUnit(product.price - userPoint)}
                  <div className="absolute top-full right-4 w-0 h-0 border-l-4 border-r-4 border-t-4 border-transparent border-t-gray-900" />
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
