
ALTER TABLE destination DROP close;
ALTER TABLE destination ADD state INT NOT NULL DEFAULT 0;
ALTER TABLE destination ADD message TEXT(65535);
ALTER TABLE destination ADD url VARCHAR(255);
