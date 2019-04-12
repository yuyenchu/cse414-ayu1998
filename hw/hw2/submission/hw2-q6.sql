.headers on
.mode column
select c.name as carrier, max(f.price) as max_price
    from FLIGHTS as f
    inner join CARRIERS as c
        on f.carrier_id = c.cid
    where ((f.origin_city = 'Seattle WA' and f.dest_city = 'New York NY')
        or (f.origin_city = 'New York NY' and f.dest_city = 'Seattle WA'))
    group by f.carrier_id
    order by carrier, max_price;