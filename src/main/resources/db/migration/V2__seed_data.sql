-- V2__seed_data.sql
-- Dados iniciais para demonstração

-- Usuário ADMINISTRATOR (será associado ao login do GitHub via OAuth2)
-- A senha está em branco pois o login é feito pelo GitHub
INSERT INTO user_entity (dt_created, status, name, email, password, profile)
VALUES (NOW(), 'ACTIVE', 'Admin FIAP', 'admin@github', '', 'ADMINISTRATOR');

-- Usuário STUDENT de exemplo
INSERT INTO user_entity (dt_created, status, name, email, password, profile)
VALUES (NOW(), 'ACTIVE', 'Estudante Demo', 'estudante@github', '', 'STUDENT');

-- Skills iniciais
INSERT INTO skill_entity (dt_created, status, description) VALUES (NOW(), 'ACTIVE', 'Java');
INSERT INTO skill_entity (dt_created, status, description) VALUES (NOW(), 'ACTIVE', 'Spring Boot');
INSERT INTO skill_entity (dt_created, status, description) VALUES (NOW(), 'ACTIVE', 'React');
INSERT INTO skill_entity (dt_created, status, description) VALUES (NOW(), 'ACTIVE', 'SQL');
INSERT INTO skill_entity (dt_created, status, description) VALUES (NOW(), 'ACTIVE', 'DevOps');
INSERT INTO skill_entity (dt_created, status, description) VALUES (NOW(), 'ACTIVE', 'UX/UI Design');
INSERT INTO skill_entity (dt_created, status, description) VALUES (NOW(), 'ACTIVE', 'Python');
INSERT INTO skill_entity (dt_created, status, description) VALUES (NOW(), 'ACTIVE', 'Docker');

-- Skills do usuário admin (id=1)
INSERT INTO user_skill (user_id, skill_id) VALUES (1, 1); -- Java
INSERT INTO user_skill (user_id, skill_id) VALUES (1, 2); -- Spring Boot
INSERT INTO user_skill (user_id, skill_id) VALUES (1, 4); -- SQL

-- Grupo de exemplo criado pelo admin
INSERT INTO group_entity (dt_created, status, description, max_members, owner_user_id)
VALUES (NOW(), 'ACTIVE', 'Projeto Full Stack - Turma A', 4, 1);

-- Skills obrigatórias do grupo (Java + SQL)
INSERT INTO group_skill_requirement (group_id, skill_id) VALUES (1, 1); -- Java
INSERT INTO group_skill_requirement (group_id, skill_id) VALUES (1, 4); -- SQL

-- Admin já é membro do grupo
INSERT INTO group_member (group_id, user_id) VALUES (1, 1);
