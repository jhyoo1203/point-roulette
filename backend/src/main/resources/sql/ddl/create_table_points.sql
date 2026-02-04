-- points 테이블
CREATE TABLE points (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    initial_amount INTEGER NOT NULL,
    remaining_amount INTEGER NOT NULL,
    earned_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    status TEXT NOT NULL,
    source_type TEXT NOT NULL,
    source_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_points_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 인덱스
CREATE INDEX idx_points_user_id ON points(user_id);
CREATE INDEX idx_points_status ON points(status);
CREATE INDEX idx_points_expires_at ON points(expires_at);
CREATE INDEX idx_points_source ON points(source_type, source_id);

-- 코멘트
COMMENT ON TABLE points IS '포인트 지갑';
COMMENT ON COLUMN points.id IS 'Auto increment';
COMMENT ON COLUMN points.user_id IS 'users.id';
COMMENT ON COLUMN points.initial_amount IS '최초 획득 포인트';
COMMENT ON COLUMN points.remaining_amount IS '잔여 포인트 (부분 사용 가능)';
COMMENT ON COLUMN points.earned_at IS '획득 시각';
COMMENT ON COLUMN points.expires_at IS '만료 시각 (earned_at + 30일)';
COMMENT ON COLUMN points.status IS '포인트 상태 (ACTIVE, USED, EXPIRED, CANCELLED)';
COMMENT ON COLUMN points.source_type IS '획득 경로 (ROULETTE, REFUND)';
COMMENT ON COLUMN points.source_id IS '경로별 ID (roulette_histories.id or orders.id)';
COMMENT ON COLUMN points.created_at IS '생성일';
COMMENT ON COLUMN points.updated_at IS '수정일';
