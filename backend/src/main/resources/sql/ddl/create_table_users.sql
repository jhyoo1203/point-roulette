-- users 테이블
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    nickname TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 유니크 제약조건: 닉네임
CREATE UNIQUE INDEX uk_users_nickname ON users(nickname);

-- 코멘트
COMMENT ON TABLE users IS '사용자 정보';
COMMENT ON COLUMN users.id IS 'Auto increment';
COMMENT ON COLUMN users.nickname IS '로그인용 닉네임';
COMMENT ON COLUMN users.created_at IS '가입일';
COMMENT ON COLUMN users.updated_at IS '수정일';
