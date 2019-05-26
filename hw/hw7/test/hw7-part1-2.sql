DROP TABLE IF EXISTS InsuranceCo;
DROP TABLE IF EXISTS Person;
DROP TABLE IF EXISTS Driver;
DROP TABLE IF EXISTS NonProfessionalDriver;
DROP TABLE IF EXISTS ProfessionalDriver;
DROP TABLE IF EXISTS Vehicle;
DROP TABLE IF EXISTS Car;
DROP TABLE IF EXISTS Truck;
DROP TABLE IF EXISTS Drives;

CREATE TABLE InsuranceCo(
    iName varchar(30) PRIMARY KEY,
    phone int
);

CREATE TABLE Person(
    SSN NUMBER PRIMARY KEY,
    name varchar(30)
);  

CREATE TABLE Driver(
    SSN NUMBER PRIMARY KEY
        CHECK(licensePlate IN (select Person.SSN 
                               from Person)),
    dirverID NUMBER
);

CREATE TABLE NonProfessionalDriver(
    SSN NUMBER PRIMARY KEY
        CHECK(licensePlate IN (select Driver.SSN 
                               from Driver)),
);

CREATE TABLE ProfessionalDriver(
    SSN NUMBER PRIMARY KEY
        CHECK(licensePlate IN (select Driver.SSN 
                               from Driver)),
    medicalHistory varchar(100)
);

CREATE TABLE Vehicle(
    licensePlate varchar(9) PRIMARY KEY,
    year int,
    maxLiability REAL,
    iName varchar(30) REFERENCES InsuranceCo(name),
    pSSN NUMBER REFERENCES Person(SSN)
);

CREATE TABLE Car(
    licensePlate varchar(9) PRIMARY KEY
        CHECK(licensePlate IN (select Vehicle.licensePlate 
                               from Vehicle)),
    make varchar(30)
);

CREATE TABLE Truck(
    licensePlate varchar(9) PRIMARY KEY
        CHECK(licensePlate IN (select Vehicle.licensePlate 
                               from Vehicle)),
    capacity int,
    pdSSN NUMBER REFERENCES ProfessionalDriver(SSN)
);

CREATE TABLE Drives(
    licensePlate varchar(9) REFERENCES Car(licensePlate),
    npdSSN NUMBER REFERENCES NonProfessionalDriver(SSN),
    PRIMARY KEY(licensePlate, npdSSN)
);

/* relationship "insures" is represented by joining Vehicle 
** and InsuranceCo using foreign key iName in Vehicle since 
** it's a many to one relationship so that each Vehicle will 
** connect to at most one InsuranceCo and exactly one maxliability 
** in the relationship, thus maxliability is stored in Vehicle
*/

/* relationships "drives" and "operates" are different because 
** Drives is a many to many relationship, but Operates is a
** many to one relationship. Therefore, Truck stored a foreign 
** key pdSSN to join with ProfessionalDriver, and Drives become
** a table storing unique pairs of licensePlate and npdSSN.
*/