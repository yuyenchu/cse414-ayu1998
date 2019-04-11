.headers on
.mode column
select  w.day_of_week day_of_week, max(f.avg_delay) delay
    from (select F.day_of_week_id id, avg(F.departure_delay) avg_delay
            from FLIGHTS as F
            group by F.day_of_week_id) as f
        , WEEKDAYS as w
    where f.id = w.did;