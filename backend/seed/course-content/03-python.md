# course: Python Programming

## module: Introduction to Python and Environment Setup
video: kqtD5dpn9C8

### Introduction
Python is a readable, general-purpose language used for web development, automation and data science. This module gets it installed and running your first program.

### Key concepts
- **Interpreted:** Python runs code directly without a separate compile step.
- **Indentation matters:** blocks are defined by indentation rather than braces.
- **Virtual environments:** keep each project's packages separate.
- **pip:** Python's package installer.

### Definitions
- **Interpreter:** the program that executes Python code.
- **REPL:** an interactive prompt that runs code line by line.
- **Virtual environment:** an isolated folder of packages for one project.

### Example
```python
name = input("Your name: ")
print(f"Hello, {name}!")
```
Create an environment with `python -m venv .venv`.

### Key takeaways
- Tick "Add Python to PATH" when installing on Windows.
- Use one virtual environment per project.
- Confirm your install with `python --version`.

## module: Variables, Data Types and Operators
video: LKFrQXaoSMQ

### Introduction
Variables give names to values so programs can use them. Python works out each value's type automatically while the program runs.

### Key concepts
- **Dynamic typing:** a variable's type comes from its current value.
- **Core types:** `int`, `float`, `str`, `bool`, `None`.
- **Operators:** arithmetic (`+ - * / // % **`), comparison (`== != < >`), logical (`and or not`).
- **Conversion:** `int()`, `float()` and `str()` change types.

### Definitions
- **Variable:** a name that refers to a value.
- **Expression:** code that produces a value.
- **f-string:** a string that embeds values, written `f"..."`.

### Example
```python
price = 499
quantity = 2
total = price * quantity
print(f"Total: {total}")  # Total: 998
print(7 / 2, 7 // 2, 7 % 2)  # 3.5 3 1
```

### Key takeaways
- `input()` always returns a string, so convert it before doing maths.
- `/` gives a decimal result, and `//` gives a whole number.
- Use clear names such as `total_marks`, in snake_case.

## module: Conditional Statements and Loops
video: DZwmZ8Usvnk

### Introduction
Conditionals make decisions and loops repeat work. Together they give a program its logic.

### Key concepts
- **`if` / `elif` / `else`:** runs the first branch that is true.
- **`for`:** loops over the items of a sequence.
- **`while`:** loops while a condition is true.
- **`range()`:** generates numbers to loop over.
- **`break` and `continue`:** stop a loop, or skip to its next pass.

### Definitions
- **Boolean:** a value that is `True` or `False`.
- **Truthy and falsy:** empty values such as `""`, `[]` and `0` count as false.
- **Iteration:** one pass through a loop.

### Example
```python
for mark in [35, 72, 90]:
    if mark >= 75:
        print(mark, "distinction")
    elif mark >= 40:
        print(mark, "pass")
    else:
        print(mark, "fail")
```

### Key takeaways
- A colon ends the condition line, and the indented block follows.
- Use `for` over a sequence rather than managing an index by hand.
- `range(5)` gives 0 to 4, not 1 to 5.

## module: Functions and Modules
video: 89cGQjB5R4M

### Introduction
Functions package logic so it can be reused, and modules organize functions into separate files. Both keep programs manageable as they grow.

### Key concepts
- **`def`:** defines a function.
- **Parameters and return values:** input and output of a function.
- **Default and keyword arguments:** make calls flexible and clear.
- **`import`:** loads code from another module.
- **Standard library:** modules included with Python, such as `math` and `datetime`.

### Definitions
- **Parameter:** a name in the function definition.
- **Argument:** the value passed in a call.
- **Module:** a `.py` file you can import.
- **Scope:** where a variable can be used.

### Example
```python
import math

def circle_area(radius, precision=2):
    return round(math.pi * radius ** 2, precision)

print(circle_area(3))  # 28.27
```

### Key takeaways
- A function should do one clear thing.
- `return` gives a value back, and `print` only displays it.
- Variables created inside a function are local to it.

## module: Lists, Tuples, Sets and Dictionaries
video: R-HLU9Fl5ug

### Introduction
Python has four core collection types. Choosing the right one makes code simpler and faster.

### Key concepts
- **List `[]`:** ordered and changeable; allows duplicates.
- **Tuple `()`:** ordered but unchangeable.
- **Set `{}`:** unordered with unique values; fast membership checks.
- **Dictionary `{key: value}`:** fast lookup by key.
- **Comprehensions:** build a collection in one line.

### Definitions
- **Mutable:** can be changed after creation.
- **Immutable:** cannot be changed after creation.
- **Key:** the unique identifier for a dictionary value.

### Example
```python
marks = {"Asha": 91, "Ravi": 78}
marks["Meena"] = 85
passed = [name for name, mark in marks.items() if mark >= 80]
unique = set([1, 1, 2])  # {1, 2}
```

### Key takeaways
- Use a list by default, and a dictionary when looking things up by name.
- Use a set to remove duplicates.
- Tuples suit fixed groups of values, such as coordinates.

## module: Object-Oriented Programming in Python
video: JeznW_7DlB0

### Introduction
Classes bundle data and the functions that work on it. This style suits programs modelling real things, such as students and courses.

### Key concepts
- **`class`:** defines a new type.
- **`__init__`:** sets up a new object.
- **`self`:** refers to the current object.
- **Inheritance:** a child class reuses a parent class.
- **`__str__`:** controls how an object prints.

### Definitions
- **Class:** a blueprint for objects.
- **Instance:** one object made from a class.
- **Attribute:** a value stored on an object.
- **Method:** a function defined in a class.

### Example
```python
class Student:
    def __init__(self, name):
        self.name = name
        self.courses = []

    def enroll(self, course):
        self.courses.append(course)

asha = Student("Asha")
asha.enroll("Python")
```

### Key takeaways
- Every method's first parameter is `self`.
- Prefer simple functions until a class genuinely helps.
- Use inheritance only for real "is-a" relationships.

## module: File Handling and Exception Handling
video: Sx1Hjr67xO0

### Introduction
Files let programs keep data after they exit. Exception handling keeps programs running when something goes wrong, such as a missing file.

### Key concepts
- **`open()` modes:** `"r"` read, `"w"` overwrite, `"a"` append.
- **`with`:** closes the file automatically.
- **`try` / `except` / `finally`:** handle errors and clean up.
- **Common exceptions:** `FileNotFoundError`, `ValueError`, `KeyError`.

### Definitions
- **Exception:** an error raised while the program runs.
- **Context manager:** an object used with `with` that handles setup and cleanup.
- **Traceback:** Python's report of where an error occurred.

### Example
```python
try:
    with open("marks.txt") as file:
        total = sum(int(line) for line in file)
    print(total)
except FileNotFoundError:
    print("marks.txt does not exist")
except ValueError:
    print("The file contains something that isn't a number")
```

### Key takeaways
- Always open files using `with`.
- `"w"` erases existing content, so use `"a"` to add.
- Catch specific exceptions rather than using a bare `except`.

## module: Python Libraries and Mini Projects
video: yVl_G-F7m8c

### Introduction
Python's strength lies in its libraries, which solve common problems in a few lines. Mini projects are where syntax turns into real skill.

### Key concepts
- **Popular libraries:** `requests` for the web, `pandas` for data tables, `matplotlib` for charts, `numpy` for numbers.
- **Installing:** `pip install requests`.
- **Requirements file:** records a project's packages.
- **Project thinking:** break a big task into small functions.

### Definitions
- **Library:** reusable code written by others.
- **Package:** a library that can be installed.
- **`requirements.txt`:** the list of packages a project needs.

### Example
A quiz game is a good first project:
```python
score = 0
if input("Capital of India? ").strip().lower() == "new delhi":
    score += 1
print(f"Score: {score}")
```

### Key takeaways
- Check for a library before writing something from scratch.
- Build small projects: a quiz, an expense tracker, a to-do list.
- Save dependencies with `pip freeze > requirements.txt`.
