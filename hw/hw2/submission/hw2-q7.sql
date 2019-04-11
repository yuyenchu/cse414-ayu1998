.headers on
.mode column
select sum(f.capacity) capacity
from FLIGHTS as f
where ((f.origin_city = 'Seattle WA' and f.dest_city = 'San Francisco CA')
    or (f.origin_city = 'San Francisco CA' and f.dest_city = 'Seattle WA'))
    and f.month_id = 7
    and f.day_of_month = 10;