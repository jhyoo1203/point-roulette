/**
 * Auth 관련 타입 정의
 */

export interface User {
  id: number;
  nickname: string;
}

export interface AuthContextType {
  user: User | null;
  isLoading: boolean;
  login: (nickname: string) => Promise<void>;
  logout: () => void;
}
