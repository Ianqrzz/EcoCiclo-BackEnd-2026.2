-- Senha padrão: Admin@123
INSERT INTO tb_usuarios (id, nome, cpf, telefone, email, senha, perfil)
VALUES (
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    'Administrador',
    '00000000000',
    NULL,
    'admin@ecociclo.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    'ADMINISTRADOR'
) ON CONFLICT DO NOTHING;

INSERT INTO tb_administradores (id)
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11')
ON CONFLICT DO NOTHING;