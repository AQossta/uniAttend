-- Группы студентов
CREATE TABLE t_groups (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    date_registration DATE NOT NULL
);

-- Роли пользователей
CREATE TABLE t_roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);

-- Предметы
CREATE TABLE t_subjects (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- Пользователи
CREATE TABLE t_users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    birthday DATE NOT NULL,
    group_id INT,
    role_id INT NOT NULL,
    FOREIGN KEY (group_id) REFERENCES t_groups(id) ON DELETE SET NULL,
    FOREIGN KEY (role_id) REFERENCES t_roles(id) ON DELETE CASCADE
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