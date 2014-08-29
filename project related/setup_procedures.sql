USE ImageDB;
DELIMITER $$

CREATE PROCEDURE RegisterUser(IN user VARCHAR(64), IN pass VARCHAR(64), IN newsalt VARCHAR(64))
	BEGIN
		START TRANSACTION;
		
		SELECT * FROM User FOR UPDATE;
		INSERT INTO User (name, password, salt) 
			VALUES (user, pass, newsalt);
		
		COMMIT;
	END$$

