DROP TABLE IF EXISTS Orders;
DROP TABLE IF EXISTS Products;
DROP TABLE IF EXISTS Suppliers;
DROP TABLE IF EXISTS Customers;

CREATE TABLE Customers (
  id SERIAL PRIMARY KEY,
  name varchar(255) default NULL,
  address varchar(255) default NULL,
  city varchar(255),
  region varchar(50) default NULL,
  zip varchar(10) default NULL,
  country varchar(100) default NULL,
  phone varchar(100) default NULL
);

CREATE TABLE Suppliers (
  id SERIAL PRIMARY KEY,
  name varchar(255),
  address varchar(255) default NULL,
  city varchar(255),
  region varchar(50) default NULL,
  zip varchar(10) default NULL,
  country varchar(100) default NULL,
  phone varchar(100) default NULL
);

CREATE TABLE Products (
  id SERIAL PRIMARY KEY,
  name varchar(255),
  price integer NULL,
  supplier_id serial references Suppliers(id)
);

CREATE TABLE Orders (
  id SERIAL PRIMARY KEY,
  product_id integer references Products(id),
  customer_id integer references Customers(id),
  order_date varchar(255),
  total_cost integer default NULL
);