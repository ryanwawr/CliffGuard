SELECT city, region, zip FROM "Customers" WHERE city LIKE 'New%';

SELECT id, order_date, total_cost from "Orders" where id < 10;

SELECT city, count(*) FROM "Customers" GROUP by city HAVING count(*) > 10;
