import { apiGet } from '@/shared/lib/apiClient';
import type { PaginatedData } from '@/shared/types/api';
import type { BudgetResponse } from '@/features/budget/types';
import type { RouletteParticipationResponse } from '@/features/roulette/types';

/**
 * 오늘 예산 조회
 * GET /api/v1/admin/budgets?startDate={today}&endDate={today}&page=0&size=1
 */
export async function fetchTodayBudget(today: string): Promise<BudgetResponse | null> {
  const result = await apiGet<PaginatedData<BudgetResponse>>('/api/v1/admin/budgets', {
    startDate: today,
    endDate: today,
    page: 0,
    size: 1,
  });

  return result.content[0] || null;
}

/**
 * 특정 페이지의 룰렛 참여 이력 조회
 */
async function fetchRouletteParticipationsPage(
  today: string,
  page: number,
  size: number = 100,
): Promise<PaginatedData<RouletteParticipationResponse>> {
  return apiGet<PaginatedData<RouletteParticipationResponse>>(
    '/api/v1/admin/roulette/participations',
    {
      startDate: today,
      endDate: today,
      page,
      size,
    },
  );
}

/**
 * 오늘 룰렛 참여 이력 전체 조회 (모든 페이지 - 통계 계산용)
 *
 * React Best Practices:
 * - async-parallel: Promise.all()로 여러 페이지 병렬 요청
 * - 첫 페이지 조회 후 totalPages 확인
 * - 2페이지 이상이면 나머지 페이지들을 병렬로 요청
 * - 모든 페이지의 content를 합쳐서 반환
 *
 * @param today - ISO 형식 날짜 (YYYY-MM-DD)
 * @returns 모든 참여 이력을 포함한 PaginatedData
 */
export async function fetchTodayRouletteParticipations(
  today: string,
): Promise<PaginatedData<RouletteParticipationResponse>> {
  // 1. 첫 페이지 조회
  const firstPage = await fetchRouletteParticipationsPage(today, 0);

  // 2. 페이지가 1개뿐이면 바로 반환
  if (firstPage.totalPages <= 1) {
    return firstPage;
  }

  // 3. 나머지 페이지들을 병렬로 요청 (async-parallel 패턴)
  const remainingPagePromises: Promise<PaginatedData<RouletteParticipationResponse>>[] = [];
  for (let page = 1; page < firstPage.totalPages; page++) {
    remainingPagePromises.push(fetchRouletteParticipationsPage(today, page));
  }

  const remainingPages = await Promise.all(remainingPagePromises);

  // 4. 모든 페이지의 content를 합침
  const allContent = [firstPage, ...remainingPages].flatMap((page) => page.content);

  // 5. 첫 페이지 메타데이터를 유지하되, content만 전체 데이터로 교체
  return {
    ...firstPage,
    content: allContent,
    pageSize: allContent.length,
    currentPage: 0,
  };
}
