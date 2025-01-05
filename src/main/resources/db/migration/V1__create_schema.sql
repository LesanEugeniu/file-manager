# CREATE SCHEMA file_manager;

CREATE TABLE file_manager.file_data
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    type       VARCHAR(255) NOT NULL,
    file_path  VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP    NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    deleted    BOOLEAN      NOT NULL DEFAULT FALSE
);

DELIMITER $$

CREATE TRIGGER ModDate_file_data_update
    BEFORE UPDATE
    ON file_manager.file_data
    FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END$$

DELIMITER ;