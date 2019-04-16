create table FLIGHTS (
		fid int primary key, 
		month_id int foreign key references MONTHS,        -- 1-12
		day_of_month int,    -- 1-31 
		day_of_week_id int foreign key references WEEKDAYS,  -- 1-7, 1 = Monday, 2 = Tuesday, etc
		carrier_id varchar(7) foreign key references CARRIERS, 
		flight_num int,
		origin_city varchar(34), 
		origin_state varchar(47), 
		dest_city varchar(34), 
		dest_state varchar(46), 
		departure_delay int, -- in mins
		taxi_out int,        -- in mins
		arrival_delay int,   -- in mins
		canceled int,        -- 1 means canceled
		actual_time int,     -- in mins
		distance int,        -- in miles
		capacity int, 
		price int            -- in $             
		);
create table CARRIERS (cid varchar(7) primary key, name varchar(83));
create table MONTHS (mid int primary key, month varchar(9));
create table WEEKDAYS (did int primary key, day_of_week varchar(9));
PRAGMA foreign_keys=ON;
.mode csv
.import ../starter-code/flights-small.csv FLIGHTS
.import ../starter-code/carriers.csv CARRIERS
.import ../starter-code/months.csv MONTHS
.import ../starter-code/weekdays.csv WEEKDAYS
