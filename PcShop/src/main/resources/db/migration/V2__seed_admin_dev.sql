-- V2__seed_admin_dev.sql
-- Bcrypt hash mk: "Admin@123456" (ví dụ) — thay bằng hash thật của bạn
-- Có thể tạo hash tạm bằng BCryptPasswordEncoder trong một @Test hoặc online generator đáng tin.

insert into users(email, password_hash, display_name, status, failed_logins)
values ('admin@example.com',
        '$2a$12$JmL6k1o7ZtDgkzV2a5y1oO5v1m0A2qI8wQn3F4vV4J7UO5m6j3y6K',
        'Admin', 'ACTIVE', 0)
    on duplicate key update email=values(email);

-- gán ROLE_ADMIN
insert into user_roles(user_id, role_id)
select u.id, r.id from users u, roles r
where u.email='admin@example.com' and r.name='ADMIN'
    on duplicate key update user_id=user_id;
