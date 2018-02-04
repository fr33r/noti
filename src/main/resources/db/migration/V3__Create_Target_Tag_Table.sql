CREATE TABLE `TARGET_TAG`
(
	`TARGET_UUID`	VARCHAR(128)	NOT NULL,
	`TAG_UUID`		VARCHAR(128)	NOT NULL,

	PRIMARY KEY(`TARGET_UUID`, `TAG_UUID`),
	CONSTRAINT FOREIGN KEY `TARGET_TAG_TARGET_UUID_FK` (`TARGET_UUID`) REFERENCES `TARGET` (`UUID`),
	CONSTRAINT FOREIGN KEY `TARGET_TAG_TAG_UUID_FK` (`TAG_UUID`) REFERENCES `TAG` (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
