/**
 * 포인트 포맷팅 유틸리티
 */

/**
 * 포인트를 천단위 콤마와 함께 표시
 */
export function formatPoint(point: number): string {
  return point.toLocaleString('ko-KR');
}

/**
 * 포인트를 단위와 함께 표시
 */
export function formatPointWithUnit(point: number): string {
  return `${formatPoint(point)}p`;
}

/**
 * 날짜를 YYYY.MM.DD 형식으로 표시
 */
export function formatDate(dateString: string): string {
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');

  return `${year}.${month}.${day}`;
}

/**
 * 날짜를 YYYY.MM.DD HH:mm 형식으로 표시
 */
export function formatDateTime(dateString: string): string {
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');

  return `${year}.${month}.${day} ${hours}:${minutes}`;
}

/**
 * 만료까지 남은 일수를 D-N 형식으로 표시
 */
export function formatDaysRemaining(days: number): string {
  if (days === 0) return '오늘 만료';
  if (days < 0) return '만료됨';
  return `D-${days}`;
}
