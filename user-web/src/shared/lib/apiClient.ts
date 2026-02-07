/**
 * API 클라이언트 설정
 */

import type { ApiResponse } from '@/shared/types/api';

// API 에러 클래스
export class ApiError extends Error {
  constructor(
    public statusCode: number,
    public errorCode?: string,
    message?: string
  ) {
    super(message || 'API 요청 중 오류가 발생했습니다.');
    this.name = 'ApiError';
  }
}

// Fetch wrapper
export async function apiFetch<T>(
  url: string,
  options?: RequestInit
): Promise<T> {
  try {
    const response = await fetch(url, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...options?.headers,
      },
      credentials: 'include', // Cookie 포함
    });

    // JSON 파싱
    const data: ApiResponse<T> = await response.json();

    // 에러 처리
    if (!response.ok || data.errorCode) {
      throw new ApiError(
        response.status,
        data.errorCode,
        getErrorMessage(data.errorCode)
      );
    }

    return data.data as T;
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }

    // 네트워크 에러 등
    throw new ApiError(0, 'NETWORK_ERROR', '네트워크 오류가 발생했습니다.');
  }
}

// GET 요청
export async function get<T>(url: string, options?: RequestInit): Promise<T> {
  return apiFetch<T>(url, { ...options, method: 'GET' });
}

// POST 요청
export async function post<T>(
  url: string,
  body?: unknown,
  options?: RequestInit
): Promise<T> {
  return apiFetch<T>(url, {
    ...options,
    method: 'POST',
    body: JSON.stringify(body),
  });
}

// PUT 요청
export async function put<T>(
  url: string,
  body?: unknown,
  options?: RequestInit
): Promise<T> {
  return apiFetch<T>(url, {
    ...options,
    method: 'PUT',
    body: JSON.stringify(body),
  });
}

// DELETE 요청
export async function del<T>(url: string, options?: RequestInit): Promise<T> {
  return apiFetch<T>(url, { ...options, method: 'DELETE' });
}

// 에러 메시지 매핑
function getErrorMessage(errorCode?: string): string {
  const errorMessages: Record<string, string> = {
    INTERNAL_SERVER_ERROR: '서버 오류가 발생했습니다.',
    INVALID_PARAMETER: '잘못된 요청입니다.',
    BUSINESS_ERROR: '비즈니스 로직 오류가 발생했습니다.',
    RESOURCE_NOT_FOUND: '요청한 리소스를 찾을 수 없습니다.',
    ALREADY_ROULETTE_PARTICIPATED: '오늘 이미 룰렛에 참여했습니다.',
    ROULETTE_PARTICIPATION_FAILED: '룰렛 참여에 실패했습니다. 다시 시도해주세요.',
    INSUFFICIENT_POINT: '포인트가 부족합니다.',
  };

  return errorMessages[errorCode || ''] || '알 수 없는 오류가 발생했습니다.';
}
