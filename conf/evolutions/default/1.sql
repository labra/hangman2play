# Database schema for languages
 
# --- !Ups


CREATE SEQUENCE language_id_seq start with 1000;

CREATE TABLE language (
    id 				bigint NOT NULL DEFAULT nextval('language_id_seq'),
    code 			varchar(20) not null,
    name 			varchar(200) not null,
    alphabet		varchar(500) not null,
    constraint 		pk_course primary key (id)
);


# --- !Downs

DROP TABLE if exists language;

DROP SEQUENCE if exists language_id_seq;
