-- roulette_histories 테이블
CREATE TABLE roulette_histories (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    participated_date DATE NOT NULL,
    won_amount INTEGER NOT NULL,
    daily_budget_id BIGINT NOT NULL,
    status TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_roulette_histories_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_roulette_histories_daily_budget FOREIGN KEY (daily_budget_id) REFERENCES daily_budgets(id)
);

-- 유니크 제약조건: 사용자별 날짜당 1회 참여 제한
CREATE UNIQUE INDEX uk_roulette_histories_user_date ON roulette_histories(user_id, participated_date);

-- 인덱스
CREATE INDEX idx_roulette_histories_user_id ON roulette_histories(user_id);
CREATE INDEX idx_roulette_histories_participated_date ON roulette_histories(participated_date);
CREATE INDEX idx_roulette_histories_daily_budget_id ON roulette_histories(daily_budget_id);

-- 코멘트
COMMENT ON TABLE roulette_histories IS '룰렛 참여 이력';
COMMENT ON COLUMN roulette_histories.id IS 'Auto increment';
COMMENT ON COLUMN roulette_histories.user_id IS 'users.id';
COMMENT ON COLUMN roulette_histories.participated_date IS '참여 날짜';
COMMENT ON COLUMN roulette_histories.won_amount IS '당첨 포인트 (100~1000p)';
COMMENT ON COLUMN roulette_histories.daily_budget_id IS 'daily_budgets.id';
COMMENT ON COLUMN roulette_histories.status IS '참여 상태 (SUCCESS, CANCELLED)';
COMMENT ON COLUMN roulette_histories.created_at IS '생성일';
COMMENT ON COLUMN roulette_histories.updated_at IS '수정일';
