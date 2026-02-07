/**
 * 포인트 페이지 클라이언트 컴포넌트
 */

'use client';

import { useAuth } from '@/features/auth/hooks/useAuth';
import { usePoints } from '../hooks/usePoints';
import { PointBalance } from './PointBalance';
import { PointList } from './PointList';
import { sortPointsByExpiry } from '../utils/pointStatus';

export function PointsPageContent() {
  const { user } = useAuth();
  const { data, isLoading, error } = usePoints(user?.id || 0);

  if (isLoading) {
    return (
      <div className="p-5 space-y-5">
        <div className="bg-indigo-600 rounded-2xl p-6 shadow-lg animate-pulse h-32" />
        <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-5 animate-pulse h-64" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-5">
        <div className="bg-red-50 border border-red-200 rounded-xl p-4">
          <p className="text-red-800 font-semibold">포인트 조회 실패</p>
          <p className="text-sm text-red-600 mt-1">잠시 후 다시 시도해주세요.</p>
        </div>
      </div>
    );
  }

  if (!data) {
    return null;
  }

  // 포인트를 만료일 기준으로 정렬
  const sortedPoints = sortPointsByExpiry(data.points);

  return (
    <div className="p-5 space-y-5">
      {/* 포인트 잔액 */}
      <PointBalance
        currentPoint={data.currentPoint}
        expiringPointIn7Days={data.expiringPointIn7Days}
      />

      {/* 포인트 내역 */}
      <PointList points={sortedPoints} />

      {/* 포인트 유효기간 안내 */}
      <div className="bg-amber-50 border border-amber-200 rounded-xl p-4">
        <div className="flex items-start gap-3">
          <span className="text-xl">⏰</span>
          <div className="flex-1">
            <h3 className="font-semibold text-amber-900 mb-1">포인트 유효기간</h3>
            <p className="text-sm text-amber-800">획득일로부터 30일간 사용 가능합니다</p>
            <p className="text-xs text-amber-700 mt-1">만료 7일 전부터 알림이 표시됩니다</p>
          </div>
        </div>
      </div>
    </div>
  );
}
