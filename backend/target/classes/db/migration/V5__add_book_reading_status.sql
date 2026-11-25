ALTER TABLE books
    ADD COLUMN reading_status VARCHAR(20) NOT NULL DEFAULT 'WANT_TO_READ';

ALTER TABLE books
    ADD CONSTRAINT chk_books_reading_status
    CHECK (reading_status IN ('WANT_TO_READ', 'READING', 'READ'));