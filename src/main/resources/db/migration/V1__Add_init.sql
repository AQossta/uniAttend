-- Группы студентов
CREATE TABLE t_groups (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    date_registration DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS t_images (
    id                  BIGSERIAL NOT NULL,
    image_path          VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

-- Роли пользователей
CREATE TABLE t_roles (
    id SERIAL PRIMARY KEY,
    role_name VARCHAR(20) NOT NULL
);

-- Предметы
CREATE TABLE t_subjects (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS t_users
(
    id                  BIGSERIAL       NOT NULL,
    user_name           VARCHAR(40)     NOT NULL,
    password            VARCHAR(256)    NOT NULL,
    email               VARCHAR(70)     NOT NULL,
    birthday DATE,
    phone_number        VARCHAR(15)     NOT NULL,
    registration_date   TIMESTAMP       NOT NULL DEFAULT current_timestamp,
    email_verified      BOOLEAN         NOT NULL,
    image_id            BIGINT          NULL,
    group_id INT,
    PRIMARY KEY (id),
    FOREIGN KEY (image_id) REFERENCES t_images(id) ON DELETE SET NULL, -- Убираем CASCADE
    FOREIGN KEY (group_id) REFERENCES t_groups(id) ON DELETE SET NULL,
    UNIQUE (email, phone_number)
);

-- Расписания
CREATE TABLE t_schedules (
    id SERIAL PRIMARY KEY,
    subject_id INT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    group_id INT NOT NULL,
    lecturer_id INT NOT NULL,
    CHECK (end_time > start_time),
    FOREIGN KEY (group_id) REFERENCES t_groups(id) ON DELETE CASCADE,
    FOREIGN KEY (lecturer_id) REFERENCES t_users(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES t_subjects(id) ON DELETE CASCADE,
    UNIQUE (subject_id, start_time, end_time, group_id)
);

-- QR-коды
CREATE TABLE t_qr_codes (
    id SERIAL PRIMARY KEY,
    qr_code TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    expiration TIMESTAMP NOT NULL
);

-- Связь QR-кодов и расписаний
CREATE TABLE t_qr_for_schedules (
    id SERIAL PRIMARY KEY,
    schedule_id INT NOT NULL,
    qr_code_id INT NOT NULL,
    FOREIGN KEY (schedule_id) REFERENCES t_schedules(id) ON DELETE CASCADE,
    FOREIGN KEY (qr_code_id) REFERENCES t_qr_codes(id) ON DELETE CASCADE
);

-- Посещаемость
CREATE TABLE t_attendance (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    schedule_id INT NOT NULL,
    scan_time TIMESTAMP NOT NULL,
    scan_type VARCHAR(10) CHECK (scan_type IN ('IN', 'OUT')),
    FOREIGN KEY (user_id) REFERENCES t_users(id) ON DELETE CASCADE,
    FOREIGN KEY (schedule_id) REFERENCES t_schedules(id) ON DELETE CASCADE
);

CREATE INDEX idx_attendance_user_schedule ON t_attendance(user_id, schedule_id);

-- Триггер для проверки порядка IN/OUT
CREATE OR REPLACE FUNCTION check_attendance_order()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.scan_type = 'OUT' THEN
        IF NOT EXISTS (
            SELECT 1 FROM t_attendance
            WHERE user_id = NEW.user_id
            AND schedule_id = NEW.schedule_id
            AND scan_type = 'IN'
        ) THEN
            RAISE EXCEPTION 'Нельзя выйти без входа!';
END IF;
    ELSIF NEW.scan_type = 'IN' THEN
        IF EXISTS (
            SELECT 1 FROM t_attendance
            WHERE user_id = NEW.user_id
            AND schedule_id = NEW.schedule_id
            AND scan_type = 'IN'
            AND NOT EXISTS (
                SELECT 1 FROM t_attendance
                WHERE user_id = NEW.user_id
                AND schedule_id = NEW.schedule_id
                AND scan_type = 'OUT'
            )
        ) THEN
            RAISE EXCEPTION 'Нельзя войти повторно без выхода!';
END IF;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER attendance_order_trigger
    BEFORE INSERT ON t_attendance
    FOR EACH ROW EXECUTE FUNCTION check_attendance_order();

-- Сессии пользователей
CREATE TABLE t_sessions (
    id SERIAL PRIMARY KEY,
    token TEXT NOT NULL,
    expiration TIMESTAMP NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES t_users(id) ON DELETE CASCADE
);

-- Верификация email
CREATE TABLE t_mail_verifications (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    code VARCHAR(6) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Приглашения
CREATE TABLE t_invites (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    link VARCHAR(255) NOT NULL,
    date_create TIMESTAMP NOT NULL DEFAULT NOW(),
    expiration TIMESTAMP NOT NULL,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES t_users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS t_user_roles
(
    id          BIGSERIAL               NOT NULL,
    user_id     BIGINT                  NOT NULL,
    role_id     BIGINT                  NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES t_users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES t_roles (id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS t_organization (
    bin                 CHAR(12) NOT NULL, -- Уникальный бизнес-идентификационный номер
    organization_name   VARCHAR(255) NOT NULL, -- Название организации
    email               VARCHAR(254) NOT NULL, -- Email
    owner_name          VARCHAR(255) NOT NULL, -- Имя владельца
    phone_number        VARCHAR(15) NOT NULL, -- Номер телефона
    registration_date   TIMESTAMP NOT NULL DEFAULT current_timestamp, -- Дата регистрации
    website_link        VARCHAR(255) NOT NULL, -- Ссылка на сайт
    address             TEXT NOT NULL, -- Полный адрес
    image_id            BIGINT NULL, -- Идентификатор изображения
    PRIMARY KEY (bin), -- БИН используется как первичный ключ
    FOREIGN KEY (image_id) REFERENCES t_images(id) ON DELETE SET NULL,
    UNIQUE (email, phone_number) -- Уникальные ограничения
);

INSERT INTO t_users (id, user_name, password, email, phone_number, registration_date, email_verified)
VALUES
    (1, 'Azhar', '$2a$12$Nr0hmjOjBmTaB0M91m.6Qu/Zm943j9Coq7NMWzpSX7UTFYe1m6nam', 'Azhar@gmail.com', '+77011112235', CURRENT_TIMESTAMP, false),
    (2, 'Yerkebulan', '$2a$12$TxN7SBjj.MbnlQz9mnDA2e8dbEq7bZsPH5P7cNBKlukBnq3VukVFW', 'erkebulanzholdaskali@gmail.com', '+77478708845', CURRENT_TIMESTAMP, false),
    (3, 'Uchenik', '$2a$12$pBTccEPl6OZXXxCqT8PyPOW/WDLbAsTpFziKjYIVK.1PzoreTMq9S', 'Uchenik@gmail.com', '+77011112233', CURRENT_TIMESTAMP, false),
    (4, 'student', '$2a$12$kDfjYF/Pc1j8Ky1i0f6RX.Du0zJfS4ZqnCTca.K72U4EuymNd2Qpu', 'student@gmail.com', '+77011112234', CURRENT_TIMESTAMP, false);


INSERT INTO t_roles (id, role_name)
VALUES
    (1, 'admin'),
    (2, 'user'),
    (3, 'teacher'),
    (4, 'student');

INSERT INTO t_user_roles (id, user_id, role_id)
VALUES
    (1, 1, 1),
    (2, 2, 1),
    (3, 3, 3),
    (4, 4, 4);

INSERT INTO t_organization (
    bin, organization_name, email, owner_name, phone_number, website_link, address
) VALUES (
             '1',
             'EURASIAN NATIONAL UNIVERSITY',
             'enu@enu.kz',
             'Бастрыкин Олег Викторович',
             '+7(7172)709500',
             'https://enu.kz/ru/',
             'г.Астана, ул. Сатпаева, 2'
);

INSERT INTO t_groups (name, date_registration) VALUES
    ('Group A-101', '2023-09-01'),
    ('Group E-505', '2025-02-28');

SELECT setval(pg_get_serial_sequence('t_users', 'id'), 5, false);

SELECT setval(pg_get_serial_sequence('t_user_roles', 'id'), 5, false);

SELECT setval(pg_get_serial_sequence('t_groups', 'id'), 3, false);