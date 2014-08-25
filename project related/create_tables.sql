-- ----------------------------------
--  CREATING TABLES AND PRIMARY KEYS
-- ----------------------------------

CREATE SCHEMA ImageDB;
USE ImageDB;

CREATE TABLE Image
   (imageid INT UNIQUE AUTO_INCREMENT,
	name VARCHAR (255),
	added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	user INT,
	CONSTRAINT pk_image PRIMARY KEY(name, user)) ENGINE=InnoDB;

CREATE TABLE Tag
   (tagid INT UNIQUE AUTO_INCREMENT,
	name VARCHAR(64),
	type INT,
	info TEXT,
	user INT,
	CONSTRAINT pk_tag PRIMARY KEY(name, user)) ENGINE=InnoDB;

CREATE TABLE TagType
   (typeid INT UNIQUE AUTO_INCREMENT,
	name VARCHAR(64),
	user INT,
	CONSTRAINT pk_type PRIMARY KEY(name, user)) ENGINE=InnoDB;

CREATE TABLE Tagged
   (image INT,
	tag INT,
	CONSTRAINT pk_tagged PRIMARY KEY(image, tag)) ENGINE=InnoDB;

CREATE TABLE User
   (userid INT UNIQUE AUTO_INCREMENT,
	name VARCHAR(64),
	password VARCHAR(64),
	CONSTRAINT pk_users PRIMARY KEY(name)) ENGINE=InnoDB;

-- -------------------------------
--  ADDING FOREIGN KEYS TO TABLES
-- -------------------------------

ALTER TABLE Image ADD CONSTRAINT fk_image_user FOREIGN KEY (user)
	REFERENCES User (userid);

ALTER TABLE Tag ADD CONSTRAINT fk_tag_user FOREIGN KEY (user)
	REFERENCES User (userid);
ALTER TABLE Tag ADD CONSTRAINT fk_type FOREIGN KEY (type) 
	REFERENCES TagType (typeid);

ALTER TABLE TagType ADD CONSTRAINT fk_tagtype_user FOREIGN KEY (user)
	REFERENCES User (userid);

ALTER TABLE Tagged ADD CONSTRAINT fk_image FOREIGN KEY (image)
	REFERENCES Image (imageid);
ALTER TABLE Tagged ADD CONSTRAINT fk_tag FOREIGN KEY (tag)
	REFERENCES Tag (tagid);