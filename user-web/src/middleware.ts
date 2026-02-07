import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

// 인증이 필요한 경로
const protectedPaths = ['/roulette', '/points', '/products', '/orders'];

// 인증 후 접근 불가한 경로 (로그인 페이지 등)
const authPaths = ['/login'];

export function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;
  const userId = request.cookies.get('userId')?.value;

  // 인증이 필요한 경로
  if (protectedPaths.some((path) => pathname.startsWith(path))) {
    if (!userId) {
      const loginUrl = new URL('/login', request.url);
      return NextResponse.redirect(loginUrl);
    }
  }

  // 이미 로그인한 사용자가 로그인 페이지 접근 시 리다이렉트
  if (authPaths.some((path) => pathname.startsWith(path))) {
    if (userId) {
      const rouletteUrl = new URL('/roulette', request.url);
      return NextResponse.redirect(rouletteUrl);
    }
  }

  // 루트 경로 처리
  if (pathname === '/') {
    if (userId) {
      const rouletteUrl = new URL('/roulette', request.url);
      return NextResponse.redirect(rouletteUrl);
    } else {
      const loginUrl = new URL('/login', request.url);
      return NextResponse.redirect(loginUrl);
    }
  }

  return NextResponse.next();
}

export const config = {
  matcher: ['/', '/login', '/roulette/:path*', '/points/:path*', '/products/:path*', '/orders/:path*'],
};
