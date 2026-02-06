import axios from 'axios';
import type { ApiErrorResponse, ApiResponse } from '@/shared/types/api';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

/** 응답 인터셉터: response.data 자동 추출 */
apiClient.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (axios.isAxiosError(error) && error.response) {
      const apiError = error.response.data as ApiErrorResponse;
      return Promise.reject(apiError);
    }
    return Promise.reject(error);
  },
);

export default apiClient;

/** 타입 안전한 GET 요청 */
export async function apiGet<T>(url: string, params?: Record<string, unknown>): Promise<T> {
  const response = await apiClient.get<unknown, ApiResponse<T>>(url, { params });
  return response.data;
}

/** 타입 안전한 POST 요청 */
export async function apiPost<T>(url: string, data?: unknown): Promise<T> {
  const response = await apiClient.post<unknown, ApiResponse<T>>(url, data);
  return response.data;
}

/** 타입 안전한 PUT 요청 */
export async function apiPut<T>(url: string, data?: unknown): Promise<T> {
  const response = await apiClient.put<unknown, ApiResponse<T>>(url, data);
  return response.data;
}

/** 타입 안전한 DELETE 요청 */
export async function apiDelete<T>(url: string): Promise<T> {
  const response = await apiClient.delete<unknown, ApiResponse<T>>(url);
  return response.data;
}
