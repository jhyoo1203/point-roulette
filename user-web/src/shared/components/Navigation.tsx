'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { Disc3, Wallet, Store, Receipt } from 'lucide-react';

const navItems = [
  { href: '/roulette', label: '룰렛', Icon: Disc3 },
  { href: '/points', label: '포인트', Icon: Wallet },
  { href: '/products', label: '상품', Icon: Store },
  { href: '/orders', label: '주문', Icon: Receipt },
];

export function Navigation() {
  const pathname = usePathname();

  return (
    <nav className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 shadow-lg safe-area-inset-bottom">
      <div className="flex justify-around items-center h-16 max-w-screen-sm mx-auto px-2">
        {navItems.map((item) => {
          const isActive = pathname.startsWith(item.href);
          const Icon = item.Icon;
          return (
            <Link
              key={item.href}
              href={item.href}
              className="relative flex flex-col items-center justify-center flex-1 h-full group"
            >
              <div className={`flex flex-col items-center justify-center transition-all ${
                isActive
                  ? 'text-indigo-600 scale-105'
                  : 'text-gray-400 group-hover:text-gray-600'
              }`}>
                <Icon className={`w-6 h-6 mb-0.5 transition-transform ${
                  isActive ? 'scale-110' : 'group-hover:scale-105'
                }`} />
                <span className={`text-xs font-medium transition-all ${
                  isActive ? 'font-semibold' : ''
                }`}>{item.label}</span>
              </div>
              {isActive && (
                <div className="absolute top-0 left-1/2 -translate-x-1/2 w-12 h-1 bg-indigo-600 rounded-b-full" />
              )}
            </Link>
          );
        })}
      </div>
    </nav>
  );
}
