CREATE TABLE SockSuppliers (
    SupplierID SERIAL PRIMARY KEY,
    SupplierName VARCHAR(255) NOT NULL,
);

CREATE TABLE Socks (
    SockID SERIAL PRIMARY KEY,
    SockType VARCHAR(255) NOT NULL,
    SockPrice DECIMAL(10, 2) NOT NULL,
    SockSupplierID INTEGER REFERENCES SockSuppliers(SupplierID),
);

CREATE TABLE Purchases (
    PurchaseID SERIAL PRIMARY KEY,
    SockID INTEGER REFERENCES Socks(SockID),
    Quantity INTEGER NOT NULL,
    PurchaseDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Sales (
    SaleID SERIAL PRIMARY KEY,
    SockID INTEGER REFERENCES Socks(SockID),
    Quantity INTEGER NOT NULL,
    SaleDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



-- Generate random sock suppliers
INSERT INTO SockSuppliers (SupplierName)
VALUES
  ('Supplier1'),
  ('Supplier2'),
  ('Supplier3'),
  ('Supplier4');

-- Generate random socks with random prices and link them to sock suppliers
INSERT INTO Socks (SockType, SockPrice, SockSupplierID)
SELECT
  CASE WHEN random() < 0.33 THEN 'Invisible'
       WHEN random() < 0.66 THEN 'Low Cut'
       ELSE 'Over the Calf'
  END AS SockType,
  CAST(random() * (20 - 5) + 5 AS DECIMAL(10, 2)) AS SockPrice,
  SupplierID
FROM generate_series(1, 100) s
JOIN (
  SELECT SupplierID FROM SockSuppliers ORDER BY random() LIMIT 100
) suppliers ON TRUE;

-- Display the populated tables
SELECT * FROM SockSuppliers;
SELECT * FROM Socks;
