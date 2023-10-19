CREATE TABLE analysis_data (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    update JSONB
);

CREATE TABLE binary_data (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    bytes BYTEA
);

CREATE TABLE bot_users (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    email CHARACTER VARYING(255),
    first_login_date TIMESTAMP(6) WITH TIME ZONE,
    first_name CHARACTER VARYING(255),
    last_name CHARACTER VARYING(255),
    state CHARACTER VARYING(255),
    tg_user_id BIGINT,
    username CHARACTER VARYING(255)
);

CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    file_id CHARACTER VARYING(255),
    file_name CHARACTER VARYING(255),
    file_size BIGINT,
    mime_type CHARACTER VARYING(255),
    binary_data_id BIGINT,
    FOREIGN KEY (binary_data_id) REFERENCES binary_data (id) ON DELETE CASCADE
);

CREATE TABLE images (
   id BIGSERIAL PRIMARY KEY NOT NULL,
   file_id CHARACTER VARYING(255),
   file_size INTEGER,
   binary_data_id BIGINT,
   FOREIGN KEY (binary_data_id) REFERENCES binary_data (id) ON DELETE CASCADE
);