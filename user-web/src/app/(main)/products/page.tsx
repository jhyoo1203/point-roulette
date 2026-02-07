export const metadata = {
  title: '상품 목록 - Point Roulette',
};

export default function ProductsPage() {
  return (
    <div className="p-5 space-y-5">
      <div className="bg-indigo-600 rounded-2xl p-6 text-white shadow-lg">
        <h2 className="text-2xl font-bold mb-2">상품 목록</h2>
        <p className="text-indigo-100 text-sm">포인트로 다양한 상품을 구매하세요</p>
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div className="bg-white rounded-xl shadow-md border border-gray-100 overflow-hidden">
          <div className="p-4">
            <h3 className="font-bold text-gray-900 mb-1">스타벅스</h3>
            <p className="text-xs text-gray-500 mb-2">아메리카노 Tall</p>
            <div className="flex items-center justify-between">
              <span className="text-lg font-bold text-indigo-600">5,000p</span>
              <button className="px-3 py-1.5 bg-indigo-600 text-white text-xs font-semibold rounded-lg hover:bg-indigo-700 transition">
                구매
              </button>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-md border border-gray-100 overflow-hidden">
          <div className="p-4">
            <h3 className="font-bold text-gray-900 mb-1">GS25</h3>
            <p className="text-xs text-gray-500 mb-2">모바일 상품권</p>
            <div className="flex items-center justify-between">
              <span className="text-lg font-bold text-indigo-600">10,000p</span>
              <button className="px-3 py-1.5 bg-indigo-600 text-white text-xs font-semibold rounded-lg hover:bg-indigo-700 transition">
                구매
              </button>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-md border border-gray-100 overflow-hidden">
          <div className="p-4">
            <h3 className="font-bold text-gray-900 mb-1">Steam</h3>
            <p className="text-xs text-gray-500 mb-2">게임 쿠폰</p>
            <div className="flex items-center justify-between">
              <span className="text-lg font-bold text-indigo-600">15,000p</span>
              <button className="px-3 py-1.5 bg-gray-300 text-gray-500 text-xs font-semibold rounded-lg cursor-not-allowed">
                품절
              </button>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-md border border-gray-100 overflow-hidden">
          <div className="p-4">
            <h3 className="font-bold text-gray-900 mb-1">배달의민족</h3>
            <p className="text-xs text-gray-500 mb-2">할인 쿠폰</p>
            <div className="flex items-center justify-between">
              <span className="text-lg font-bold text-indigo-600">3,000p</span>
              <button className="px-3 py-1.5 bg-indigo-600 text-white text-xs font-semibold rounded-lg hover:bg-indigo-700 transition">
                구매
              </button>
            </div>
          </div>
        </div>
      </div>

      <div className="text-center pt-4">
        <p className="text-sm text-gray-500">상품 준비 중입니다</p>
      </div>
    </div>
  );
}
