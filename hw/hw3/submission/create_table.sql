create table CARRIERS (cid varchar(7), name varchar(83), primary key (cid));
create table MONTHS (mid int, month varchar(9), primary key (mid));
create table WEEKDAYS (did int, day_of_week varchar(9), primary key (did));
create table FLIGHTS (
		fid int, 
		month_id int,        -- 1-12
		day_of_month int,    -- 1-31 
		day_of_week_id int,  -- 1-7, 1 = Monday, 2 = Tuesday, etc
		carrier_id varchar(7), 
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
		price int,           -- in $             
		primary key (fid),
        foreign key (month_id) references MONTHS,
        foreign key (day_of_week_id) references WEEKDAYS,
        foreign key (carrier_id) references CARRIERS);
