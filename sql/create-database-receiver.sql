--Setup database
DROP DATABASE IF EXISTS receiver;
CREATE DATABASE receiver;
\c receiver;


--DROP TABLE IF EXISTS public.subscriber_blocking;

--CREATE TABLE IF NOT EXISTS public.subscriber_blocking (
     --id BIGINT NOT NULL,
     --blocked BOOLEAN NOT NULL,
     --changed TIMESTAMP
     --PRIMARY KEY(id)
--);