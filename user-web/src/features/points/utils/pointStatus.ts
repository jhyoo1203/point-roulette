/**
 * 포인트 상태 계산 유틸리티
 */

import type { PointResponse } from '@/shared/types/models';

/**
 * 포인트가 만료되었는지 확인
 */
export function isExpired(expiresAt: string): boolean {
  return new Date(expiresAt) < new Date();
}

/**
 * 포인트가 N일 이내에 만료되는지 확인
 */
export function isExpiringWithinDays(expiresAt: string, days: number): boolean {
  const expiryDate = new Date(expiresAt);
  const targetDate = new Date();
  targetDate.setDate(targetDate.getDate() + days);

  return expiryDate <= targetDate && !isExpired(expiresAt);
}

/**
 * 만료까지 남은 일수 계산
 */
export function getDaysUntilExpiry(expiresAt: string): number {
  const now = new Date();
  const expiry = new Date(expiresAt);
  const diffTime = expiry.getTime() - now.getTime();
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

  return Math.max(0, diffDays);
}

/**
 * 포인트 상태에 따른 UI 클래스 반환
 */
export function getPointStatusStyle(point: PointResponse): {
  textColor: string;
  bgColor: string;
  label: string;
} {
  // 만료된 포인트
  if (point.status === 'EXPIRED' || isExpired(point.expiresAt)) {
    return {
      textColor: 'text-gray-500',
      bgColor: 'bg-gray-50',
      label: '만료됨',
    };
  }

  // 사용된 포인트
  if (point.status === 'USED' || point.remainingAmount === 0) {
    return {
      textColor: 'text-gray-500',
      bgColor: 'bg-gray-50',
      label: '사용 완료',
    };
  }

  // 취소된 포인트
  if (point.status === 'CANCELLED') {
    return {
      textColor: 'text-gray-500',
      bgColor: 'bg-gray-50',
      label: '취소됨',
    };
  }

  // 7일 이내 만료 예정
  if (isExpiringWithinDays(point.expiresAt, 7)) {
    return {
      textColor: 'text-amber-700',
      bgColor: 'bg-amber-50 border-amber-200',
      label: '만료 임박',
    };
  }

  // 정상 활성 포인트
  return {
    textColor: 'text-gray-900',
    bgColor: 'bg-white',
    label: '사용 가능',
  };
}

/**
 * 포인트 소스 타입에 따른 라벨 반환
 */
export function getPointSourceLabel(sourceType: string): string {
  const labels: Record<string, string> = {
    ROULETTE: '룰렛 당첨',
    REFUND: '주문 취소 환불',
  };

  return labels[sourceType] || sourceType;
}

/**
 * 포인트 목록을 만료일 기준으로 정렬 (만료 임박 순)
 */
export function sortPointsByExpiry(points: PointResponse[]): PointResponse[] {
  return [...points].sort((a, b) => {
    // 활성 포인트만 정렬
    const aActive = a.status === 'ACTIVE' && a.remainingAmount > 0;
    const bActive = b.status === 'ACTIVE' && b.remainingAmount > 0;

    if (aActive && !bActive) return -1;
    if (!aActive && bActive) return 1;

    // 둘 다 활성이면 만료일 오름차순
    return new Date(a.expiresAt).getTime() - new Date(b.expiresAt).getTime();
  });
}
