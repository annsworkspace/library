UPDATE app_users
SET role = 'ADMIN'
WHERE id = (
    SELECT MIN(id)
    FROM app_users
);