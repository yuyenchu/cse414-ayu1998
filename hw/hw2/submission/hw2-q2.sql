.headers on
.mode column
select  c.name name,
        f1.flight_num f1_flight_num, 
        f1.origin_city f1_origin_city,
        f1.dest_city f1_dest_city, 
        f1.actual_time f1_actual_time, 
        f2.flight_num f2_flight_num, 
        f2.origin_city f2_origin_city, 
        f2.dest_city f2_dest_city, 
        f2.actual_time f2_actual_time, 
        f1.actual_time+f2.actual_time actual_time
    from FLIGHTS as f1 
    inner join FLIGHTS as f2 
        on f1.dest_city = f2.origin_city 
        and f1.month_id = f2.month_id 
        and f1.day_of_month = f2.day_of_month 
        and f1.carrier_id = f2.carrier_id
    inner join CARRIERS as c
        on f1.carrier_id = c.cid
    where f1.actual_time+f2.actual_time < 420 
        and f1.origin_city = 'Seattle WA' 
        and f2.dest_city = 'Boston MA' 
        and f1.month_id = 7 
        and f1.day_of_month = 15
    order by c.name;