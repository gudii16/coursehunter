-- ============================================================
-- CourseHunter — Course Service Schema
-- ============================================================

CREATE DATABASE course_inventory;

CREATE SCHEMA IF NOT EXISTS course_inventory;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ── Write Model ──────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS course_inventory.courses (
    id               UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    title            VARCHAR(255) NOT NULL,
    description      TEXT,
    instructor_name  VARCHAR(255) NOT NULL,
    status           VARCHAR(20)  NOT NULL DEFAULT 'DRAFT'
                                  CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED')),
    total_seats      INT          NOT NULL CHECK (total_seats > 0),
    start_date       DATE,
    end_date         DATE,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS course_inventory.seat_inventory (
    id               UUID      PRIMARY KEY DEFAULT uuid_generate_v4(),
    course_id        UUID      NOT NULL UNIQUE REFERENCES course_inventory.courses(id) ON DELETE CASCADE,
    total_seats      INT       NOT NULL CHECK (total_seats >= 0),
    reserved_seats   INT       NOT NULL DEFAULT 0 CHECK (reserved_seats >= 0),
    confirmed_seats  INT       NOT NULL DEFAULT 0 CHECK (confirmed_seats >= 0),
    available_seats  INT       GENERATED ALWAYS AS (total_seats - reserved_seats - confirmed_seats) STORED,
    updated_at       TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS course_inventory.seat_reservations (
    id             UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    course_id      UUID        NOT NULL REFERENCES course_inventory.courses(id) ON DELETE CASCADE,
    enrollment_id  UUID        NOT NULL,
    status         VARCHAR(20) NOT NULL DEFAULT 'RESERVED'
                               CHECK (status IN ('RESERVED', 'CONFIRMED', 'RELEASED', 'EXPIRED')),
    expires_at     TIMESTAMP   NOT NULL,
    created_at     TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS course_inventory.tags (
    id    UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    name  VARCHAR(100) NOT NULL UNIQUE,
    slug  VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS course_inventory.course_tags (
    course_id  UUID NOT NULL REFERENCES course_inventory.courses(id) ON DELETE CASCADE,
    tag_id     UUID NOT NULL REFERENCES course_inventory.tags(id) ON DELETE CASCADE,
    PRIMARY KEY (course_id, tag_id)
);

-- ── Read Model ───────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS course_inventory.course_catalog_view (
    id               UUID         PRIMARY KEY,
    title            VARCHAR(255) NOT NULL,
    description      TEXT,
    instructor_name  VARCHAR(255),
    status           VARCHAR(20),
    total_seats      INT,
    available_seats  INT,
    start_date       DATE,
    end_date         DATE,
    tags             VARCHAR(100)[],
    updated_at       TIMESTAMP
);

-- ── Indexes ──────────────────────────────────────────────────

CREATE INDEX IF NOT EXISTS idx_courses_status      ON course_inventory.courses(status);
CREATE INDEX IF NOT EXISTS idx_courses_instructor  ON course_inventory.courses(instructor_name);

CREATE INDEX IF NOT EXISTS idx_seat_reservations_course      ON course_inventory.seat_reservations(course_id);
CREATE INDEX IF NOT EXISTS idx_seat_reservations_enrollment  ON course_inventory.seat_reservations(enrollment_id);
CREATE INDEX IF NOT EXISTS idx_seat_reservations_status      ON course_inventory.seat_reservations(status);
CREATE INDEX IF NOT EXISTS idx_seat_reservations_expires_at  ON course_inventory.seat_reservations(expires_at)
    WHERE status = 'RESERVED';

CREATE INDEX IF NOT EXISTS idx_catalog_tags    ON course_inventory.course_catalog_view USING GIN(tags);
CREATE INDEX IF NOT EXISTS idx_catalog_status  ON course_inventory.course_catalog_view(status);

-- ── Auto-update updated_at trigger ───────────────────────────

CREATE OR REPLACE FUNCTION course_inventory.update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_courses_updated_at
    BEFORE UPDATE ON course_inventory.courses
    FOR EACH ROW EXECUTE FUNCTION course_inventory.update_updated_at();

CREATE OR REPLACE TRIGGER trg_seat_inventory_updated_at
    BEFORE UPDATE ON course_inventory.seat_inventory
    FOR EACH ROW EXECUTE FUNCTION course_inventory.update_updated_at();

CREATE OR REPLACE TRIGGER trg_seat_reservations_updated_at
    BEFORE UPDATE ON course_inventory.seat_reservations
    FOR EACH ROW EXECUTE FUNCTION course_inventory.update_updated_at();
