-- products 테이블
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    price INTEGER NOT NULL,
    stock INTEGER NOT NULL,
    description TEXT,
    status TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스
CREATE INDEX idx_products_status ON products(status);

-- 코멘트
COMMENT ON TABLE products IS '상품 정보';
COMMENT ON COLUMN products.id IS 'Auto increment';
COMMENT ON COLUMN products.name IS '상품명';
COMMENT ON COLUMN products.price IS '가격 (포인트)';
COMMENT ON COLUMN products.stock IS '재고 수량';
COMMENT ON COLUMN products.description IS '상품 설명';
COMMENT ON COLUMN products.status IS '상품 상태 (ACTIVE, INACTIVE)';
COMMENT ON COLUMN products.created_at IS '생성일';
COMMENT ON COLUMN products.updated_at IS '수정일';
