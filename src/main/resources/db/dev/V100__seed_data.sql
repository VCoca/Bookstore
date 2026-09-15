-- admin nalog
-- lozinka: admin1234 (BCrypt heš)
INSERT INTO users (jmbg, first_name, last_name, role, email, password_hashed) VALUES
    ('0101990710011', 'Marko', 'Marković', 'ADMIN', 'admin@bookstore.local',
     '$2a$10$8FkiuAQTYE.hvmX5hyV6b.fYzzV1SSSJU1bwtQgVtH/GadsrjW1Gy');

-- obican korisnik
-- lozinka: user1234
INSERT INTO users (jmbg, first_name, last_name, role, email, password_hashed) VALUES
    ('0202991710022', 'Ivan', 'Ivanović', 'USER', 'ivan@bookstore.local',
     '$2a$10$MZgW78PP25MBLY3vIy6EvODh/cnJk61sF594B2UESfXlYrGa.MTjK');

INSERT INTO books (title, author, isbn, published_year, available_copies, price) VALUES
    ('Na Drini ćuprija', 'Ivo Andrić',       '9788610010114', 1945, 5,  1200.00),
    ('Seobe',            'Miloš Crnjanski',  '9788610012369', 1929, 3,   980.00),
    ('Prokleta avlija',  'Ivo Andrić',       '9788610011234', 1954, 2,   850.00),
    ('Clean Code',       'Robert C. Martin', '9780132350884', 2008, 10, 3400.00),
    ('Nečista krv',      'Borisav Stanković','9788610013456', 1910, 0,   790.00);