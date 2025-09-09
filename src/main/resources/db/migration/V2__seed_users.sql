INSERT INTO users (id, name, email, roles)
VALUES (uuid_generate_v4(), 'Alice', 'alice@example.com', ARRAY ['ADMIN','USER']),
       (uuid_generate_v4(), 'Bob', 'bob@example.com', ARRAY ['USER']),
       (uuid_generate_v4(), 'Charlie', 'charlie@example.com', ARRAY ['USER'])
ON CONFLICT (email) DO NOTHING;


