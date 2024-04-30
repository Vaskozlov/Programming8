DROP TABLE IF EXISTS ORGANIZATIONS;
DROP TABLE IF EXISTS USERS;
DROP TABLE IF EXISTS ORGANIZATION_TYPES;
DROP TABLE IF EXISTS ADDRESS;
DROP TABLE IF EXISTS LOCATION;
DROP TABLE IF EXISTS COORDINATES;

CREATE TABLE IF NOT EXISTS COORDINATES
(
    ID INTEGER PRIMARY KEY,
    X  BIGINT NOT NULL,
    Y  BIGINT CHECK ( Y < 464 )
);

CREATE TABLE IF NOT EXISTS LOCATION
(
    ID   INTEGER PRIMARY KEY,
    X    DOUBLE PRECISION,
    Y    REAL,
    Z    BIGINT NOT NULL,
    NAME VARCHAR(1024) CHECK ( LENGTH(NAME) <= 933 )
);

CREATE TABLE IF NOT EXISTS ADDRESS
(
    ID          INTEGER PRIMARY KEY,
    ZIP_CODE    VARCHAR(128) CHECK ( LENGTH(ZIP_CODE) >= 3 ),
    LOCATION_ID INTEGER REFERENCES LOCATION ON DELETE CASCADE NOT NULL
);

CREATE TABLE IF NOT EXISTS ORGANIZATION_TYPES
(
    ID   INTEGER PRIMARY KEY,
    NAME VARCHAR(512)
);

CREATE TABLE IF NOT EXISTS USERS
(
    ID       SERIAL PRIMARY KEY,
    LOGIN    VARCHAR(512) NOT NULL CHECK ( LENGTH(LOGIN) > 0 ) UNIQUE,
    PASSWORD VARCHAR(512) NOT NULL CHECK ( LENGTH(PASSWORD) > 0 )
);

-- ALTER TABLE USERS ADD UNIQUE (LOGIN);
-- ALTER TABLE USERS ALTER COLUMN PASSWORD TYPE VARCHAR(512);

CREATE TABLE IF NOT EXISTS ORGANIZATIONS
(
    ID                   SERIAL PRIMARY KEY,
    NAME                 VARCHAR(1024)                                    NOT NULL CHECK ( LENGTH(NAME) > 0 ),
    COORDINATES_ID       INTEGER REFERENCES COORDINATES ON DELETE CASCADE NOT NULL,
    CREATION_TIME        DATE                                             NOT NULL,
    ANNUAL_TURNOVER      REAL CHECK ( ANNUAL_TURNOVER >= 0.0 ),
    FULL_NAME            VARCHAR(1024)                                    NOT NULL CHECK ( LENGTH(FULL_NAME) <= 573 ),
    EMPLOYEES_COUNT      INTEGER CHECK ( EMPLOYEES_COUNT > 0 ),
    ORGANIZATION_TYPE_ID INTEGER REFERENCES ORGANIZATION_TYPES,
    POSTAL_ADDRESS_ID    INTEGER REFERENCES ADDRESS ON DELETE CASCADE,
    CREATOR_ID           INTEGER REFERENCES USERS,
    CONSTRAINT UNIQUE_ORGANIZATION_CHECK UNIQUE (FULL_NAME, ORGANIZATION_TYPE_ID)
);

-- ALTER TABLE ORGANIZATIONS
--     ADD CONSTRAINT UNIQUE_ORGANIZATION_CHECK UNIQUE (FULL_NAME, ORGANIZATION_TYPE_ID);

INSERT INTO ORGANIZATION_TYPES
VALUES (0, 'COMMERCIAL'),
       (1, 'PUBLIC'),
       (2, 'PRIVATE_LIMITED_COMPANY'),
       (3, 'OPEN_JOINT_STOCK_COMPANY');
