/* ---------- ENUM TYPES ---------- */
CREATE TYPE affinity            AS ENUM ('FIRE','WATER','EARTH','WIND','LIFE','DEATH');          -- valid elemental affinities :contentReference[oaicite:8]{index=8}
CREATE TYPE specialization_code AS ENUM ('ELEMENTAL','BLOOD','NECROMANCY','LIFE','ARCANE');      -- valid specialities :contentReference[oaicite:9]{index=9}
CREATE TYPE enrollment_status   AS ENUM ('ENROLLED','WITHDRAWN');
CREATE TYPE submission_status   AS ENUM ('PENDING','APPROVED','REJECTED');

/* ---------- CORE TABLES ---------- */

/* Each speciality can take up to 100 students per year (capacity is editable if rules change) */
CREATE TABLE specialization (
                                id            BIGSERIAL       PRIMARY KEY,
                                code          specialization_code UNIQUE NOT NULL,
                                max_capacity  INT             NOT NULL DEFAULT 100   -- rule: 100 seats/year :contentReference[oaicite:10]{index=10}
);

/* Master record for a student, independent of any particular year */
CREATE TABLE student (
                         id             BIGSERIAL PRIMARY KEY,
                         first_name     VARCHAR(50)  NOT NULL,
                         last_name      VARCHAR(50)  NOT NULL,
                         current_ssn    CHAR(9)      UNIQUE NOT NULL,
                         prev_ssn       CHAR(9)      UNIQUE,                  -- keeps history for SSN changes :contentReference[oaicite:11]{index=11}
                         hp             INT          NOT NULL,                -- Health Points
                         mp             INT          NOT NULL,                -- Mana Points
                         iq             INT          NOT NULL,                -- IQ
                         affinity       affinity     NOT NULL,                -- Elemental Affinity :contentReference[oaicite:12]{index=12}
                         email          VARCHAR(320) NOT NULL,
                         phone          VARCHAR(20)
);

/* An enrolment links a student to a single speciality in a single year */
CREATE TABLE enrollment (
                            id               BIGSERIAL        PRIMARY KEY,
                            student_id       BIGINT           NOT NULL REFERENCES student(id) ON DELETE CASCADE,
                            specialization_id BIGINT          NOT NULL REFERENCES specialization(id),
                            year             INT              NOT NULL,
                            status           enrollment_status NOT NULL DEFAULT 'ENROLLED',
                            UNIQUE (student_id, year),                                   -- one year per student :contentReference[oaicite:13]{index=13}
                            UNIQUE (specialization_id, year, student_id)
);

/* Raw application/withdrawal form data (immutable audit log) */
CREATE TABLE submission (
                            id                  BIGSERIAL        PRIMARY KEY,
                            student_id          BIGINT           REFERENCES student(id) ON DELETE SET NULL,
                            first_name          VARCHAR(50)  NOT NULL,
                            last_name           VARCHAR(50)  NOT NULL,
                            current_ssn         CHAR(9)      NOT NULL,
                            new_ssn             CHAR(9),
                            iq                  INT          NOT NULL,
                            hp                  INT          NOT NULL,
                            mp                  INT          NOT NULL,
                            affinity            affinity     NOT NULL,
                            desired_spec_id     BIGINT       NOT NULL REFERENCES specialization(id),
                            desired_year        INT          NOT NULL,
                            email               VARCHAR(320) NOT NULL,
                            phone               VARCHAR(20),
                            withdraw            BOOLEAN      NOT NULL DEFAULT FALSE,     -- checked when the form’s “Withdraw” box is ticked :contentReference[oaicite:14]{index=14}
                            status              submission_status NOT NULL DEFAULT 'PENDING',
                            created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

/* Tracks every manual change someone makes to a submission */
CREATE TABLE submission_edit (
                                 id             BIGSERIAL  PRIMARY KEY,
                                 submission_id  BIGINT     NOT NULL REFERENCES submission(id) ON DELETE CASCADE,
                                 field_name     VARCHAR(50) NOT NULL,
                                 old_value      TEXT,
                                 new_value      TEXT,
                                 edited_at      TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

/* ---------- INDEXES FOR FAST LOOK-UPS ---------- */
CREATE INDEX idx_submission_ssn           ON submission (current_ssn);
CREATE INDEX idx_submission_new_ssn       ON submission (new_ssn);
CREATE INDEX idx_submission_spec_year     ON submission (desired_spec_id, desired_year);
CREATE INDEX idx_enrollment_spec_year     ON enrollment  (specialization_id, year);
CREATE INDEX idx_student_prev_ssn         ON student     (prev_ssn);
