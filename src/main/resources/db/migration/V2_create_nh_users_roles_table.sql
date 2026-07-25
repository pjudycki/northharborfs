CREATE TABLE nh_users_roles (
	user_id BIGINT NOT NULL,
	role VARCHAR(30) NOT NULL,
	
	CONSTRAINT pk_nh_users_roles
		PRIMARY KEY (user_id, role),
	
	CONSTRAINT fk_nh_users_roles_user
		FOREIGN KEY (user_id)
		REFERENCES nh_users (id)
		ON DELETE CASCADE
)
