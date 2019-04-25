select distinct c.name as carrier
    from FLIGHTS as f, CARRIERS as c
    where f.carrier_id = c.cid
        and f.origin_city = 'Seattle WA'
        and f.dest_city = 'San Francisco CA'
    order by carrier asc;