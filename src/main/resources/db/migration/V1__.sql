ALTER TABLE notifications
DROP
CONSTRAINT fk9y21adhxn0ayjhfocscqox7bh;

ALTER TABLE resumes
    ADD CONSTRAINT pk_resumes PRIMARY KEY (id);

DROP TABLE admins CASCADE;

ALTER TABLE applications
DROP
COLUMN applicationid;

ALTER TABLE messages
DROP
COLUMN message_id;

ALTER TABLE notifications
DROP
COLUMN notificationid;

ALTER TABLE notifications
DROP
COLUMN userid;

ALTER TABLE notifications
    ALTER COLUMN created_date DROP NOT NULL;

ALTER TABLE vacancies
    ALTER COLUMN description DROP NOT NULL;

ALTER TABLE resumes
    ALTER COLUMN education DROP NOT NULL;

ALTER TABLE users
    ALTER COLUMN email DROP NOT NULL;

ALTER TABLE resumes
    ALTER COLUMN experience DROP NOT NULL;

ALTER TABLE users
    ALTER COLUMN lastname DROP NOT NULL;

ALTER TABLE vacancies
    ALTER COLUMN location DROP NOT NULL;

ALTER TABLE notifications
    ALTER COLUMN message DROP NOT NULL;

ALTER TABLE users
    ALTER COLUMN name DROP NOT NULL;

ALTER TABLE users
    ALTER COLUMN password DROP NOT NULL;

ALTER TABLE resumes
    ALTER COLUMN skills DROP NOT NULL;

ALTER TABLE vacancies
    ALTER COLUMN title DROP NOT NULL;

ALTER TABLE user_types
    ALTER COLUMN type DROP NOT NULL;

ALTER TABLE applications
    ADD CONSTRAINT pk_applications PRIMARY KEY (id);

ALTER TABLE messages
    ADD CONSTRAINT pk_messages PRIMARY KEY (id);

ALTER TABLE notifications
    ADD CONSTRAINT pk_notifications PRIMARY KEY (id);