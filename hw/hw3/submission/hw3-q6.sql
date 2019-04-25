select distinct c.name as carrier
    from CARRIERS as c, 
        (select F.carrier_id
            from FLIGHTS as F
            where f.origin_city = 'Seattle WA'
                and f.dest_city = 'San Francisco CA')as f
    where f.carrier_id = c.cid
    order by carrier asc;