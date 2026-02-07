export const metadata = {
  title: '주문 내역 - Point Roulette',
};

export default function OrdersPage() {
  return (
    <div className="p-5 space-y-5">
      <div className="bg-indigo-600 rounded-2xl p-6 text-white shadow-lg">
        <h2 className="text-2xl font-bold mb-2">주문 내역</h2>
        <p className="text-indigo-100 text-sm">포인트로 구매한 상품을 확인하세요</p>
      </div>

      <div className="space-y-3">
        <div className="bg-white rounded-xl shadow-md border border-gray-100 p-5">
          <div className="flex items-start gap-4">
            <div className="flex-1 min-w-0">
              <div className="flex items-start justify-between mb-2">
                <div>
                  <h3 className="font-bold text-gray-900">스타벅스 아메리카노</h3>
                  <p className="text-sm text-gray-500">2024.01.15 14:30</p>
                </div>
                <span className="px-2.5 py-1 bg-green-100 text-green-700 text-xs font-semibold rounded-full whitespace-nowrap">
                  완료
                </span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">사용 포인트</span>
                <span className="font-bold text-indigo-600">5,000p</span>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-md border border-gray-100 p-5">
          <div className="flex items-start gap-4">
            <div className="flex-1 min-w-0">
              <div className="flex items-start justify-between mb-2">
                <div>
                  <h3 className="font-bold text-gray-900">GS25 상품권</h3>
                  <p className="text-sm text-gray-500">2024.01.10 09:15</p>
                </div>
                <span className="px-2.5 py-1 bg-green-100 text-green-700 text-xs font-semibold rounded-full whitespace-nowrap">
                  완료
                </span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">사용 포인트</span>
                <span className="font-bold text-indigo-600">10,000p</span>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-md border border-gray-100 p-5 opacity-60">
          <div className="flex items-start gap-4">
            <div className="flex-1 min-w-0">
              <div className="flex items-start justify-between mb-2">
                <div>
                  <h3 className="font-bold text-gray-900">배달의민족 쿠폰</h3>
                  <p className="text-sm text-gray-500">2024.01.05 18:20</p>
                </div>
                <span className="px-2.5 py-1 bg-red-100 text-red-700 text-xs font-semibold rounded-full whitespace-nowrap">
                  취소됨
                </span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-sm text-gray-600">환불 포인트</span>
                <span className="font-bold text-gray-500">+3,000p</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-md border border-gray-100 p-8 text-center">
        <div className="text-5xl mb-3">📦</div>
        <p className="text-gray-500 font-medium mb-1">주문 내역이 없습니다</p>
        <p className="text-sm text-gray-400">포인트로 상품을 구매해보세요</p>
      </div>
    </div>
  );
}
