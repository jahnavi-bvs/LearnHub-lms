# course: Database Management Systems

## module: Introduction to DBMS and Database Architecture
video: 6Iu45VZGQDk

### Introduction
A database management system stores data safely and lets many users access it at once. This module explains why databases replaced plain files and how they are structured.

### Key concepts
- **Problems with files:** duplicated data, inconsistency and no safe concurrent access.
- **What a DBMS provides:** querying, security, backup and recovery.
- **Three-level architecture:** external (user views), conceptual (logical design), internal (physical storage).
- **Data independence:** storage can change without breaking applications.

### Definitions
- **Database:** an organized collection of related data.
- **DBMS:** the software managing a database, such as MySQL.
- **Schema:** the structure of the database.
- **Instance:** the data stored at one moment.

### Example
A college stores students, courses and marks. With separate files, a changed phone number must be updated everywhere. With a DBMS, it is stored once and every department sees the same value.

### Key takeaways
- A DBMS reduces redundancy and keeps data consistent.
- The schema changes rarely, while data changes constantly.
- Layers separate how data is used from how it is stored.

## module: ER Model and Relational Model
video: wOD02sezmX8

### Introduction
Before building tables, designers model the real world. The ER model captures that design, and the relational model turns it into tables.

### Key concepts
- **Entities:** things we store data about, such as Student.
- **Attributes:** properties, such as name.
- **Relationships:** links, such as Student enrolls in Course.
- **Cardinality:** one-to-one, one-to-many, many-to-many.
- **Mapping:** a many-to-many relationship becomes its own table.

### Definitions
- **Relation:** a table.
- **Tuple:** a row in a table.
- **Attribute:** a column in a table.
- **ER diagram:** a visual model of entities and relationships.

### Example
A student takes many courses and a course has many students, so we add a linking table:
```sql
enrollments(student_id, course_id, enrolled_at)
```

### Key takeaways
- Design the ER model first, then derive the tables.
- One-to-many relationships use a foreign key on the "many" side.
- Many-to-many relationships always need a linking table.

## module: Keys and Integrity Constraints
video: _UZLrD_R0T4

### Introduction
Keys identify rows, and constraints stop invalid data from entering a database. They enforce the rules at the lowest level, whatever an application does.

### Key concepts
- **Super key:** any set of columns that uniquely identifies a row.
- **Candidate key:** a minimal super key.
- **Primary key:** the candidate key chosen as the identifier.
- **Foreign key:** a column referring to a primary key in another table.
- **Constraints:** `NOT NULL`, `UNIQUE`, `CHECK`, `DEFAULT`.

### Definitions
- **Entity integrity:** a primary key cannot be null.
- **Referential integrity:** a foreign key must match an existing row.
- **Composite key:** a key made of more than one column.

### Example
```sql
CREATE TABLE enrollments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id BIGINT NOT NULL,
  course_id BIGINT NOT NULL,
  UNIQUE (student_id, course_id)
);
```
The unique rule stops a student enrolling twice.

### Key takeaways
- Every table needs a primary key.
- Foreign keys prevent orphaned rows.
- Constraints protect data even when application code has bugs.

## module: SQL Fundamentals and Queries
video: Hl4NZB1XR9c

### Introduction
SQL is the standard language for working with relational databases. You describe the result you want, and the database works out how to get it.

### Key concepts
- **DDL:** defines structure with `CREATE`, `ALTER`, `DROP`.
- **DML:** changes data with `INSERT`, `UPDATE`, `DELETE`.
- **`SELECT`:** reads data, with `WHERE`, `ORDER BY` and `LIMIT`.
- **Aggregates:** `COUNT`, `SUM`, `AVG`, used with `GROUP BY` and `HAVING`.

### Definitions
- **Query:** a request for data.
- **Clause:** a part of a statement, such as `WHERE`.
- **NULL:** a missing or unknown value.

### Example
```sql
SELECT course_id, COUNT(*) AS students
FROM enrollments
GROUP BY course_id
HAVING COUNT(*) > 10
ORDER BY students DESC;
```

### Key takeaways
- `WHERE` filters rows before grouping; `HAVING` filters groups after it.
- Test for missing values with `IS NULL`, not `= NULL`.
- An `UPDATE` or `DELETE` without `WHERE` changes every row.

## module: Joins, Subqueries and Advanced SQL
video: 0OQJDd3QqQM

### Introduction
Related data lives in separate tables. Joins bring it together, and subqueries let one query use the result of another.

### Key concepts
- **`INNER JOIN`:** only rows that match in both tables.
- **`LEFT JOIN`:** all rows from the left table, matched where possible.
- **Subquery:** a query inside another query.
- **Views:** saved queries used like tables.
- **Window functions:** calculations across related rows, such as `RANK()`.

### Definitions
- **Join condition:** how rows are matched, written after `ON`.
- **Correlated subquery:** a subquery that uses values from the outer query.
- **CTE:** a named temporary result, written with `WITH`.

### Example
```sql
SELECT c.title, COUNT(e.id) AS students
FROM courses c
LEFT JOIN enrollments e ON e.course_id = c.id
GROUP BY c.id, c.title;
```
`LEFT JOIN` keeps courses that have no students, showing 0.

### Key takeaways
- Use `LEFT JOIN` when unmatched rows must still appear.
- Always write a join condition, or you get every combination of rows.
- A join is often clearer and faster than an equivalent subquery.

## module: Functional Dependencies and Normalization
video: GFQaEYEc8_8

### Introduction
Normalization organizes tables to remove repeated data. It prevents errors that occur when the same fact is stored in several places.

### Key concepts
- **Anomalies:** insert, update and delete problems caused by redundancy.
- **1NF:** every cell holds a single value.
- **2NF:** no column depends on only part of a composite key.
- **3NF:** no column depends on another non-key column.
- **BCNF:** every determinant must be a candidate key.

### Definitions
- **Functional dependency:** `A -> B` means A determines B.
- **Partial dependency:** depends on part of a composite key.
- **Transitive dependency:** depends on a key through another column.
- **Denormalization:** deliberately duplicating data to speed up reads.

### Example
In `orders(order_id, customer_id, customer_city)`, the city depends on the customer rather than the order. Moving it to `customers(customer_id, city)` satisfies 3NF, so a move is updated once.

### Key takeaways
- Each fact should be stored in exactly one place.
- 3NF is enough for most applications.
- Denormalize only for a measured performance need.

## module: Transactions, ACID Properties and Concurrency Control
video: GAe5oB742dw

### Introduction
A transaction groups several operations into one all-or-nothing unit. This keeps data correct during failures and when many users work at once.

### Key concepts
- **Atomicity:** all steps happen, or none do.
- **Consistency:** rules hold before and after the transaction.
- **Isolation:** concurrent transactions do not interfere.
- **Durability:** committed data survives a crash.
- **Locks:** control access to data being changed.
- **Isolation levels:** trade safety against speed.

### Definitions
- **Commit:** make changes permanent.
- **Rollback:** undo all changes.
- **Dirty read:** reading uncommitted data.
- **Deadlock:** transactions waiting on each other forever.

### Example
```sql
START TRANSACTION;
UPDATE accounts SET balance = balance - 500 WHERE id = 1;
UPDATE accounts SET balance = balance + 500 WHERE id = 2;
COMMIT;
```
If the second update fails, a rollback restores the first.

### Key takeaways
- Use a transaction whenever several changes must succeed together.
- Keep transactions short, because they hold locks.
- Stronger isolation is safer but reduces concurrency.

## module: Indexing, Query Optimization and Database Security
video: BIlFTFrEFOI

### Introduction
As data grows, queries slow down and security risks rise. Indexes make lookups fast, query plans reveal problems, and access rules protect the data.

### Key concepts
- **Index:** a sorted structure, usually a B+ tree, that finds rows quickly.
- **Trade-off:** indexes speed up reads but slow down writes and use space.
- **`EXPLAIN`:** shows how the database will run a query.
- **Security:** least privilege, encryption, and parameterized queries.

### Definitions
- **Full table scan:** reading every row to find matches.
- **Query plan:** the steps chosen to run a query.
- **SQL injection:** running attacker-supplied SQL via user input.
- **Least privilege:** giving only the access that is needed.

### Example
```sql
CREATE INDEX idx_enrollments_student ON enrollments (student_id);
EXPLAIN SELECT * FROM enrollments WHERE student_id = 7;
```

### Key takeaways
- Index columns often used in `WHERE` and `JOIN`, not all columns.
- Use `EXPLAIN` before guessing why a query is slow.
- Application accounts should never have full admin rights.
