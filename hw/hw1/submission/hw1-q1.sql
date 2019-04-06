create table edges (source int, destination int);
insert into edges 
values 	(10,5),
	(6,25), 
	(1,3), 
	(4,4);
select * from edges;
select source from edges;
select * from edges where source > destination;
insert into edges values ('1','2000'); --it works because auto convert into int

