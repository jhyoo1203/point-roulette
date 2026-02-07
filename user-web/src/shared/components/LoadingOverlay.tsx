'use client';

export function LoadingOverlay({ message = '로딩 중...' }: { message?: string }) {
  return (
    <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50">
      <div className="bg-white rounded-2xl shadow-2xl p-8 flex flex-col items-center gap-4 border border-gray-100">
        <div className="relative">
          <div className="w-16 h-16 border-4 border-indigo-100 rounded-full" />
          <div className="absolute inset-0 w-16 h-16 border-4 border-transparent border-t-indigo-600 rounded-full animate-spin" />
        </div>
        <p className="text-gray-700 font-semibold">{message}</p>
      </div>
    </div>
  );
}
