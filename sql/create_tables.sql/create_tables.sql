CREATE TABLE SockSuppliers (
    SupplierID SERIAL PRIMARY KEY,
    SupplierName VARCHAR(255) NOT NULL
);

CREATE TABLE Socks (
    SockID SERIAL PRIMARY KEY,
    SockTypeID INTEGER,
    Operation VARCHAR(255) NOT NULL,
    SockType VARCHAR(255) NOT NULL,
    SockPrice INTEGER NOT NULL,
    SockSupplierID INTEGER REFERENCES SockSuppliers(SupplierID),
    Quantity INTEGER NOT NULL
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

INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (1, 'Sell', 'Invisible', 5, 1, 50);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (2, 'Sell', 'Low Cut', 10, 1,50);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (3, 'Sell', 'Over the Calf', 15, 1, 50);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (4, 'Sell', 'Invisible', 10, 2, 50);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (5, 'Sell', 'Low Cut', 15, 2, 50);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (6, 'Sell', 'Over the Calf', 5, 2, 50);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (7, 'Sell', 'Invisible', 15, 3, 50);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (8, 'Sell', 'Low Cut', 5, 3, 50);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (9, 'Sell', 'Over the Calf', 10, 3, 50);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (10, 'Sell', 'Invisible', 15, 4, 50);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (11, 'Sell', 'Low Cut', 10, 4, 50);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (12, 'Sell', 'Over the Calf', 5, 4, 50);




INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (1, 'Buy', 'Invisible', 5, 1, 10);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (2, 'Buy', 'Low Cut', 10, 1,15);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (3, 'Sell', 'Over the Calf', 15, 1, 50);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (4, 'Sell', 'Invisible', 10, 2, 50);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (5, 'Sell', 'Low Cut', 15, 2, 50);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (6, 'Sell', 'Over the Calf', 5, 2, 50);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (7, 'Sell', 'Invisible', 15, 3, 50);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (8, 'Sell', 'Low Cut', 5, 3, 50);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (9, 'Sell', 'Over the Calf', 10, 3, 50);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (10, 'Sell', 'Invisible', 15, 4, 50);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (11, 'Sell', 'Low Cut', 10, 4, 50);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID, Quantity) VALUES (12, 'Sell', 'Over the Calf', 5, 4, 50);





INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID) VALUES (1, 'Buy', 'Invisible', 10, 1);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID) VALUES (1, 'Buy', 'Invisible', 10, 1);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID) VALUES (1, 'Buy', 'Invisible', 10, 1);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID) VALUES (1, 'Buy', 'Invisible', 10, 1);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID) VALUES (1, 'Buy', 'Invisible', 10, 1);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID) VALUES (1, 'Buy', 'Invisible', 10, 1);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID) VALUES (1, 'Buy', 'Invisible', 10, 1);
INSERT into Socks (SockTypeID, Operation, SockType, SockPrice, SockSupplierID) VALUES (1, 'Buy', 'Invisible', 10, 1);


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
  CAST(random() * (20 - 5) + 5 AS INTEGER) AS SockPrice,
  SupplierID
FROM generate_series(1, 100) s
JOIN (
  SELECT SupplierID FROM SockSuppliers ORDER BY random() LIMIT 100
) suppliers ON TRUE;

-- Display the populated tables
SELECT * FROM SockSuppliers;
SELECT * FROM Socks;
