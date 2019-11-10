DROP TABLE IF EXISTS SAMPLE_TABLE;
CREATE TABLE SAMPLE_TABLE(
user_no INT PRIMARY KEY
, user_id VARCHAR(255)
, mobile_no VARCHAR(255)
, created_at timestamp
, modified_at timestamp
);
INSERT INTO SAMPLE_TABLE VALUES(1, 'test1',  '010-0000-0000',now(), now());
INSERT INTO SAMPLE_TABLE VALUES(2, 'test2',  '010-2222-0000',now(), now());
INSERT INTO SAMPLE_TABLE VALUES(3, 'test3',  '010-0000-3333',now(), now());
-- SELECT * FROM code_group ORDER BY ID;*/
