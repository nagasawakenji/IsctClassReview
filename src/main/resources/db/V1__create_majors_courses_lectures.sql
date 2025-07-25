CREATE TABLE majors (
    id SMALLINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    url TEXT NOT NULL
);

CREATE TABLE courses (
    id SMALLINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    majors_id SMALLINT NOT NULL REFERENCES majors(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    url TEXT NOT NULL
);

CREATE TABLE lectures (
    id SMALLINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    courses_id SMALLINT NOT NULL REFERENCES courses(id),
    code VARCHAR(20) NOT NULL,
    title VARCHAR(200) NOT NULL,
    opening_period VARCHAR(50),
    url TEXT
);