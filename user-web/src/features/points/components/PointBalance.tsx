/**
 * 포인트 잔액 카드 컴포넌트
 */

'use client';

import { formatPointWithUnit } from '../utils/formatPoint';

interface PointBalanceProps {
  currentPoint: number;
  expiringPointIn7Days: number;
}

export function PointBalance({ currentPoint, expiringPointIn7Days }: PointBalanceProps) {
  return (
    <div className="bg-indigo-600 rounded-2xl p-6 shadow-lg">
      <div className="text-white">
        <p className="text-indigo-100 text-sm mb-1">보유 포인트</p>
        <p className="text-4xl font-bold mb-1">{formatPointWithUnit(currentPoint)}</p>
        {expiringPointIn7Days > 0 && (
          <div className="mt-3 bg-amber-500/20 border border-amber-400/30 rounded-lg p-2">
            <p className="text-amber-100 text-xs">
              ⏰ 7일 이내 만료 예정: <span className="font-semibold">{formatPointWithUnit(expiringPointIn7Days)}</span>
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
