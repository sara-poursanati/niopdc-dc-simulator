CREATE TABLE "RELEASE_INFO"
(	"RELEASE_INFO" VARCHAR2(2) NOT NULL,
     "VER_NAME" VARCHAR2(30),
     "VER" VARCHAR2(10) NOT NULL,
     "RELEASE_TIME" DATE,
     "ACTIVE_TIME" DATE,
     "STATE" VARCHAR2(20),
     "OPERATOR_ID" VARCHAR2(20),
     "OPTIME" DATE,
     CONSTRAINT "PK_RELEASE_INFO" PRIMARY KEY ("RELEASE_INFO", "VER")
);

CREATE TABLE "FUEL_TYPE_INFO"
   (	"FUEL_TYPE" VARCHAR2(2) NOT NULL,
	"FUEL_TYPE_NAME" VARCHAR2(40) NOT NULL,
	"P" NUMBER(10,0) NOT NULL,
	"P1" NUMBER(10,0) NOT NULL,
	"P2" NUMBER(10,0) NOT NULL,
	"P3" NUMBER(10,0) NOT NULL ,
	 CONSTRAINT "PK_FUEL_TYPE_INFO" PRIMARY KEY ("FUEL_TYPE")
  );

CREATE TABLE "HISTORY_FUEL_TYPE"
   (	"FUEL_TYPE" VARCHAR2(2) NOT NULL,
	"FUEL_TYPE_NAME" VARCHAR2(40) NOT NULL,
	"P" NUMBER(10,0) NOT NULL,
	"P1" NUMBER(10,0) NOT NULL,
	"P2" NUMBER(10,0) NOT NULL,
	"P3" NUMBER(10,0) NOT NULL,
	"RELEASE_TIME" DATE,
	"ACTIVE_TIME" DATE,
	"VER" VARCHAR2(10) NOT NULL,
	 CONSTRAINT "PK_HISTORY_FUEL_TYPE" PRIMARY KEY ("FUEL_TYPE", "VER")
    );

-- New Tables
CREATE TABLE "FUEL"
(	"ID" VARCHAR2(2) NOT NULL,
     "NAME" VARCHAR2(40) NOT NULL,
     CONSTRAINT "PK_FUEL" PRIMARY KEY ("ID")
);

CREATE TABLE "INCB_LIST"
(	"CARD_ID" VARCHAR2(10) NOT NULL,
     CONSTRAINT "PK_INCB_LIST" PRIMARY KEY ("CARD_ID")
);
