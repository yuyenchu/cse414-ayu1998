.headers on
.mode column
select distinct c.name
    from FLIGHTS as f
    inner join CARRIERS as c
        on f.carrier_id = c.cid
    group by f.month_id, f.day_of_month, f.carrier_id
    having count(f.carrier_id) > 1000
    order by name;