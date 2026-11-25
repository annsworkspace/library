-- This adds an optional place for each book to record its cover image path.

ALTER TABLE books
ADD COLUMN cover_path VARCHAR(500);