select distinct f2.dest_city as city
    from FLIGHTS as f1, FLIGHTS as f2
    where f1.origin_city = 'Seattle WA' 
        and f1.dest_city = f2.origin_city 
        and f2.dest_city not in (select distinct F.dest_city
                                from FLIGHTS as F
                                where F.origin_city = 'Seattle WA')
        and f2.dest_city != 'Seattle WA' 
    order by city asc;