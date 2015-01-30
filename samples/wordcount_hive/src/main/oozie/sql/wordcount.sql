use ${DB_NAME};

INSERT INTO TABLE result SELECT word, count(*) as number FROM wordcount GROUP BY word