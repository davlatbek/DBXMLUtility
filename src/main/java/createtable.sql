DROP TABLE IF EXISTS dbtoxml.department;
CREATE TABLE department(
    ID          INT           NOT NULL PRIMARY KEY AUTO_INCREMENT,
    DepCode     VARCHAR(20)   NOT NULL,
    DepJob      VARCHAR(100)  NOT NULL,
    Description VARCHAR(255)  DEFAULT 'description text',
    UNIQUE (DepCode, DepJob)
);
