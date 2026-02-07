'use client';

import { RouletteWheel } from '@/features/roulette/components/RouletteWheel';

export default function RoulettePage() {
  return (
    <div className="p-5 space-y-5">
      <div className="bg-indigo-600 rounded-2xl p-6 text-white shadow-lg">
        <h2 className="text-2xl font-bold mb-2">오늘의 룰렛</h2>
        <p className="text-indigo-100 text-sm">매일 한 번 무료로 도전하세요!</p>
      </div>

      <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-8">
        <RouletteWheel />
      </div>

      <div className="bg-amber-50 border border-amber-200 rounded-xl p-4">
        <div className="flex items-start gap-3">
          <span className="text-2xl">💡</span>
          <div className="flex-1">
            <h3 className="font-semibold text-amber-900 mb-1">참여 안내</h3>
            <ul className="text-sm text-amber-800 space-y-1">
              <li>• 하루에 한 번만 참여 가능합니다</li>
              <li>• 100p ~ 1,000p 랜덤 지급</li>
              <li>• 일일 예산 소진 시 참여 불가</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
