/**
 * 한국 시간대(Asia/Seoul, UTC+9) 기준 날짜 유틸리티
 *
 * 백엔드가 Asia/Seoul 시간대를 사용하므로, 프론트엔드도 동일한 시간대로 날짜를 계산합니다.
 */

/**
 * 한국 시간대 기준 오늘 날짜를 YYYY-MM-DD 형식으로 반환
 *
 * @returns ISO 형식 날짜 문자열 (YYYY-MM-DD)
 *
 * @example
 * getTodayInKST() // "2026-02-07"
 */
export function getTodayInKST(): string {
  // 한국 시간대로 날짜 포맷팅
  const kstDate = new Date().toLocaleString('en-CA', {
    timeZone: 'Asia/Seoul',
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  });

  // toLocaleString('en-CA')는 YYYY-MM-DD 형식으로 반환
  return kstDate.split(',')[0];
}

/**
 * 특정 날짜를 한국 시간대 기준으로 YYYY-MM-DD 형식으로 변환
 *
 * @param date - Date 객체
 * @returns ISO 형식 날짜 문자열 (YYYY-MM-DD)
 */
export function formatDateInKST(date: Date): string {
  const kstDate = date.toLocaleString('en-CA', {
    timeZone: 'Asia/Seoul',
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  });

  return kstDate.split(',')[0];
}

/**
 * 한국 시간대 기준 현재 시각을 반환
 *
 * @returns Date 객체
 */
export function getNowInKST(): Date {
  // 한국 시간대의 현재 시각을 ISO 문자열로 변환 후 Date 객체 생성
  const kstString = new Date().toLocaleString('en-US', {
    timeZone: 'Asia/Seoul',
  });

  return new Date(kstString);
}
