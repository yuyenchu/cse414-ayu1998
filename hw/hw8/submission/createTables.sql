-- add all your SQL setup statements here. 

-- You can assume that the following base table has been created with data loaded for you when we test your submission 
-- (you still need to create and populate it in your instance however),
-- although you are free to insert extra ALTER COLUMN ... statements to change the column 
-- names / types if you like.

--Flights (fid int, 
--         month_id int,        -- 1-12
--         day_of_month int,    -- 1-31 
--         day_of_week_id int,  -- 1-7, 1 = Monday, 2 = Tuesday, etc
--         carrier_id varchar(7), 
--         flight_num int,
--         origin_city varchar(34), 
--         origin_state varchar(47), 
--         dest_city varchar(34), 
--         dest_state varchar(46), 
--         departure_delay int, -- in mins
--         taxi_out int,        -- in mins
--         arrival_delay int,   -- in mins
--         canceled int,        -- 1 means canceled
--         actual_time int,     -- in mins
--         distance int,        -- in miles
--         capacity int, 
--         price int            -- in $             
--         )


CREATE TABLE Users (
    username varchar(20) PRIMARY KEY,
    password varchar(20),
    balance int
);

CREATE TABLE Reservations (
    rid int PRIMARY KEY,
    uname varchar(20) REFERENCES Users(username),
    flight1 int REFERENCES Flights(fid),
    flight2 int REFERENCES Flights(fid),
    paid int,       --1 means paid
    canceled int,   --1 means canceled
    price int
);

CREATE TABLE  Travel (
    month_id int,
    day_of_month int,
    uname varchar(20) REFERENCES Users(username)
);

CREATE TABLE RID (
    id int
);