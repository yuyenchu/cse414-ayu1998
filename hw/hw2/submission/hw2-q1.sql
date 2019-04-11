.headers on
.mode column
select distinct f.flight_num from FLIGHTS as f
	where f.day_of_week_id = 1 
		and f.origin_city = 'Seattle WA' 
		and f.dest_city = 'Boston MA' 
		and f.carrier_id = 'AS';
