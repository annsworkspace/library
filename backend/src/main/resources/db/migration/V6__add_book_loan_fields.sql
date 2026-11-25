ALTER TABLE books
    ADD COLUMN loaned_to VARCHAR(160),
    ADD COLUMN loan_date DATE,
    ADD COLUMN returned_date DATE;

ALTER TABLE books
    ADD CONSTRAINT chk_books_loan_fields
    CHECK (
        (loaned_to IS NULL AND loan_date IS NULL AND returned_date IS NULL)
        OR
        (
            loaned_to IS NOT NULL
            AND loan_date IS NOT NULL
            AND (returned_date IS NULL OR returned_date >= loan_date)
        )
    );