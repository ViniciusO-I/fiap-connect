-- V1__init_schema.sql
-- Schema do FIAP Connect (mantém a estrutura original do skill-hub)

CREATE TABLE user_entity (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    dt_created  TIMESTAMP,
    dt_updated  TIMESTAMP,
    status      VARCHAR(20),
    name        VARCHAR(255),
    email       VARCHAR(255),
    password    VARCHAR(255),
    profile     VARCHAR(30),
    CONSTRAINT uk_user_entity_email UNIQUE (email)
);

CREATE TABLE skill_entity (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    dt_created  TIMESTAMP,
    dt_updated  TIMESTAMP,
    status      VARCHAR(20),
    description VARCHAR(255)
);

CREATE TABLE group_entity (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    dt_created   TIMESTAMP,
    dt_updated   TIMESTAMP,
    status       VARCHAR(20),
    description  VARCHAR(255),
    max_members  INT,
    owner_user_id INT NOT NULL,
    CONSTRAINT fk_group_owner FOREIGN KEY (owner_user_id) REFERENCES user_entity(id)
);

CREATE TABLE user_skill (
    user_id  INT NOT NULL,
    skill_id INT NOT NULL,
    PRIMARY KEY (user_id, skill_id),
    CONSTRAINT fk_user_skill_user  FOREIGN KEY (user_id)  REFERENCES user_entity(id),
    CONSTRAINT fk_user_skill_skill FOREIGN KEY (skill_id) REFERENCES skill_entity(id)
);

CREATE TABLE group_member (
    group_id INT NOT NULL,
    user_id  INT NOT NULL,
    PRIMARY KEY (group_id, user_id),
    CONSTRAINT fk_group_member_group FOREIGN KEY (group_id) REFERENCES group_entity(id),
    CONSTRAINT fk_group_member_user  FOREIGN KEY (user_id)  REFERENCES user_entity(id)
);

CREATE TABLE group_skill_requirement (
    group_id INT NOT NULL,
    skill_id INT NOT NULL,
    PRIMARY KEY (group_id, skill_id),
    CONSTRAINT fk_group_skill_group FOREIGN KEY (group_id) REFERENCES group_entity(id),
    CONSTRAINT fk_group_skill_skill FOREIGN KEY (skill_id) REFERENCES skill_entity(id)
);
