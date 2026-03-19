create table job_details
(
    id               int                                    not null comment '对应 jobs 表的 id'
        primary key,
    full_description text                                   not null comment '详细职责与要求',
    requirement      text                                   null comment '任职资格',
    location         varchar(100) default '远程/不限'       null comment '工作地点',
    update_time      timestamp    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
);

create table jobs
(
    id          int auto_increment
        primary key,
    title       varchar(100)                        not null comment '职位名称',
    company     varchar(100)                        not null comment '公司名称',
    salary      varchar(50)                         null comment '薪资范围',
    description text                                null comment '职位描述',
    created_at  timestamp default CURRENT_TIMESTAMP null
);

create table users
(
    id           bigint auto_increment
        primary key,
    openid       varchar(128)                        not null comment '微信唯一标识',
    email_prefix varchar(64)                         not null comment 'CityU邮箱前缀',
    full_email   varchar(128)                        not null comment '完整邮箱',
    created_at   timestamp default CURRENT_TIMESTAMP null,
    constraint openid
        unique (openid)
);

create table resumes
(
    user_id     bigint                              not null comment '对应 users 表的 id'
        primary key,
    name        varchar(50)                         null comment '姓名',
    edu         varchar(100)                        null comment '院校专业',
    skills      text                                null comment '核心技能',
    experience  text                                null comment '项目经历',
    update_time timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint fk_resumes_user
        foreign key (user_id) references users (id)
            on delete cascade
);

