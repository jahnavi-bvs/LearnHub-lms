# course: Applied Mathematics

## module: Fundamentals of Algebra
video: NybHckSEQBI

### Introduction
Algebra uses letters to stand for unknown or changing values. It turns real-world situations into equations that can be solved systematically.

### Key concepts
- **Variables and expressions:** `3x + 2` combines numbers and letters.
- **Solving equations:** do the same operation to both sides.
- **Order of operations:** brackets, powers, multiplication and division, addition and subtraction.
- **Factoring:** rewriting an expression as a product.

### Definitions
- **Variable:** a symbol standing for a number.
- **Coefficient:** the number multiplying a variable.
- **Equation:** a statement that two expressions are equal.
- **Linear equation:** an equation whose variable has a power of 1.

### Example
Solve `2x + 6 = 14`:
```text
2x = 14 - 6 = 8
x = 8 / 2 = 4
```
Check: 2(4) + 6 = 14.

### Key takeaways
- Whatever you do to one side, do to the other.
- Always check your answer by substituting it back.
- Only like terms can be combined.

## module: Functions and Graphs
video: 52tpYl2tTqk

### Introduction
A function describes how one quantity depends on another. Graphs show that relationship visually, making patterns easy to see.

### Key concepts
- **Function:** each input gives exactly one output.
- **Domain and range:** allowed inputs and resulting outputs.
- **Linear functions:** y = mx + c, a straight line.
- **Quadratic functions:** y = ax² + bx + c, a parabola.
- **Vertical line test:** checks whether a graph is a function.

### Definitions
- **Slope (m):** the rate of change of a line.
- **Intercept:** where a graph crosses an axis.
- **Domain:** the set of possible inputs.
- **Range:** the set of possible outputs.

### Example
A taxi charges ₹50 plus ₹15 per kilometre:
```text
f(d) = 15d + 50
f(10) = 15(10) + 50 = ₹200
```

### Key takeaways
- `f(x)` means the output of f for the input x.
- Slope tells you how fast something is changing.
- Graphing a function quickly reveals its behaviour.

## module: Differential Calculus
video: WsQQvHm4lSw

### Introduction
Differential calculus studies how quantities change. The derivative gives an instantaneous rate of change, such as a car's speed at one exact moment.

### Key concepts
- **Limits:** the value a function approaches.
- **Derivative:** the slope of a function at a point.
- **Rules:** power, product, quotient and chain rules.
- **Optimization:** a maximum or minimum occurs where the derivative is zero.

### Definitions
- **Derivative:** the instantaneous rate of change, written `dy/dx`.
- **Tangent:** a line touching a curve at a point.
- **Critical point:** where the derivative is zero or undefined.

### Example
If position is `s = t²`, then velocity is its derivative:
```text
ds/dt = 2t
At t = 3 seconds, velocity = 6 m/s
```
Power rule: the derivative of xⁿ is n xⁿ⁻¹.

### Key takeaways
- A derivative is a slope and a rate of change.
- Set the derivative to zero to find maximums and minimums.
- Velocity is the derivative of position, and acceleration of velocity.

## module: Integral Calculus
video: tB3wJ-bwdZc

### Introduction
Integration is the reverse of differentiation. It adds up tiny pieces to find totals, such as the area under a curve or the distance travelled.

### Key concepts
- **Indefinite integral:** a family of functions, plus a constant C.
- **Definite integral:** a number found between two limits.
- **Fundamental Theorem of Calculus:** links integrals and derivatives.
- **Techniques:** substitution and integration by parts.

### Definitions
- **Antiderivative:** a function whose derivative is the given function.
- **Constant of integration:** the unknown constant, C.
- **Limits of integration:** the start and end values.

### Example
```text
Integral of 2x dx = x² + C
From 0 to 3: 3² - 0² = 9
```
So the area under `y = 2x` from 0 to 3 is 9. Power rule: the integral of xⁿ is xⁿ⁺¹/(n+1) + C.

### Key takeaways
- Don't forget +C on indefinite integrals.
- A definite integral equals the upper-limit value minus the lower-limit value.
- Integrating velocity over time gives displacement.

## module: Matrices and Determinants
video: ZFVFtVbeerI

### Introduction
A matrix is a rectangular table of numbers. Matrices organize data and solve systems of equations, and they're used heavily in graphics and machine learning.

### Key concepts
- **Order:** rows x columns.
- **Operations:** addition, and scalar and matrix multiplication.
- **Determinant:** a number computed from a square matrix.
- **Inverse:** exists only if the determinant isn't zero.

### Definitions
- **Square matrix:** the same number of rows and columns.
- **Identity matrix:** 1s on the diagonal and 0s elsewhere.
- **Transpose:** rows and columns swapped.
- **Singular matrix:** a matrix whose determinant is zero.

### Example
```text
A = | 3  2 |
    | 1  4 |
det(A) = (3)(4) - (2)(1) = 10
```
Because the determinant isn't zero, A has an inverse.

### Key takeaways
- Matrix multiplication isn't commutative: AB usually differs from BA.
- To multiply, the columns of the first must equal the rows of the second.
- A zero determinant means no unique solution.

## module: Probability and Statistics
video: _Pyi12dn4Kw

### Introduction
Statistics summarizes and interprets data, while probability measures uncertainty. Together they support decisions in science, business and machine learning.

### Key concepts
- **Averages:** mean, median and mode.
- **Spread:** range, variance and standard deviation.
- **Probability:** favourable outcomes divided by total outcomes.
- **Normal distribution:** the bell curve.
- **Correlation:** how two variables move together.

### Definitions
- **Population:** the entire group of interest.
- **Sample:** a subset of a population.
- **Independent events:** one doesn't affect the other.
- **Outlier:** a value far from the rest.

### Example
Marks of 10, 20 and 90:
```text
Mean = 40, Median = 20
```
The outlier 90 pulls the mean up, so the median better represents a typical value. The probability of rolling a 6 is 1/6.

### Key takeaways
- Use the median when data contains outliers.
- Correlation doesn't prove causation.
- A sample must be representative to be useful.

## module: Linear Algebra
video: 5oZ84mlt7tM

### Introduction
Linear algebra studies vectors and the transformations that act on them. It's the mathematical language of computer graphics, data science and machine learning.

### Key concepts
- **Vectors:** have magnitude and direction.
- **Dot product:** measures how aligned two vectors are.
- **Linear transformations:** matrices that rotate, scale or shear space.
- **Eigenvectors:** directions that a transformation only scales.
- **Systems of equations:** written as Ax = b.

### Definitions
- **Vector space:** a set of vectors closed under addition and scaling.
- **Basis:** the minimal set of vectors describing a space.
- **Rank:** the number of independent rows or columns.
- **Eigenvalue:** the factor by which an eigenvector is scaled.

### Example
```text
a = (1, 2), b = (3, 4)
a . b = (1)(3) + (2)(4) = 11
```
A dot product of zero means the vectors are perpendicular.

### Key takeaways
- A matrix is best thought of as a transformation of space.
- PCA in machine learning is built on eigenvectors.
- Visual understanding makes the formulas far easier.

## module: Mathematical Applications and Problem Solving
video: yHYMArAVDWY

### Introduction
Applied mathematics means turning real problems into mathematical models and interpreting the answers. A clear method matters as much as knowing formulas.

### Key concepts
- **Pólya's four steps:** understand the problem, make a plan, carry it out, look back.
- **Mathematical modelling:** represent a situation using equations.
- **Assumptions:** simplify the problem, and state them clearly.
- **Estimation:** check that an answer is sensible.

### Definitions
- **Model:** a simplified mathematical description of a situation.
- **Variable:** a quantity that can change.
- **Optimization:** finding the best possible value.

### Example
A rectangular garden needs the largest area from 40 m of fencing. Using sides x and 20 - x:
```text
A = x(20 - x) = 20x - x²
dA/dx = 20 - 2x = 0, so x = 10
```
The best shape is a 10 m square, with an area of 100 m².

### Key takeaways
- Define your variables before calculating.
- Check units and whether the answer makes sense.
- Practise on a wide range of problems to build judgement.
