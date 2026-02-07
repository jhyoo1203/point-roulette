/**
 * API 엔드포인트 상수
 */

const API_BASE = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export const ENDPOINTS = {
  // User
  LOGIN: `${API_BASE}/api/v1/users/login`,

  // Roulette
  ROULETTE_PARTICIPATE: (userId: number) => `${API_BASE}/api/v1/roulette/participate/${userId}`,
  ROULETTE_STATUS: (userId: number) => `${API_BASE}/api/v1/roulette/status/${userId}`,

  // Points
  POINTS: (userId: number) => `${API_BASE}/api/v1/points/${userId}`,

  // Products
  PRODUCTS: `${API_BASE}/api/v1/products`,
  PRODUCT_PURCHASE: (userId: number) => `${API_BASE}/api/v1/products/purchase/${userId}`,

  // Orders
  ORDERS: (userId: number) => `${API_BASE}/api/v1/products/orders/${userId}`,
} as const;
