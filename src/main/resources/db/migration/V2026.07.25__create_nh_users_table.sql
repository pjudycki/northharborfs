CREATE TABLE nh_users(
	id BIGINT NOT NULL AUTO_INCREMENT,
	username VARCHAR(50) NOT NULL,
	email VARCHAR(254) NOT NULL,
	password_hash VARCHAR(255) NOT NULL,
	enabled BOOLEAN NOT NULL DEFAULT TRUE,
	
	CONSTRAINT pk_nh_users
		PRIMARY KEY (id),
	CONSTRAINT unique_nh_users_username
		UNIQUE (username),
		
	CONSTRAINT unique_nh_users_email
		UNIQUE (email)
)

