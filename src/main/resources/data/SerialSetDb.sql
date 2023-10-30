-- Create sequence for SerialSet
CREATE SEQUENCE serial_set_id_seq START 1;

-- Create SerialSet table with sequence and indexes
CREATE TABLE SerialSet (
    id INT DEFAULT nextval('serial_set_id_seq') PRIMARY KEY,
    name VARCHAR(255) UNIQUE,
    quantity INT,
    createdDate TIMESTAMP,
    serialLength INT,
    configuration BOOLEAN,
    number BOOLEAN,
    lowerCase BOOLEAN,
    upperCase BOOLEAN,
    exclusions VARCHAR(255)
);

-- Create index for SerialSet name
CREATE INDEX idx_serialset_name ON SerialSet (name);

-- Create sequence for SerialNumber
CREATE SEQUENCE serial_number_id_seq START 1;

-- Create SerialNumber table with sequence and indexes
CREATE TABLE SerialNumber (
    id INT DEFAULT nextval('serial_number_id_seq') PRIMARY KEY,
    value VARCHAR(255),
    createdDate TIMESTAMP,
    serial_set_id INT REFERENCES SerialSet(id),
    FOREIGN KEY (serial_set_id) REFERENCES SerialSet(id)
);

-- Create index for SerialNumber value
CREATE INDEX idx_serialnumber_value ON SerialNumber (value);
