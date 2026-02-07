/**
 * 포인트 목록/타임라인 컴포넌트
 */

'use client';

import type { PointResponse } from '@/shared/types/models';
import { formatPointWithUnit, formatDateTime } from '../utils/formatPoint';
import {
  getPointStatusStyle,
  getPointSourceLabel,
  getDaysUntilExpiry,
  isExpired,
} from '../utils/pointStatus';

interface PointListProps {
  points: PointResponse[];
}

export function PointList({ points }: PointListProps) {
  if (points.length === 0) {
    return (
      <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-8 text-center">
        <div className="text-5xl mb-3">💎</div>
        <p className="text-gray-500 font-medium mb-1">포인트 내역이 없습니다</p>
        <p className="text-sm text-gray-400">룰렛을 돌려 포인트를 획득해보세요</p>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-5">
      <div className="flex items-center justify-between mb-4">
        <h3 className="font-bold text-gray-900">포인트 내역</h3>
        <span className="text-sm text-gray-500">{points.length}건</span>
      </div>

      <div className="space-y-3">
        {points.map((point) => (
          <PointItem key={point.id} point={point} />
        ))}
      </div>
    </div>
  );
}

function PointItem({ point }: { point: PointResponse }) {
  const style = getPointStatusStyle(point);
  const sourceLabel = getPointSourceLabel(point.sourceType);
  const daysRemaining = getDaysUntilExpiry(point.expiresAt);
  const expired = isExpired(point.expiresAt);

  const isPositive = point.sourceType === 'ROULETTE' || point.sourceType === 'REFUND';
  const iconBgColor = isPositive ? 'bg-green-100' : 'bg-red-100';
  const iconColor = isPositive ? 'text-green-600' : 'text-red-600';
  const amountColor = isPositive ? 'text-green-600' : 'text-red-600';
  const sign = isPositive ? '+' : '-';

  return (
    <div
      className={`flex items-center gap-3 p-4 rounded-xl border ${style.bgColor} ${
        point.status !== 'ACTIVE' ? 'opacity-60' : ''
      }`}
    >
      <div className={`w-10 h-10 ${iconBgColor} rounded-full flex items-center justify-center flex-shrink-0`}>
        <span className={`${iconColor} font-bold`}>{sign}</span>
      </div>

      <div className="flex-1 min-w-0">
        <p className={`font-semibold ${style.textColor}`}>{sourceLabel}</p>
        <p className="text-xs text-gray-500">{formatDateTime(point.createdAt)}</p>
        {point.remainingAmount < point.initialAmount && point.status === 'ACTIVE' && (
          <p className="text-xs text-amber-600 mt-1">
            일부 사용됨 ({formatPointWithUnit(point.remainingAmount)} 남음)
          </p>
        )}
      </div>

      <div className="text-right flex-shrink-0">
        <p className={`font-bold ${style.textColor} ${!isPositive && amountColor}`}>
          {sign}
          {formatPointWithUnit(point.initialAmount)}
        </p>
        {point.status === 'ACTIVE' && !expired && (
          <p className={`text-xs ${daysRemaining <= 7 ? 'text-amber-600 font-semibold' : 'text-gray-500'}`}>
            D-{daysRemaining}
          </p>
        )}
        {expired && <p className="text-xs text-gray-400">만료됨</p>}
        {point.status === 'USED' && point.remainingAmount === 0 && (
          <p className="text-xs text-gray-400">사용 완료</p>
        )}
        {point.status === 'CANCELLED' && <p className="text-xs text-gray-400">취소됨</p>}
      </div>
    </div>
  );
}
