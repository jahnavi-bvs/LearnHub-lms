# course: Java Programming – Beginner to Advanced

## module: Introduction to Java and JDK Setup
video: 4EP8YzcN0hQ

### Introduction
Java is a general-purpose, object-oriented language used for enterprise systems, Android apps and backend services. This module explains how Java programs run and how to set up your machine to write them.

### Key concepts
- **Write once, run anywhere:** source code compiles to bytecode, which runs on any machine with a JVM.
- **Compile and run are separate steps:** `javac` turns `.java` files into `.class` files, and `java` runs them.
- **Entry point:** every program starts at `public static void main(String[] args)`.

### Definitions
- **JDK (Java Development Kit):** the compiler and tools needed to build Java programs.
- **JRE (Java Runtime Environment):** the libraries and JVM needed to run them.
- **JVM (Java Virtual Machine):** the program that executes bytecode.
- **Bytecode:** the platform-independent output of the compiler.

### Example
```java
public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello, LearnHub!");
    }
}
```
Run `javac Hello.java`, then `java Hello`.

### Key takeaways
- Install the JDK, then confirm it with `java -version`.
- The file name must match the public class name.
- The JVM is what makes Java portable across operating systems.

## module: Variables, Data Types and Operators
video: RPoquiZydYc

### Introduction
Programs work by storing and changing values. This module covers how Java stores data in typed variables and combines values using operators.

### Key concepts
- **Static typing:** a variable's type is fixed when declared and checked by the compiler.
- **Primitive types:** `byte`, `short`, `int`, `long`, `float`, `double`, `char`, `boolean`.
- **Reference types:** objects such as `String` and arrays, where the variable refers to an object.
- **Operators:** arithmetic (`+ - * / %`), comparison (`== != < >`), logical (`&& || !`), assignment (`= +=`).

### Definitions
- **Variable:** a named storage location with a type.
- **Literal:** a fixed value written in code, such as `42`.
- **Type casting:** converting a value from one type to another.

### Example
```java
int marks = 85;
double percent = marks / 100.0; // 0.85
boolean passed = marks >= 40;   // true
int wrong = marks / 100;        // 0: integer division
```

### Key takeaways
- Dividing two `int` values discards the decimal part.
- Compare `String` contents with `.equals()`, not `==`.
- Pick the type that matches the data: `int` for counts, `double` for measurements.

## module: Conditional Statements and Loops
video: ldYLYRNaucM

### Introduction
By default, statements run one after another. Conditionals let a program choose a path, and loops let it repeat work.

### Key concepts
- **`if` / `else if` / `else`:** runs the first branch whose condition is true.
- **`switch`:** chooses among many fixed values.
- **`for`:** best when the number of repetitions is known.
- **`while`:** repeats as long as a condition holds.
- **`do-while`:** like `while`, but always runs at least once.
- **`break` and `continue`:** exit a loop, or skip to its next iteration.

### Definitions
- **Condition:** an expression that evaluates to `true` or `false`.
- **Iteration:** one pass through a loop's body.
- **Infinite loop:** a loop whose condition never becomes false.

### Example
```java
for (int i = 1; i <= 5; i++) {
    if (i % 2 == 0) {
        System.out.println(i + " is even");
    } else {
        System.out.println(i + " is odd");
    }
}
```

### Key takeaways
- Make sure every loop can end, by updating what its condition checks.
- Use `for` for counting and `while` for waiting on a condition.
- Keep nested conditions shallow so code stays readable.

## module: Arrays and Strings
video: n60Dn0UsbEk

### Introduction
Arrays hold many values of one type, and strings hold text. Almost every real program uses both.

### Key concepts
- **Fixed size:** an array's length cannot change after it is created.
- **Zero-based indexing:** the first element is at index 0, and the last at `length - 1`.
- **Immutable strings:** `String` methods return a new string instead of changing the original.
- **`StringBuilder`:** an efficient way to build text in a loop.

### Definitions
- **Array:** an ordered, fixed-size collection of same-typed elements.
- **Index:** an element's position in an array.
- **Immutable:** cannot be changed after creation.

### Example
```java
int[] scores = {70, 85, 90};
int total = 0;
for (int score : scores) {
    total += score;
}
String name = "learnhub".toUpperCase(); // "LEARNHUB"
```

### Key takeaways
- Accessing an invalid index throws `ArrayIndexOutOfBoundsException`.
- Use `.length` for arrays, and `.length()` for strings.
- `ArrayList` is the right choice when the size must grow or shrink.

## module: Object-Oriented Programming (OOP)
video: 7GwptabrYyk

### Introduction
Object-oriented programming organizes code around objects that combine data and behaviour. It helps large programs stay understandable and reusable.

### Key concepts
- **Encapsulation:** keep fields private and expose controlled methods.
- **Inheritance:** a class reuses another class's code using `extends`.
- **Polymorphism:** one method call behaves differently depending on the object's actual type.
- **Abstraction:** expose what an object does and hide how, using abstract classes and interfaces.

### Definitions
- **Class:** a blueprint describing data and behaviour.
- **Object:** an instance created from a class.
- **Constructor:** special code that initializes a new object.
- **Interface:** a contract of methods that a class promises to implement.

### Example
```java
class Animal {
    String speak() { return "..."; }
}
class Dog extends Animal {
    @Override
    String speak() { return "Woof"; }
}
Animal pet = new Dog();
pet.speak(); // "Woof": polymorphism
```

### Key takeaways
- Private fields protect an object's data from invalid changes.
- Inherit only for true "is-a" relationships.
- Programming to interfaces keeps code flexible.

## module: Exception Handling and File Handling
video: 1XAfapkBQjk

### Introduction
Programs meet problems at runtime, such as a missing file. Exception handling lets them respond without crashing, and file handling lets them keep data after they exit.

### Key concepts
- **`try` / `catch` / `finally`:** run risky code, handle failures, and always clean up.
- **Checked exceptions:** the compiler forces you to handle them, such as `IOException`.
- **Unchecked exceptions:** programming errors such as `NullPointerException`.
- **`throw` and `throws`:** raise an exception, or declare that a method may raise one.
- **try-with-resources:** closes files automatically.

### Definitions
- **Exception:** an object representing an error during execution.
- **Stack trace:** the list of method calls leading to the error.
- **Stream:** a sequence of data being read or written.

### Example
```java
try (BufferedReader reader = new BufferedReader(new FileReader("notes.txt"))) {
    System.out.println(reader.readLine());
} catch (IOException e) {
    System.out.println("Could not read file: " + e.getMessage());
}
```

### Key takeaways
- Catch specific exceptions, not a general `Exception`.
- Never leave a `catch` block empty, because it hides problems.
- Prefer try-with-resources so files always close.

## module: Collections Framework and Generics
video: rH0winlka8A

### Introduction
The Collections Framework provides ready-made data structures, and generics make them type-safe. Together they replace most manual array handling.

### Key concepts
- **`List`:** ordered, allows duplicates. `ArrayList` is the common choice.
- **`Set`:** no duplicates. `HashSet` is fast but unordered.
- **`Map`:** key-value pairs. `HashMap` gives fast lookup by key.
- **Generics:** `List<String>` lets the compiler reject non-strings.

### Definitions
- **Collection:** an object holding a group of elements.
- **Generic type:** a class with a type parameter, written in angle brackets.
- **Autoboxing:** automatic conversion such as `int` to `Integer`.

### Example
```java
Map<String, Integer> marks = new HashMap<>();
marks.put("Asha", 91);
marks.put("Ravi", 78);
for (Map.Entry<String, Integer> entry : marks.entrySet()) {
    System.out.println(entry.getKey() + ": " + entry.getValue());
}
```

### Key takeaways
- Choose by need: order, uniqueness, or lookup by key.
- Declare using the interface: `List<String> names = new ArrayList<>()`.
- Generics catch type mistakes at compile time rather than at runtime.

## module: JDBC and Database Connectivity
video: 7v2OnUti2eM

### Introduction
Most applications store data in a database. JDBC is Java's standard API for connecting to relational databases and running SQL.

### Key concepts
- **Connect:** `DriverManager.getConnection(url, user, password)`.
- **`PreparedStatement`:** a SQL statement with `?` placeholders.
- **`ResultSet`:** the rows returned by a query, read one at a time.
- **Close resources:** use try-with-resources for connections and statements.

### Definitions
- **JDBC driver:** a library that lets Java talk to one specific database.
- **SQL injection:** an attack that inserts SQL through user input.
- **Connection pool:** a set of reusable database connections.

### Example
```java
String sql = "SELECT name FROM students WHERE id = ?";
try (PreparedStatement statement = connection.prepareStatement(sql)) {
    statement.setInt(1, 7);
    ResultSet rows = statement.executeQuery();
    while (rows.next()) {
        System.out.println(rows.getString("name"));
    }
}
```

### Key takeaways
- Always use `PreparedStatement` with user input, because it prevents SQL injection.
- Never hardcode database passwords in source code.
- Frameworks like Spring Data JPA are built on top of JDBC.
