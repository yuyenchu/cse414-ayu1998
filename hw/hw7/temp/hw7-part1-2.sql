CREATE TABLE InsuranceCo(
    name varchar(30) PRIMARY KEY,
    phone int
);

CREATE TABLE Person(
    SSN int PRIMARY KEY,
    name varchar(30)
);  

CREATE TABLE Driver(
    SSN int PRIMARY KEY REFERENCES Person(SSN),
    dirverID int
);

CREATE TABLE NonProfessionalDriver(
    SSN int PRIMARY KEY REFERENCES Person(SSN)
);

CREATE TABLE ProfessionalDriver(
    SSN int PRIMARY KEY REFERENCES Person(SSN),
    medicalHistory varchar(100)
);

CREATE TABLE Vehicle(
    licensePlate varchar(9) PRIMARY KEY,
    year int,
    maxLiability REAL,
    iName varchar(30) REFERENCES InsuranceCo(name),
    pSSN int REFERENCES Person(SSN)
);

CREATE TABLE Car(
    licensePlate varchar(9) PRIMARY KEY REFERENCES Vehicle(licensePlate),
    make varchar(30)
);

CREATE TABLE Truck(
    licensePlate varchar(9) PRIMARY KEY REFERENCES Vehicle(licensePlate),
    capacity int,
    pdSSN int REFERENCES ProfessionalDriver(SSN)
);

CREATE TABLE Drives(
    licensePlate varchar(9) REFERENCES Car(licensePlate),
    npdSSN int REFERENCES NonProfessionalDriver(SSN),
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