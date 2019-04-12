.headers on
.mode column
select c.name, 
        100*avg(f.canceled) as percent
    from(select * from FLIGHTS as F
            where F.origin_city = 'Seattle WA') as f
    inner join CARRIERS as c
        on f.carrier_id = c.cid
    group by f.carrier_id
    having percent > 0.5
    order by percent asc;