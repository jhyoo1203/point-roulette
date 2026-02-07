/**
 * 도메인 모델 타입 정의
 */

// ===== User =====
export interface LoginRequest {
  nickname: string;
}

export interface LoginResponse {
  id: number;
  nickname: string;
  isNewUser: boolean;
}

// ===== Roulette =====
export interface RouletteParticipateResponse {
  success: boolean;
  wonAmount?: number;
  remainingBudget: number;
}

export interface RouletteStatusResponse {
  hasParticipatedToday: boolean;
  todayRemainingBudget: number;
  lastParticipation?: RouletteHistoryResponse;
}

export interface RouletteHistoryResponse {
  id: number;
  userId: number;
  wonAmount: number;
  participatedAt: string;
}

// ===== Point =====
export type PointSourceType = 'ROULETTE' | 'REFUND';
export type PointStatus = 'ACTIVE' | 'USED' | 'EXPIRED' | 'CANCELLED';

export interface PointResponse {
  id: number;
  initialAmount: number;
  remainingAmount: number;
  expiresAt: string;
  sourceType: PointSourceType;
  sourceId: number;
  status: PointStatus;
  createdAt: string;
}

export interface PointBalanceResponse {
  userId: number;
  currentPoint: number;
  expiringPointIn7Days: number;
  points: PointResponse[];
}

// ===== Product =====
export type ProductStatus = 'ACTIVE' | 'INACTIVE';

export interface ProductResponse {
  id: number;
  name: string;
  price: number;
  stock: number;
  description?: string;
  status: ProductStatus;
  createdAt: string;
  updatedAt: string;
}

// ===== Order =====
export type OrderStatus = 'COMPLETED' | 'CANCELLED';

export interface OrderCreateRequest {
  productId: number;
  quantity: number;
}

export interface OrderResponse {
  id: number;
  userId: number;
  productId: number;
  productName: string;
  quantity: number;
  totalPrice: number;
  status: OrderStatus;
  createdAt: string;
}
