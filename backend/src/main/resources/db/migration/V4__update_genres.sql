INSERT INTO categories (name) VALUES
    ('Sci-Fi'),
    ('Dystopia')
ON CONFLICT (name) DO NOTHING;

DELETE FROM categories category
WHERE category.name IN ('Tecnologia', 'Biografia')
  AND NOT EXISTS (
      SELECT 1
      FROM books book
      WHERE book.category_id = category.id
  );
