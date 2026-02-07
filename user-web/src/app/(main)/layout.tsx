'use client';

import { Navigation } from '@/shared/components/Navigation';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { LoadingOverlay } from '@/shared/components/LoadingOverlay';
import { LogOut } from 'lucide-react';

export default function MainLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const { user, isLoading, logout } = useAuth();

  if (isLoading) {
    return <LoadingOverlay message="사용자 정보를 불러오는 중..." />;
  }

  if (!user) {
    return null;
  }

  return (
    <div className="min-h-screen bg-gray-50 pb-16">
      <header className="bg-white border-b border-gray-200 shadow-sm sticky top-0 z-20 backdrop-blur-sm bg-white/95">
        <div className="max-w-screen-sm mx-auto px-5 py-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 bg-indigo-600 rounded-lg flex items-center justify-center">
              <span className="text-white text-lg font-bold">P</span>
            </div>
            <h1 className="text-lg font-bold text-gray-900">
              Point Roulette
            </h1>
          </div>
          <div className="flex items-center gap-3">
            <div className="text-right">
              <span className="text-xs text-gray-500 block">안녕하세요</span>
              <span className="text-sm font-semibold text-gray-900">
                {user.nickname}
              </span>
            </div>
            <button
              onClick={logout}
              className="p-2 text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-lg transition-all"
              aria-label="로그아웃"
            >
              <LogOut className="w-5 h-5" />
            </button>
          </div>
        </div>
      </header>

      <main className="max-w-screen-sm mx-auto">{children}</main>

      <Navigation />
    </div>
  );
}
