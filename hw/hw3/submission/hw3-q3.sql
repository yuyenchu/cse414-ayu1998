select f2.origin_city as origin_city, cast(count(f1.i) as real)/count(f2.fid)*100 as percentage
    from FLIGHTS as f2
    left outer join (select F.origin_city as o, F.fid as i
         from FLIGHTS as F
         where F.actual_time is not NULL
            and F.actual_time < 180) as f1
    on f1.i = f2.fid
    group by f2.origin_city;