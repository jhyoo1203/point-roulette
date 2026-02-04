-- orders 테이블
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    total_price INTEGER NOT NULL,
    status TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_orders_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- 인덱스
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_product_id ON orders(product_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);

-- 코멘트
COMMENT ON TABLE orders IS '주문 정보';
COMMENT ON COLUMN orders.id IS 'Auto increment';
COMMENT ON COLUMN orders.user_id IS 'users.id';
COMMENT ON COLUMN orders.product_id IS 'products.id';
COMMENT ON COLUMN orders.quantity IS '수량';
COMMENT ON COLUMN orders.total_price IS '총 가격 (price * quantity)';
COMMENT ON COLUMN orders.status IS '주문 상태 (COMPLETED, CANCELLED)';
COMMENT ON COLUMN orders.created_at IS '생성일';
COMMENT ON COLUMN orders.updated_at IS '수정일';
