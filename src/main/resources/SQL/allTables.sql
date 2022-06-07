select id, name, admin_email
from tables
join user_table
on tables.id = user_table.table_id
where user_table.user_email = ?
order by id