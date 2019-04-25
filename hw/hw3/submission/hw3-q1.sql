select distinct f2.origin_city, f2.dest_city, f2.actual_time as time
    from (select F.origin_city as o, max(F.actual_time) as t
          from FLIGHTS as F
          group by F.origin_city) as f1, FLIGHTS as f2
    where f2.origin_city = f1.o and f2.actual_time = f1.t
    order by f2.origin_city, f2.dest_city asc;