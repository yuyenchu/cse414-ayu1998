.headers on
.mode column
select c.name, sum(f.departure_delay) as delay
    from FLIGHTS as f
    inner join CARRIERS as c
        on f.carrier_id = c.cid
    group by f.carrier_id
    order by name, delay;