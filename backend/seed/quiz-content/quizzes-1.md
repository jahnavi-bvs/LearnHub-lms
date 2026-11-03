# course: Java Programming – Beginner to Advanced

## quiz: Java Quiz 1: Fundamentals
? Which tool compiles a .java file into bytecode?
= javac
- java
- jar
- javadoc
? What makes Java programs portable across operating systems?
= Bytecode runs on any machine with a JVM
- The source code is written in English
- Every operating system includes Java's compiler
- Java programs are compiled to machine code for each system
? Which package do you need to build Java programs, not just run them?
= JDK
- JRE
- JVM
- JAR
? What does 7 / 2 evaluate to when both values are int?
= 3
- 3.5
- 4
- 3.0
? How should you compare the contents of two String objects?
= With the equals() method
- With the == operator
- With the = operator
- With the compareType() method

## quiz: Java Quiz 2: Core Features
? Which loop always runs its body at least once?
= do-while
- for
- while
- for-each
? What is the index of the first element of a Java array?
= 0
- 1
- -1
- It depends on the array's type
? What happens when code accesses an array index that doesn't exist?
= ArrayIndexOutOfBoundsException is thrown
- The array grows automatically
- The value 0 is returned
- The compiler always catches the mistake
? Why is a String described as immutable?
= Its contents can't change after it is created
- It can only hold one word
- It can't be assigned to a variable
- It can only be used once
? Which OOP principle keeps fields private and exposes controlled methods?
= Encapsulation
- Inheritance
- Polymorphism
- Compilation
? Which keyword makes one class inherit from another?
= extends
- implements
- inherits
- super

## quiz: Java Quiz 3: Advanced Topics
? Which block always runs, whether or not an exception occurs?
= finally
- catch
- try
- throw
? Which is an example of a checked exception?
= IOException
- NullPointerException
- ArrayIndexOutOfBoundsException
- ArithmeticException
? Why use try-with-resources when reading a file?
= The file is closed automatically
- The file is read faster
- Exceptions can no longer occur
- The file is copied into memory first
? Which collection does NOT allow duplicate elements?
= HashSet
- ArrayList
- LinkedList
- Vector
? Which collection stores key-value pairs?
= HashMap
- ArrayList
- HashSet
- TreeSet
? What do generics such as List<String> mainly provide?
= Type checking at compile time
- Faster execution at runtime
- Automatic sorting
- Automatic saving to a database
? Why use PreparedStatement in JDBC with user input?
= It prevents SQL injection
- It removes the need for a database driver
- It makes the connection permanent
- It replaces SQL with Java

# course: Full Stack Web Development

## quiz: Full Stack Quiz 1: Web Basics
? What does the frontend of a web application refer to?
= The part users see and interact with in the browser
- The database that stores the data
- The server's operating system
- The network cables between computers
? Which protocol do browsers and servers use to communicate on the web?
= HTTP
- FTP
- SMTP
- SSH
? Which HTML element is the most semantic choice for a site's navigation links?
= nav
- div
- span
- b
? What is the alt attribute on an image for?
= A text description for those who can't see the image
- Choosing an alternative image size
- Making the image load faster
- Linking the image to another page
? What does the CSS box model consist of?
= Content, padding, border and margin
- Header, body, footer and sidebar
- Rows, columns, cells and grids
- Fonts, colours, images and links

## quiz: Full Stack Quiz 2: JavaScript and React
? Which keyword declares a variable that won't be reassigned?
= const
- let
- var
- static
? What is the difference between === and == in JavaScript?
= === doesn't convert types before comparing
- === compares only numbers
- == is faster and always safer
- There is no difference
? What does fetch() NOT do automatically when a server returns 404?
= Throw an error
- Return a Promise
- Give you a response object
- Complete the request
? In React, how does data normally pass from a parent to a child component?
= Through props
- Through CSS
- Through the URL only
- Through global variables only
? Which hook stores data that changes over time in a component?
= useState
- useEffect
- useParams
- useRoute
? Why do React list items need a key?
= So React can track each item between renders
- To encrypt the list
- To sort the list automatically
- To make the items clickable

## quiz: Full Stack Quiz 3: Backend and APIs
? What is Node.js?
= A runtime that runs JavaScript outside the browser
- A CSS framework
- A database
- A web browser
? In Express, what is middleware?
= A function that runs before a route's handler
- A kind of database table
- The frontend's stylesheet
- A type of HTML element
? Where should an application's secrets, such as API keys, be kept?
= In environment variables
- In the source code
- In the public HTML
- In the browser's address bar
? Which HTTP method is normally used to create a new resource?
= POST
- GET
- DELETE
- HEAD
? Which status code means the client isn't logged in?
= 401
- 200
- 201
- 500
? What does it mean that a REST API is stateless?
= Each request carries everything needed to process it
- The server has no database
- The API can't change any data
- Responses never change
? What does CORS control?
= Whether a browser lets a page call another origin
- How quickly a page loads
- How data is stored in a database
- How CSS is applied to HTML

# course: Python Programming

## quiz: Python Quiz 1: Basics
? How does Python define a block of code?
= By indentation
- By curly braces
- By semicolons
- By the word begin
? Which tool installs Python packages?
= pip
- javac
- npm
- gcc
? What type does input() always return?
= str
- int
- float
- bool
? What does 7 // 2 evaluate to in Python?
= 3
- 3.5
- 4
- 1
? What does range(5) produce?
= The numbers 0 to 4
- The numbers 1 to 5
- The numbers 0 to 5
- The number 5 five times

## quiz: Python Quiz 2: Functions and Collections
? Which keyword defines a function?
= def
- function
- func
- define
? What is the difference between return and print?
= return gives a value back; print only displays it
- They do exactly the same thing
- print is required before return
- return only works with strings
? Which collection is ordered but can't be changed after creation?
= Tuple
- List
- Set
- Dictionary
? Which collection automatically removes duplicates?
= Set
- List
- Tuple
- String
? How do you look up a value in a dictionary?
= By its key
- Only by its position
- By sorting it first
- By its data type
? What does [x * 2 for x in nums] create?
= A new list with each number doubled
- A dictionary of pairs
- A set with duplicates removed
- A function that runs later

## quiz: Python Quiz 3: OOP, Files and Libraries
? Which method initializes a new object in a class?
= __init__
- __new_object__
- create
- __start__
? What does self refer to inside a method?
= The current object
- The class's parent
- The Python interpreter
- The module's file name
? What does opening a file in "w" mode do to existing content?
= Erases it
- Keeps it and adds to the end
- Makes it read-only
- Copies it to a backup file
? Why open files using with?
= The file is closed automatically
- The file opens faster
- The file becomes encrypted
- Errors can no longer happen
? Which block catches an error in Python?
= except
- catch
- rescue
- error
? Which library is commonly used to work with tables of data?
= pandas
- requests
- flask
- pygame
? What is a requirements.txt file for?
= Listing the packages a project needs
- Storing passwords
- Writing the program's main code
- Describing a database schema

# course: Database Management Systems

## quiz: DBMS Quiz 1: Foundations
? What is a main advantage of a DBMS over plain files?
= It reduces redundancy and keeps data consistent
- It always uses less disk space
- It removes the need for backups
- It only allows one user at a time
? In the relational model, what is a tuple?
= A row in a table
- A column in a table
- A whole database
- A type of key
? How is a many-to-many relationship implemented in tables?
= With a linking table
- With a single extra column
- By merging both tables
- It can't be implemented
? What uniquely identifies each row in a table?
= The primary key
- A foreign key
- An index
- A view
? What does referential integrity require?
= A foreign key must match an existing row
- A primary key can be null
- Every column must be unique
- Tables must have equal numbers of rows

## quiz: DBMS Quiz 2: SQL
? Which SQL keyword filters rows before grouping?
= WHERE
- HAVING
- ORDER BY
- GROUP BY
? Which clause filters groups after GROUP BY?
= HAVING
- WHERE
- LIMIT
- FROM
? How do you correctly test for a missing value?
= IS NULL
- = NULL
- == NULL
- EQUALS NULL
? What happens if you run UPDATE without a WHERE clause?
= Every row in the table is changed
- Nothing happens
- Only the first row is changed
- SQL always rejects it
? Which join keeps all rows from the left table, even without a match?
= LEFT JOIN
- INNER JOIN
- CROSS JOIN
- SELF JOIN
? Which statement category does CREATE TABLE belong to?
= DDL
- DML
- DCL
- TCL

## quiz: DBMS Quiz 3: Design and Transactions
? What is the main purpose of normalization?
= Removing redundant data
- Making every query run faster
- Encrypting the data
- Adding more tables for security
? What does 1NF require?
= Each cell holds a single value
- No column depends on another non-key column
- Every table has a foreign key
- Every determinant is a candidate key
? What does the A in ACID stand for?
= Atomicity
- Availability
- Accuracy
- Authorization
? What does a transaction's rollback do?
= Undoes all of its changes
- Makes its changes permanent
- Runs it a second time
- Deletes the table
? What is a deadlock?
= Transactions waiting on each other forever
- A database that has run out of disk
- A query with no WHERE clause
- A table with no primary key
? What is the trade-off of adding an index?
= Faster reads but slower writes and more storage
- Faster writes but slower reads
- Less storage but less security
- No trade-off at all
? Which SQL command shows how a query will be executed?
= EXPLAIN
- DESCRIBE TABLE
- SHOW RESULT
- TRACE ROW

# course: Artificial Intelligence & Machine Learning

## quiz: AI & ML Quiz 1: Foundations
? How are AI, ML and deep learning related?
= Deep learning is part of ML, and ML is part of AI
- AI is part of deep learning
- They are three unrelated fields
- ML and AI are both parts of deep learning
? What does supervised learning need?
= Labelled training examples
- No data at all
- Only rewards and penalties
- Only unlabelled data
? What is a feature in machine learning?
= An input variable
- The correct answer
- The final trained model
- A software update
? Which library provides DataFrames?
= pandas
- NumPy
- Matplotlib
- Jupyter
? In scikit-learn, which method trains a model?
= fit
- predict
- train_all
- learn_now

## quiz: AI & ML Quiz 2: Data and Algorithms
? What does a loss function measure?
= A model's error
- A dataset's size
- Training speed
- The number of features
? What does gradient descent do during training?
= Adjusts weights to reduce the loss
- Adds more data automatically
- Deletes unhelpful features
- Increases the error on purpose
? What is data leakage?
= Test information influencing training
- A dataset being stolen
- Missing values in a table
- A model running out of memory
? Why hold back a test set?
= To evaluate on data the model hasn't seen
- To make training faster
- To use less disk space
- To increase the training accuracy
? Linear regression is normally used to predict what?
= A number
- A category only
- A cluster
- An image
? What is overfitting?
= Learning the training data too closely and failing on new data
- A model too simple to find the pattern
- Using too little memory
- Training on too many computers

## quiz: AI & ML Quiz 3: Advanced Topics
? Which is a clustering algorithm?
= k-means
- Linear regression
- Logistic regression
- Decision tree
? What does PCA do?
= Reduces the number of features
- Labels the data automatically
- Trains neural networks
- Removes duplicate rows only
? Why can accuracy mislead with imbalanced classes?
= Always predicting the common class scores highly
- Accuracy can't be calculated for them
- Accuracy is always zero for them
- Imbalanced data can't be trained on
? What does backpropagation calculate?
= How each weight affected the error
- How many layers to add
- How much data to collect
- How long training will take
? What is one epoch?
= One full pass through the training data
- One neuron in a network
- One prediction by the model
- One layer of the network
? Which network type is especially suited to images?
= Convolutional neural network
- Linear regression
- k-means
- Decision stump
? What is model drift?
= Performance declining as real-world data changes
- A model moving to another server
- Training getting faster over time
- A model's file growing larger
