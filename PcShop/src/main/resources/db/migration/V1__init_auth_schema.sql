-- V1__init_auth_schema.sql

-- 1) USERS
create table if not exists users (
                                     id            bigint primary key auto_increment,
                                     email         varchar(255) not null unique,
    password_hash varchar(255) not null,
    display_name  varchar(100),
    status        varchar(20)  not null default 'PENDING', -- PENDING, ACTIVE, LOCKED, DISABLED
    failed_logins int          not null default 0,
    last_login_at datetime     null,
    created_at    datetime     not null default current_timestamp,
    updated_at    datetime     not null default current_timestamp on update current_timestamp,
    constraint chk_users_status check (status in ('PENDING','ACTIVE','LOCKED','DISABLED'))
    ) engine=InnoDB;

create index idx_users_email on users(email);

-- 2) ROLES + USER_ROLES
create table if not exists roles (
                                     id   bigint primary key auto_increment,
                                     name varchar(50) not null unique
    ) engine=InnoDB;

create table if not exists user_roles (
                                          user_id bigint not null,
                                          role_id bigint not null,
                                          primary key (user_id, role_id),
    foreign key (user_id) references users(id) on delete cascade,
    foreign key (role_id) references roles(id) on delete cascade
    ) engine=InnoDB;

-- 3) EMAIL VERIFICATION TOKENS
create table if not exists email_verification_tokens (
                                                         id          bigint primary key auto_increment,
                                                         user_id     bigint not null,
                                                         token_hash  varchar(255) not null,
    expires_at  datetime not null,
    used_at     datetime null,
    foreign key (user_id) references users(id) on delete cascade
    ) engine=InnoDB;

create index idx_evt_user on email_verification_tokens(user_id);
create index idx_evt_exp  on email_verification_tokens(expires_at);

-- 4) PASSWORD RESET TOKENS
create table if not exists password_reset_tokens (
                                                     id          bigint primary key auto_increment,
                                                     user_id     bigint not null,
                                                     token_hash  varchar(255) not null,
    expires_at  datetime not null,
    used_at     datetime null,
    foreign key (user_id) references users(id) on delete cascade
    ) engine=InnoDB;

create index idx_prt_user on password_reset_tokens(user_id);
create index idx_prt_exp  on password_reset_tokens(expires_at);

-- 5) REFRESH TOKENS (lưu HASH, không lưu token rõ)
create table if not exists refresh_tokens (
                                              id           bigint primary key auto_increment,
                                              user_id      bigint      not null,
                                              token_hash   varchar(255) not null,
    jti          varchar(64)  not null,   -- id logic của refresh
    device_info  varchar(255),
    ip           varchar(64),
    user_agent   varchar(255),
    created_at   datetime not null default current_timestamp,
    expires_at   datetime not null,
    revoked_at   datetime null,
    replaced_by  varchar(64) null,
    foreign key (user_id) references users(id) on delete cascade
    ) engine=InnoDB;

create index idx_rt_user     on refresh_tokens(user_id);
create index idx_rt_jti      on refresh_tokens(jti);
create index idx_rt_expires  on refresh_tokens(expires_at);
create index idx_rt_revoked  on refresh_tokens(revoked_at);

-- 6) MFA (optional)
create table if not exists user_mfa (
                                        user_id          bigint primary key,
                                        secret_encrypted varchar(255),
    enabled_at       datetime null,
    foreign key (user_id) references users(id) on delete cascade
    ) engine=InnoDB;

create table if not exists mfa_recovery_codes (
                                                  id        bigint primary key auto_increment,
                                                  user_id   bigint not null,
                                                  code_hash varchar(255) not null,
    used_at   datetime null,
    foreign key (user_id) references users(id) on delete cascade
    ) engine=InnoDB;

-- 7) SEED ROLE cơ bản
insert into roles(name) values ('USER') on duplicate key update name=values(name);
insert into roles(name) values ('ADMIN') on duplicate key update name=values(name);
