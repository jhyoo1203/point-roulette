/** 대시보드 예산 현황 */
export interface DashboardBudgetSummary {
  totalAmount: number;
  remainingAmount: number;
  spentAmount: number;
  usagePercentage: number;
  isLowBudget: boolean; // 잔여 1,000p 미만 여부
}

/** 대시보드 룰렛 통계 */
export interface DashboardRouletteStats {
  participantCount: number; // 오늘 참여 인원수
  totalPointsGiven: number; // 오늘 총 지급 포인트
  successCount: number; // 성공 건수
  cancelledCount: number; // 취소 건수
}

/** 오늘 날짜 기준 파라미터 */
export interface TodayParams {
  today: string; // ISO 형식 날짜 (YYYY-MM-DD)
}
