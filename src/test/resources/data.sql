SET MODE MySQL;

-- 테이블 생성
drop table if exists application_entity cascade;
drop table if exists application_label_entity cascade;
drop table if exists role_entity cascade;
drop table if exists user_entity cascade;
drop table if exists workspace_entity cascade;
drop table if exists domain_entity cascade;
drop table if exists application_env_entity cascade;
drop table if exists application_env_detail_entity cascade;
drop table if exists application_env_matcher_entity cascade;
drop table if exists application_env_label_entity cascade;
drop table if exists volume_entity cascade;
drop table if exists volume_mount_entity cascade;
create table application_entity (external_port integer not null, port integer not null, application_type varchar(255) check (application_type in ('SPRING_BOOT','NEST_JS','MYSQL','MARIA_DB','REDIS')), description varchar(255), failure_reason varchar(255), github_url varchar(255), id binary(16) not null, name varchar(255), status varchar(255) check (status in ('CREATED','PENDING','RUNNING','STOPPED','FAILURE')), version varchar(255), workspace_id binary(16), primary key (id));
create table application_label_entity (application_id binary(16) not null, label varchar(255));
create table role_entity (roles varchar(255) check (roles in ('ROLE_ADMIN','ROLE_DEVELOPER','ROLE_USER')), user_id binary(16) not null);
create table user_entity (email varchar(255), id binary(16) not null, name varchar(255), password varchar(255), status varchar(255) check (status in ('PENDING','CREATED')), primary key (id));
create table workspace_entity (description varchar(255), id binary(16) not null, owner_id binary(16), title varchar(255), primary key (id));
create table domain_entity (id binary(16), name varchar(255), description varchar(255), application_id binary(16), workspace_id binary(16), primary key(id));
create table application_env_entity (id BINARY(16) not null, description varchar(255), name varchar(255), workspace_id binary(16), primary key (id));
create table application_env_detail_entity (id BINARY(16) not null, encryption bit not null, env_key varchar(255), env_value varchar(255), env_id BINARY(16), primary key (id));
create table application_env_matcher_entity (id BINARY(16) not null, application_id BINARY(16), env_id BINARY(16), primary key (id));
create table application_env_label_entity (application_env_id BINARY(16) not null, label varchar(255));
create table volume_entity (id BINARY(16) not null, description varchar(255), name varchar(255), physical_path varchar(255) not null, workspace_id BINARY(16), primary key (id));
create table volume_mount_entity (id BINARY(16) not null, mount_path varchar(255) not null, application_id BINARY(16) not null, volume_id BINARY(16) not null, read_only bit(1) not null, primary key (id));
alter table if exists volume_entity add constraint FKhp1ggnqtveky8po2cwsb45una foreign key (workspace_id) references workspace_entity (id) on delete cascade;
alter table if exists volume_mount_entity add constraint FKr5bb2ev813ioxtylyobgdphel foreign key (application_id) references application_entity (id) on delete cascade;
alter table if exists volume_mount_entity add constraint FKq6dppr5p20mvgjditmpypicey foreign key (volume_id) references volume_entity (id) on delete cascade;
alter table if exists application_env_detail_entity add constraint FKtjqi6ag33hu1vxeg5kgc58ga6 foreign key (env_id) references application_env_entity (id) on delete cascade;
alter table if exists application_env_matcher_entity add constraint FK4ivou7scuc0dg5af4g7epjdj0 foreign key (application_id) references application_entity (id) on delete cascade;
alter table if exists application_env_matcher_entity add constraint FKqfkw6tgy65k4x6syrh6p858ic foreign key (env_id) references application_env_entity (id) on delete cascade;
alter table if exists application_env_entity add constraint FKm22cqdjjl434jyqenpa4nf78p foreign key (workspace_id) references workspace_entity (id);
alter table if exists application_env_label_entity add constraint FKgd5b8upn11w2uh6voe20dm6df foreign key (application_env_id) references application_env_entity (id);
alter table if exists application_entity add constraint FKn9drxkrx2h6wlorxfy00mr45h foreign key (workspace_id) references workspace_entity;
alter table if exists application_label_entity add constraint FKq6iovxq5tdx2i1lwrx34kg0b9 foreign key (application_id) references application_entity;
alter table if exists role_entity add constraint FKrot6fehcor0f3sux5s6kgl0a4 foreign key (user_id) references user_entity;
alter table if exists workspace_entity add constraint FKlfxk1bhw5knckt8vv28xvx5g2 foreign key (owner_id) references user_entity;
alter table if exists domain_entity add constraint FKh7b0t3y0a7x5boh75j8lanww6 foreign key (application_id) references application_entity;
alter table if exists domain_entity add constraint FKgh6jr55192rq9v5byby652dry foreign key (workspace_id) references workspace_entity;

-- 유저 생성
insert into user_entity (email,name,password,status,id) values ('ownerEmail','applicationOwner', '$2a$10$uOSckwMEixpnBwRVp6ZoeOG9hQPbxXp.I0UK6xTok8tKK7G5f6A46', 'CREATED',  X'923a6407a5f84e1ebffd0621910ddfc8');
insert into role_entity (user_id,roles) values (X'923a6407a5f84e1ebffd0621910ddfc8', 'ROLE_USER');
insert into user_entity (email,name,password,status,id) values ('testEmail','applicationOwner', '$2a$10$uOSckwMEixpnBwRVp6ZoeOG9hQPbxXp.I0UK6xTok8tKK7G5f6A46', 'CREATED', X'1e1973eb3fb947ac9342c16cd63ffc6f');
insert into role_entity (user_id,roles) values (X'1e1973eb3fb947ac9342c16cd63ffc6f', 'ROLE_USER');

-- 워크스페이스 생성
insert into workspace_entity (description,owner_id,title,id) values ('testDescription', X'923a6407a5f84e1ebffd0621910ddfc8', 'testTitle', X'd57b42f55cc4440b8dceb4fc2e372eff');

-- 애플리케이션 생성
insert into application_entity (application_type,description,external_port,failure_reason,github_url,name,port,status,version,workspace_id,id) values ('SPRING_BOOT','testDescription', 8080, NULL, 'testUrl', 'testName', 8080, 'STOPPED','17', X'd57b42f55cc4440b8dceb4fc2e372eff', X'2fb0f3158272422f8e9fc4f765c022b2');