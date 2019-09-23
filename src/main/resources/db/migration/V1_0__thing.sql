CREATE TABLE "bloodpressures"
(
    "sessionid" VARCHAR(36)      NOT NULL,
    "time"      TIMESTAMP        NOT NULL,
    "sys"       DOUBLE PRECISION NOT NULL,
    "dia"       DOUBLE PRECISION NOT NULL,
    PRIMARY KEY ("time", "sessionid")
);

CREATE TABLE "height"
(
    "sessionid" VARCHAR(36)      NOT NULL,
    "time"      TIMESTAMP        NOT NULL,
    "height"    DOUBLE PRECISION NOT NULL,
    PRIMARY KEY ("time", "sessionid")
);

CREATE TABLE "weight"
(
    "sessionid" VARCHAR(36)      NOT NULL,
    "time"      TIMESTAMP        NOT NULL,
    "weight"    DOUBLE PRECISION NOT NULL,
    PRIMARY KEY ("time", "sessionid")
);


CREATE TABLE "tmp_lastresult"
(
    "citizen"    VARCHAR(30)  NOT NULL,
    "reflink"    VARCHAR(16)  NOT NULL,
    "value"      VARCHAR(255) NOT NULL,
    "updatedate" TIMESTAMP    NULL,
    PRIMARY KEY ("citizen", "reflink")
);

CREATE TABLE "register"
(
    "sessionid"      VARCHAR(36)  NOT NULL,
    "time"           TIMESTAMP    NULL,
    "deviceid"       VARCHAR(36)  NOT NULL,
    "citizenid"      VARCHAR(36)  NULL DEFAULT NULL,
    "citizenidinput" VARCHAR(10)  NULL DEFAULT NULL,
    "birthdate"      DATE         NULL DEFAULT NULL,
    "name"           VARCHAR(100) NULL DEFAULT NULL,
    "sex"            VARCHAR(10)  NULL DEFAULT NULL,
    PRIMARY KEY ("time", "sessionid")
);
