CREATE TABLE tb_departments (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO tb_departments (id, name, description, enabled)
VALUES
    (1, 'Administrativo', 'Departamento responsável pela administração geral', TRUE),
    (2, 'Sem Departamento', 'Usuários que ainda não pertencem a um departamento específico', TRUE)
    ON CONFLICT (id) DO NOTHING;
SELECT setval('tb_departments_id_seq', (SELECT MAX(id) FROM tb_departments) + 1);