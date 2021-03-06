CREATE TABLE CWF_DOMAIN (
	ID VARCHAR(20) PRIMARY KEY,
	NAME VARCHAR(255),
	ATTRIBUTES VARCHAR(255) DEFAULT '');

INSERT INTO CWF_DOMAIN (ID, NAME, ATTRIBUTES) VALUES
	('1', 'General Medicine Clinic', ''),
	('2', 'Emergency Room', ''),
	('3', 'Test Hospital', 'default=true');

CREATE TABLE CWF_USER (
	ID VARCHAR(20) PRIMARY KEY,
	USERNAME VARCHAR(40),
	PASSWORD VARCHAR(40),
	NAME VARCHAR(255),
	DOMAIN_ID VARCHAR(20),
	AUTHORITIES VARCHAR(255) DEFAULT '');

INSERT INTO CWF_USER (ID, USERNAME, PASSWORD, NAME, DOMAIN_ID, AUTHORITIES) VALUES
	('1', 'DOCTOR123', 'DOCTOR321$', 'Doctor, Test', '1', 'PRIV_PATIENT_SELECT'),
	('2', 'USER123', 'USER321$', 'User, Test', '*', 'PRIV_DEBUG,PRIV_CAREWEB_DESIGNER,PRIV_PATIENT_SELECT');
	
