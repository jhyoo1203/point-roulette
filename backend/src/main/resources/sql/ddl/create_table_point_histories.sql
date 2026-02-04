-- point_histories 테이블
CREATE TABLE point_histories (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    point_id BIGINT,
    amount INTEGER NOT NULL,
    transaction_type TEXT NOT NULL,
    reference_type TEXT NOT NULL,
    reference_id BIGINT NOT NULL,
    balance_after INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_point_histories_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_point_histories_point FOREIGN KEY (point_id) REFERENCES points(id)
);

-- 인덱스
CREATE INDEX idx_point_histories_user_id ON point_histories(user_id);
CREATE INDEX idx_point_histories_point_id ON point_histories(point_id);
CREATE INDEX idx_point_histories_created_at ON point_histories(created_at);
CREATE INDEX idx_point_histories_reference ON point_histories(reference_type, reference_id);

-- 코멘트
COMMENT ON TABLE point_histories IS '포인트 변동 이력';
COMMENT ON COLUMN point_histories.id IS 'Auto increment';
COMMENT ON COLUMN point_histories.user_id IS 'users.id';
COMMENT ON COLUMN point_histories.point_id IS 'points.id (nullable)';
COMMENT ON COLUMN point_histories.amount IS '변동 포인트 (+/-)';
COMMENT ON COLUMN point_histories.transaction_type IS '거래 유형 (EARN, USE, REFUND, EXPIRE, CANCEL)';
COMMENT ON COLUMN point_histories.reference_type IS '참조 타입 (ROULETTE, ORDER)';
COMMENT ON COLUMN point_histories.reference_id IS '참조 ID';
COMMENT ON COLUMN point_histories.balance_after IS '거래 후 잔액';
COMMENT ON COLUMN point_histories.created_at IS '생성일';
COMMENT ON COLUMN point_histories.updated_at IS '수정일';
