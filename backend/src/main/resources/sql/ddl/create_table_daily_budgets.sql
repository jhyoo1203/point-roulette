-- daily_budgets 테이블
CREATE TABLE daily_budgets (
    id BIGSERIAL PRIMARY KEY,
    budget_date DATE NOT NULL,
    total_amount INTEGER NOT NULL DEFAULT 100000,
    remaining_amount INTEGER NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 유니크 제약조건: 예산 날짜
CREATE UNIQUE INDEX uk_daily_budgets_budget_date ON daily_budgets(budget_date);

-- 코멘트
COMMENT ON TABLE daily_budgets IS '일일 예산 관리';
COMMENT ON COLUMN daily_budgets.id IS 'Auto increment';
COMMENT ON COLUMN daily_budgets.budget_date IS '예산 날짜(YYYY-MM-DD)';
COMMENT ON COLUMN daily_budgets.total_amount IS '총 예산 (기본 100,000p)';
COMMENT ON COLUMN daily_budgets.remaining_amount IS '잔여 예산';
COMMENT ON COLUMN daily_budgets.version IS '낙관적 락용 버전';
COMMENT ON COLUMN daily_budgets.created_at IS '생성일';
COMMENT ON COLUMN daily_budgets.updated_at IS '수정일';
