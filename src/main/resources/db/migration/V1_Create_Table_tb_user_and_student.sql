CREATE TABLE tb_user (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         email VARCHAR(255) NOT NULL,
                         password VARCHAR(255) NOT NULL,
                         cpf VARCHAR(14) NOT NULL,
                         birthday DATE,
                         genero VARCHAR(10), -- MALE, FEMALE, OTHER
                         favorite_language VARCHAR(20), -- PORTUGUESE, ENGLISH
                         account_type VARCHAR(20), -- STUDENT, TEACHER, ENTERPRISE
                         status VARCHAR(10), -- ACTIVE, INACTIVE
                         created_at TIMESTAMP,
                         updated_at TIMESTAMP
);