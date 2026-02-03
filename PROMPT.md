# AI 활용 프롬프트 기록

> **목적**: Point Roulette 프로젝트 개발 과정에서 AI와 협업한 전체 프롬프트 기록
>
> **AI 도구**: Claude Code, NotebookLM, Gemini

---

## 📋 목차

1. [Claude Code](#1-claude-code)
2. [NotebookLM](#2-notebooklm)
3. [Gemini](#3-gemini)
> 관점: [설계], [문제 고민과 해결], [생산성 향상]  

---

## 1. Claude Code

### [설계] ERD 초안 설계

**프롬프트**
```
obsidian에서 '2026 상반기 유저서비스스쿼드 과제' 문서를 바탕으로 백엔드 ERD 설계를 Mermaid로 부탁해.

아래 전략을 반영해줬으면 좋겠어.
1. JPA 연관관계 활용: 기존에는 유연성을 위해 논리적인 ID 참조를 선호하지만 이번에는 JPA 매핑을 활용하려고 해. Lazy Loading + fetch join을 활용할 예정이야.
2. 포인트 만료 관리: 단순히 총액(total_point)만 관리하면 유효기간 처리가 안 되잖아. 포인트를 획득한 건별로 만료일을 기록하고 사용할 때 오래된 순(FIFO)으로 차감할 수 있는 구조가 필요해.
3. 동시성 제어: 일일 예산 테이블은 트래픽이 몰릴 때 비관적 락을 걸어서 정합성을 맞추기 좋은 구조로 잡아줘.
4. 내가 생각한 초안: User, Order, Product, DailyBudget, RouletteHistory, Point, PointHistory 테이블
5. 추가 의견: Point 테이블에는 e.g. expires_at와 같은 만료 시간을 같이 입력해두는 필드가 있으면 좋겠어. 또한 created_at, updated_at 필드는 필수
```

**설명**
- obsidian mcp 활용
- ERD 초안 설계
- 이후 휴먼 검수하며 ERD 환성 

---

## 2. NotebookLM
_(이후 추가 예정)_

---

## 3. Gemini
_(이후 추가 예정)_
