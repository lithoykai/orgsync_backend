CREATE TABLE tb_users (
  user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  enabled BOOLEAN NOT NULL,
  department_id BIGINT NOT NULL DEFAULT 2,
  CONSTRAINT fk_department FOREIGN KEY (department_id) REFERENCES tb_departments(id) ON DELETE CASCADE
);