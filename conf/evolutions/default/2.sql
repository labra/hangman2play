# Database schema for words
 
# --- !Ups


CREATE SEQUENCE words_id_seq start with 1000;

CREATE TABLE words (
    id 				bigint NOT NULL DEFAULT nextval('words_id_seq') not null,
    languageId 		bigint not null,
    word 			varchar(256) not null,
    constraint 		pk_word primary key (id)
);


alter table words add constraint fk_language_1 foreign key (languageId) references language (id) on delete restrict on update restrict;

create index ix_language_iri_1 on words (languageId);
 
# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

DROP TABLE if exists words;

DROP SEQUENCE if exists words_id_seq;