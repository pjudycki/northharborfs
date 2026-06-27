CREATE TABLE retrieval (
    id bigint NOT NULL AUTO_INCREMENT,
    success boolean,
    historical boolean,
    timestamp bigint,
    date Date,
    base VARCHAR(50),
PRIMARY KEY (id)
);

CREATE TABLE currency (
    id bigint NOT NULL AUTO_INCREMENT,
    currency_name varchar(3),
    rate_of_exchange decimal,
    retrieval_id bigint,
    PRIMARY KEY (id),
FOREIGN KEY (retrieval_id) REFERENCES retrieval(id)
);