CREATE TABLE tb_departments (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO tb_departments (id, name, description, enabled)
VALUES (1, 'Administrativo', 'Departamento responsável pela administração geral', true)
    ON CONFLICT (id) DO NOTHING;