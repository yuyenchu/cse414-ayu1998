select distinct f.origin_city as city
    from FLIGHTS as f
    where f.actual_time is not NULL 
        and f.origin_city not in (select F.origin_city
                                  from FLIGHTS F
                                  where F.actual_time >= 180)
    order by city asc;