--- V1__init.sql
CREATE TABLE demo
(
     id         UUID NOT NULL,
     active     BOOLEAN DEFAULT TRUE,
     testdata   VARCHAR(128) NOT NULL,
     created_date   TIMESTAMP NOT NULL DEFAULT now(),
     created_by     VARCHAR(64) NOT NULL,
     updated_date   TIMESTAMP NOT NULL DEFAULT now(),
     updated_by     VARCHAR(64) NOT NULL,
     PRIMARY KEY(id),
     UNIQUE(testdata)
);

