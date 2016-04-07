
CREATE TABLE destination(
        id BIGINT NOT NULL,
        `name` VARCHAR(256) NOT NULL,
        money INT,
        `close` BOOLEAN NOT NULL,
        created BIGINT NOT NULL,
        PRIMARY KEY(id)
) ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4;

CREATE TABLE `session`(
        id BIGINT NOT NULL,
        created BIGINT NOT NULL,
        PRIMARY KEY(id)
) ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4;

CREATE TABLE vote(
        id BIGINT NOT NULL,
        dest_id BIGINT NOT NULL,
        session_id BIGINT NOT NULL,
        ip BIGINT NOT NULL,
        created BIGINT NOT NULL,
        PRIMARY KEY(id),
        INDEX (dest_id) USING HASH,
        INDEX (ip, created),
        INDEX (session_id, created)
) ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4;
