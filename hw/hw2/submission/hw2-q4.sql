.headers on
.mode column
select distinct c.name
    from FLIGHTS as f
    inner join CARRIERS as c
        on f.carrier_id = c.cid
    group by f.carrier_id, f.month_id, f.day_of_month
    having count(f.fid) > 1000
    order by name;