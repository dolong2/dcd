-- 테이블 생성
drop table if exists application_entity cascade
drop table if exists application_env_entity cascade
drop table if exists application_label_entity cascade
drop table if exists global_env_entity cascade
drop table if exists role_entity cascade
drop table if exists user_entity cascade
drop table if exists workspace_entity cascade
create table application_entity (external_port integer not null, port integer not null, application_type varchar(255) check (application_type in ('SPRING_BOOT','NEST_JS','MYSQL','MARIA_DB','REDIS')), description varchar(255), failure_reason varchar(255), github_url varchar(255), id varchar(255) not null, name varchar(255), status varchar(255) check (status in ('CREATED','PENDING','RUNNING','STOPPED','FAILURE')), version varchar(255), workspace_id varchar(255), primary key (id))
create table application_env_entity (application_id varchar(255) not null, env_key varchar(255) not null, env_value varchar(255), primary key (application_id, env_key))
create table application_label_entity (application_id varchar(255) not null, label varchar(255))
create table global_env_entity (env_key varchar(255) not null, env_value varchar(255), workspace_id varchar(255) not null, primary key (env_key, workspace_id))
create table role_entity (roles varchar(255) check (roles in ('ROLE_ADMIN','ROLE_DEVELOPER','ROLE_USER')), user_id varchar(255) not null)
create table user_entity (email varchar(255), id varchar(255) not null, name varchar(255), password varchar(255), status varchar(255) check (status in ('PENDING','CREATED')), primary key (id))
create table workspace_entity (description varchar(255), id varchar(255) not null, owner_id varchar(255), title varchar(255), primary key (id))
alter table if exists application_entity add constraint FKn9drxkrx2h6wlorxfy00mr45h foreign key (workspace_id) references workspace_entity
alter table if exists application_env_entity add constraint FKb8anxni720qr9mk9neafwm7v foreign key (application_id) references application_entity
alter table if exists application_label_entity add constraint FKq6iovxq5tdx2i1lwrx34kg0b9 foreign key (application_id) references application_entity
alter table if exists global_env_entity add constraint FK694b15y5r7k158yn9c2rnftte foreign key (workspace_id) references workspace_entity
alter table if exists role_entity add constraint FKrot6fehcor0f3sux5s6kgl0a4 foreign key (user_id) references user_entity
alter table if exists workspace_entity add constraint FKlfxk1bhw5knckt8vv28xvx5g2 foreign key (owner_id) references user_entity