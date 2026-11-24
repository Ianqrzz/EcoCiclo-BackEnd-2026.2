-- Usuario administrador inicial (APENAS para desenvolvimento local).
-- Senha padrao: Admin@123  (hash BCrypt, custo 10, conferido em DataSqlSenhaAdminTest)
-- Em producao/staging, troque a senha apos o primeiro login.

INSERT INTO tb_usuarios (id, nome, cpf, telefone, email, senha, perfil)
VALUES (
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    'Administrador',
    '00000000000',
    NULL,
    'admin@ecociclo.com',
    '$2a$10$zBf311bjvT45Fc3A4fMYDehYppzTA89OqtZa7S0/E9ucKBCY21602',
    'ADMINISTRADOR'
) ON CONFLICT DO NOTHING;

INSERT INTO tb_administradores (id)
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11')
ON CONFLICT DO NOTHING;