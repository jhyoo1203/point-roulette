/**
 * 상품 구매 확인 모달
 */

'use client';

import { useState } from 'react';
import type { ProductResponse } from '@/shared/types/models';
import { formatPointWithUnit } from '@/features/points/utils/formatPoint';

interface PurchaseModalProps {
  product: ProductResponse;
  userPoint: number;
  onConfirm: (quantity: number) => void;
  onCancel: () => void;
  isLoading?: boolean;
}

export function PurchaseModal({
  product,
  userPoint,
  onConfirm,
  onCancel,
  isLoading = false,
}: PurchaseModalProps) {
  const [quantity, setQuantity] = useState(1);
  const totalPrice = product.price * quantity;
  const remainingPoint = userPoint - totalPrice;

  const maxQuantity = Math.min(
    product.stock,
    Math.floor(userPoint / product.price)
  );

  const handleConfirm = () => {
    if (quantity > 0 && quantity <= maxQuantity) {
      onConfirm(quantity);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl shadow-xl max-w-md w-full p-6">
        <h2 className="text-xl font-bold text-gray-900 mb-4">상품 구매</h2>

        {/* 상품 정보 */}
        <div className="bg-gray-50 rounded-xl p-4 mb-4">
          <h3 className="font-semibold text-gray-900 mb-1">{product.name}</h3>
          {product.description && (
            <p className="text-sm text-gray-600 mb-2">{product.description}</p>
          )}
          <p className="text-lg font-bold text-indigo-600">{formatPointWithUnit(product.price)}</p>
        </div>

        {/* 수량 선택 */}
        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-700 mb-2">
            수량
          </label>
          <div className="flex items-center gap-3">
            <button
              onClick={() => setQuantity(Math.max(1, quantity - 1))}
              disabled={quantity <= 1}
              className="w-10 h-10 rounded-lg border border-gray-300 flex items-center justify-center font-bold text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              -
            </button>
            <input
              type="number"
              value={quantity}
              onChange={(e) => {
                const value = parseInt(e.target.value) || 1;
                setQuantity(Math.min(maxQuantity, Math.max(1, value)));
              }}
              className="flex-1 text-center border border-gray-300 rounded-lg py-2 px-3 font-semibold"
              min={1}
              max={maxQuantity}
            />
            <button
              onClick={() => setQuantity(Math.min(maxQuantity, quantity + 1))}
              disabled={quantity >= maxQuantity}
              className="w-10 h-10 rounded-lg border border-gray-300 flex items-center justify-center font-bold text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              +
            </button>
          </div>
          <p className="text-xs text-gray-500 mt-1">최대 {maxQuantity}개 구매 가능</p>
        </div>

        {/* 결제 정보 */}
        <div className="bg-indigo-50 rounded-xl p-4 mb-4 space-y-2">
          <div className="flex items-center justify-between text-sm">
            <span className="text-gray-600">총 금액</span>
            <span className="font-semibold text-gray-900">{formatPointWithUnit(totalPrice)}</span>
          </div>
          <div className="flex items-center justify-between text-sm">
            <span className="text-gray-600">보유 포인트</span>
            <span className="font-semibold text-gray-900">{formatPointWithUnit(userPoint)}</span>
          </div>
          <div className="border-t border-indigo-200 pt-2">
            <div className="flex items-center justify-between">
              <span className="font-semibold text-gray-900">잔여 포인트</span>
              <span className={`font-bold ${remainingPoint >= 0 ? 'text-indigo-600' : 'text-red-600'}`}>
                {formatPointWithUnit(remainingPoint)}
              </span>
            </div>
          </div>
        </div>

        {/* 버튼 */}
        <div className="flex gap-3">
          <button
            onClick={onCancel}
            disabled={isLoading}
            className="flex-1 px-4 py-3 border border-gray-300 text-gray-700 font-semibold rounded-xl hover:bg-gray-50 transition disabled:opacity-50"
          >
            취소
          </button>
          <button
            onClick={handleConfirm}
            disabled={isLoading || remainingPoint < 0}
            className="flex-1 px-4 py-3 bg-indigo-600 text-white font-semibold rounded-xl hover:bg-indigo-700 transition disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {isLoading ? '구매 중...' : '구매하기'}
          </button>
        </div>
      </div>
    </div>
  );
}
