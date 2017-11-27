# --- Initial

# --- !Ups

create table "payment" ("id" text NOT NULL PRIMARY KEY,"type" text NOT NULL,"version" BIGINT NOT NULL,"organisation_id" text NOT NULL,"attributes" text NOT NULL);

# --- !Downs

drop table "payment";
