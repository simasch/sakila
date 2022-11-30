create table application_user
(
    id              uuid primary key not null,
    hashed_password varchar,
    name            varchar,
    profile_picture oid,
    username        varchar
);

create table user_roles
(
    user_id uuid not null,
    roles   varchar,

    foreign key (user_id) references application_user (id)
);

