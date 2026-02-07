'use client';

/**
 * 룰렛 휠 컴포넌트
 */

import { useState } from 'react';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { useRouletteStatus } from '../hooks/useRouletteStatus';
import { useRouletteSpin } from '../hooks/useRouletteSpin';
import type { RouletteParticipateResponse } from '@/shared/types/models';
import { ApiError } from '@/shared/lib/apiClient';

export function RouletteWheel() {
  const { user } = useAuth();
  const { data: status, isLoading: isLoadingStatus } = useRouletteStatus(user!.id);
  const { mutate: spin, isPending } = useRouletteSpin(user!.id);

  const [isSpinning, setIsSpinning] = useState(false);
  const [rotation, setRotation] = useState(0);
  const [showResult, setShowResult] = useState(false);
  const [spinResult, setSpinResult] = useState<RouletteParticipateResponse | null>(null);

  // 스핀 핸들러
  const handleSpin = () => {
    if (isSpinning || isPending || status?.hasParticipatedToday) return;

    setIsSpinning(true);
    setShowResult(false);

    // 백엔드에 참여 요청
    spin(undefined, {
      onSuccess: (data) => {
        setSpinResult(data);
        animateSpin();
      },
      onError: (error: Error | ApiError) => {
        setIsSpinning(false);
        alert(error.message || '룰렛 참여에 실패했습니다.');
      },
    });
  };

  // 애니메이션 처리
  const animateSpin = () => {
    // 3바퀴 + 랜덤 각도 회전
    const baseRotation = 360 * 3;
    const randomAngle = Math.random() * 360;
    const totalRotation = rotation + baseRotation + randomAngle;

    setRotation(totalRotation);

    // 애니메이션 완료 후 결과 표시
    setTimeout(() => {
      setIsSpinning(false);
      setShowResult(true);
    }, 3000); // 3초 애니메이션
  };

  // 결과 모달 닫기
  const closeResultModal = () => {
    setShowResult(false);
    setSpinResult(null);
  };

  if (isLoadingStatus) {
    return (
      <div className="flex flex-col items-center justify-center p-8">
        <div className="w-48 h-48 bg-gray-100 rounded-full flex items-center justify-center animate-pulse">
          <div className="text-center">
            <div className="text-6xl mb-2">🎰</div>
            <p className="text-sm text-gray-500 font-medium">로딩 중...</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="flex flex-col items-center justify-center space-y-6">
      {/* 룰렛 휠 */}
      <div className="relative">
        <div
          className="w-48 h-48 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-full flex items-center justify-center shadow-2xl border-8 border-white transition-transform duration-[3000ms] ease-out"
          style={{
            transform: `rotate(${rotation}deg)`,
          }}
        >
          <div className="text-center">
            <div className="text-6xl mb-2">🎰</div>
            <p className="text-xs text-white font-bold">SPIN</p>
          </div>
        </div>
        {/* 포인터 */}
        <div className="absolute -top-4 left-1/2 -translate-x-1/2">
          <div className="w-0 h-0 border-l-[12px] border-l-transparent border-r-[12px] border-r-transparent border-t-[20px] border-t-red-500"></div>
        </div>
      </div>

      {/* 예산 정보 */}
      <div className="w-full bg-gray-50 rounded-xl p-4 border border-gray-200">
        <div className="flex justify-between items-center">
          <span className="text-sm text-gray-600">오늘 잔여 예산</span>
          <span className="text-lg font-bold text-indigo-600">
            {status?.todayRemainingBudget.toLocaleString()}p
          </span>
        </div>
      </div>

      {/* 스핀 버튼 */}
      <button
        onClick={handleSpin}
        disabled={isSpinning || isPending || status?.hasParticipatedToday || (status?.todayRemainingBudget ?? 0) <= 0}
        className="w-full bg-indigo-600 text-white py-4 rounded-xl font-bold text-lg shadow-lg hover:bg-indigo-700 hover:shadow-xl transform hover:scale-[1.02] active:scale-[0.98] transition-all disabled:bg-gray-400 disabled:cursor-not-allowed disabled:transform-none"
      >
        {isSpinning || isPending
          ? '돌리는 중...'
          : status?.hasParticipatedToday
            ? '오늘 이미 참여했습니다'
            : (status?.todayRemainingBudget ?? 0) <= 0
              ? '오늘 예산이 소진되었습니다'
              : '룰렛 돌리기'}
      </button>

      {/* 마지막 참여 이력 */}
      {status?.lastParticipation && (
        <div className="w-full bg-green-50 border border-green-200 rounded-xl p-4">
          <div className="flex items-center gap-2 mb-2">
            <span className="text-xl">✨</span>
            <h3 className="font-semibold text-green-900">최근 당첨 내역</h3>
          </div>
          <div className="text-sm text-green-800 space-y-1">
            <p>• 날짜: {status.lastParticipation.participatedDate}</p>
            <p>• 당첨 포인트: {status.lastParticipation.wonAmount.toLocaleString()}p</p>
          </div>
        </div>
      )}

      {/* 결과 모달 */}
      {showResult && spinResult && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl p-8 max-w-sm w-full shadow-2xl">
            {spinResult.success ? (
              <>
                <div className="text-center mb-6">
                  <div className="text-7xl mb-4">🎉</div>
                  <h2 className="text-2xl font-bold text-gray-900 mb-2">축하합니다!</h2>
                  <p className="text-5xl font-bold text-indigo-600 mb-2">
                    {spinResult.wonAmount?.toLocaleString()}p
                  </p>
                  <p className="text-sm text-gray-600">포인트를 획득했습니다</p>
                </div>
                <div className="bg-gray-50 rounded-lg p-4 mb-6">
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-600">남은 예산</span>
                    <span className="font-semibold text-gray-900">
                      {spinResult.remainingBudget.toLocaleString()}p
                    </span>
                  </div>
                </div>
              </>
            ) : (
              <>
                <div className="text-center mb-6">
                  <div className="text-7xl mb-4">😢</div>
                  <h2 className="text-2xl font-bold text-gray-900 mb-2">아쉽네요...</h2>
                  <p className="text-gray-600">오늘 예산이 모두 소진되었습니다</p>
                  <p className="text-sm text-gray-500 mt-2">내일 다시 도전해주세요!</p>
                </div>
              </>
            )}
            <button
              onClick={closeResultModal}
              className="w-full bg-indigo-600 text-white py-3 rounded-xl font-semibold hover:bg-indigo-700 transition-colors"
            >
              확인
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
