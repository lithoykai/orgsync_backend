CREATE TABLE tb_roles (
                          role_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          name VARCHAR(50) UNIQUE NOT NULL
);

