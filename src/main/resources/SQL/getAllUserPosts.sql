select id, name, description, date, colour, table_id
from
select id, name, description, date, colour, p.table_id
from
(
select table_id
from user_table
where user_email = ?
) as t
join posts as p
on (t.table_id = p.table_id) as res