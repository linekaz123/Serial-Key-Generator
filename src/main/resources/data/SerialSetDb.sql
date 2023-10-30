-- Create sequence for SerialSet
CREATE SEQUENCE serial_set_id_seq START 1;

-- Create SerialSet table with sequence and indexes
CREATE TABLE serial_set (
    id INT DEFAULT nextval('serial_set_id_seq') PRIMARY KEY,
    name VARCHAR(255) UNIQUE,
    quantity INT,
    created_date TIMESTAMP,
    serial_length INT,
    configuration BOOLEAN,
    number BOOLEAN,
    lower_case BOOLEAN,
    upper_case BOOLEAN,
    exclusions VARCHAR(255)
);

-- Create index for SerialSet name
CREATE INDEX idx_serialset_name ON serial_set (name);

-- Create sequence for SerialNumber
CREATE SEQUENCE serial_number_id_seq START 1;

-- Create SerialNumber table with sequence and indexes
CREATE TABLE serial_number (
    id INT DEFAULT nextval('serial_number_id_seq') PRIMARY KEY,
    value VARCHAR(255),
    created_date TIMESTAMP,
    serial_set_id INT REFERENCES serial_set(id),
    FOREIGN KEY (serial_set_id) REFERENCES serial_set(id)
);

-- Create index for SerialNumber value
CREATE INDEX idx_serialnumber_value ON serial_number (value);
