.headers on
.mode column
select c.name name, sum(f.departure_delay) delay
    from FLIGHTS as f
    inner join CARRIERS as c
        on f.carrier_id = c.cid
    group by f.carrier_id
    order by name, delay;