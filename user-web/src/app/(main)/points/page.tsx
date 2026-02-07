export const metadata = {
  title: '내 포인트 - Point Roulette',
};

export default function PointsPage() {
  return (
    <div className="p-5 space-y-5">
      <div className="bg-indigo-600 rounded-2xl p-6 shadow-lg">
        <div className="text-white">
          <p className="text-indigo-100 text-sm mb-1">보유 포인트</p>
          <p className="text-4xl font-bold mb-1">0</p>
          <p className="text-indigo-100 text-xs">Point</p>
        </div>
      </div>

      <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-5">
        <div className="flex items-center justify-between mb-4">
          <h3 className="font-bold text-gray-900">포인트 내역</h3>
          <button className="text-sm text-indigo-600 font-medium">전체보기</button>
        </div>

        <div className="space-y-3">
          <div className="flex items-center gap-3 p-4 bg-gray-50 rounded-xl">
            <div className="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center">
              <span className="text-green-600 font-bold">+</span>
            </div>
            <div className="flex-1">
              <p className="font-semibold text-gray-900">룰렛 당첨</p>
              <p className="text-xs text-gray-500">2024.01.15</p>
            </div>
            <div className="text-right">
              <p className="font-bold text-green-600">+500p</p>
              <p className="text-xs text-gray-500">D-25</p>
            </div>
          </div>

          <div className="flex items-center gap-3 p-4 bg-gray-50 rounded-xl">
            <div className="w-10 h-10 bg-red-100 rounded-full flex items-center justify-center">
              <span className="text-red-600 font-bold">-</span>
            </div>
            <div className="flex-1">
              <p className="font-semibold text-gray-900">상품 구매</p>
              <p className="text-xs text-gray-500">2024.01.10</p>
            </div>
            <div className="text-right">
              <p className="font-bold text-red-600">-300p</p>
            </div>
          </div>
        </div>

        <div className="mt-6 text-center">
          <p className="text-sm text-gray-500">포인트 내역이 없습니다</p>
        </div>
      </div>

      <div className="bg-amber-50 border border-amber-200 rounded-xl p-4">
        <div className="flex items-start gap-3">
          <span className="text-xl">⏰</span>
          <div className="flex-1">
            <h3 className="font-semibold text-amber-900 mb-1">포인트 유효기간</h3>
            <p className="text-sm text-amber-800">획득일로부터 30일간 사용 가능합니다</p>
          </div>
        </div>
      </div>
    </div>
  );
}
