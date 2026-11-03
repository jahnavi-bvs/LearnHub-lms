# course: Artificial Intelligence & Machine Learning

## module: Introduction to AI and Machine Learning
video: ukzFI9rgwfU

### Introduction
Artificial intelligence is the broad goal of making machines perform tasks that seem intelligent. Machine learning achieves this by learning patterns from data rather than following hand-written rules.

### Key concepts
- **AI, ML and deep learning:** deep learning is part of ML, and ML is part of AI.
- **Supervised learning:** learns from labelled examples.
- **Unsupervised learning:** finds patterns in unlabelled data.
- **Reinforcement learning:** learns through rewards and penalties.

### Definitions
- **Model:** the result of training, used to make predictions.
- **Training:** the process of learning from data.
- **Feature:** an input variable.
- **Label:** the correct answer in training data.

### Example
A spam filter trains on emails marked "spam" or "not spam". It learns which patterns indicate spam, then predicts labels for new emails without anyone writing explicit rules.

### Key takeaways
- ML is useful when rules are too complex to write by hand.
- Better data usually improves a model more than a cleverer algorithm.
- A model is only as fair and accurate as its data.

## module: Python for Machine Learning
video: 7eh4d6sabA0

### Introduction
Python dominates machine learning because of its readable syntax and strong libraries. This module introduces the tools used in almost every ML project.

### Key concepts
- **NumPy:** fast arrays and mathematics.
- **pandas:** tables of data called DataFrames.
- **Matplotlib:** charts and plots.
- **scikit-learn:** ready-made ML algorithms.
- **Jupyter Notebook:** run code in cells and view results immediately.

### Definitions
- **DataFrame:** a table with labelled rows and columns.
- **Array:** a grid of same-typed numbers.
- **Fit and predict:** train a model, then use it.

### Example
```python
import pandas as pd
from sklearn.tree import DecisionTreeClassifier

data = pd.read_csv("music.csv")
X = data.drop(columns=["genre"])
y = data["genre"]
model = DecisionTreeClassifier().fit(X, y)
print(model.predict([[21, 1]]))
```

### Key takeaways
- Most scikit-learn models share the same `fit` and `predict` pattern.
- Explore data in pandas before training anything.
- Use a virtual environment to manage ML packages.

## module: Mathematics and Statistics for ML
video: 1VSZtNYMntM

### Introduction
You can use ML libraries without much maths, but understanding the basics helps you choose models and debug them. Four areas matter most.

### Key concepts
- **Linear algebra:** data is stored as vectors and matrices.
- **Calculus:** gradients show how to adjust a model to reduce error.
- **Probability:** expresses uncertainty in predictions.
- **Statistics:** mean, variance and distributions describe data.

### Definitions
- **Vector:** an ordered list of numbers.
- **Gradient:** the direction of steepest increase of a function.
- **Standard deviation:** how spread out values are.
- **Loss function:** a measure of a model's error.

### Example
Gradient descent trains models by repeatedly moving downhill on the loss:
```text
new_weight = weight - learning_rate * gradient
```
Too large a learning rate overshoots the minimum, and too small trains very slowly.

### Key takeaways
- Training is mostly about minimizing a loss function.
- Learn concepts first; libraries handle the detailed calculations.
- Always check the mean and spread of your data before modelling.

## module: Data Preprocessing and Data Visualization
video: Q7DTR5AwphA

### Introduction
Real data is messy. Preparing it often takes most of a project's time, and visualization helps you spot problems before they affect a model.

### Key concepts
- **Missing values:** remove them or fill them in.
- **Encoding:** turn categories into numbers, for example with one-hot encoding.
- **Scaling:** put features on a similar range.
- **Train/test split:** hold back data to evaluate honestly.
- **Plots:** histograms, scatter plots and box plots.

### Definitions
- **Outlier:** a value far from the others.
- **Normalization:** rescaling values to a fixed range such as 0 to 1.
- **Data leakage:** test information accidentally influencing training.

### Example
```python
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)
scaler = StandardScaler().fit(X_train)
X_train = scaler.transform(X_train)
X_test = scaler.transform(X_test)
```

### Key takeaways
- Fit preprocessing on training data only, to avoid leakage.
- Plot your data before trusting it.
- Clean data matters more than a complex model.

## module: Supervised Learning Algorithms
video: E0Hmnixke2g

### Introduction
Supervised learning trains on examples with known answers. It solves two main problems: predicting a number and predicting a category.

### Key concepts
- **Regression:** predicts numbers, using linear regression.
- **Classification:** predicts categories, using logistic regression.
- **Decision trees and random forests:** a series of yes-or-no splits.
- **k-nearest neighbours:** predicts from the closest examples.
- **Evaluation:** accuracy, precision, recall and mean squared error.

### Definitions
- **Overfitting:** memorizing training data and failing on new data.
- **Underfitting:** too simple to capture the pattern.
- **Precision:** of predicted positives, how many were correct.
- **Recall:** of actual positives, how many were found.

### Example
For a disease test, a model that says "healthy" for everyone may be 99% accurate if the disease is rare, but it finds no patients. Recall reveals this failure.

### Key takeaways
- Always evaluate on data the model has not seen.
- Accuracy can mislead when classes are imbalanced.
- Try a simple model first as a baseline.

## module: Unsupervised Learning Algorithms
video: D6gtZrsYi6c

### Introduction
Unsupervised learning finds structure in data with no labels. It is useful for exploring data and for grouping things when no one has defined categories.

### Key concepts
- **Clustering:** k-means groups similar items together.
- **Dimensionality reduction:** PCA compresses many features into fewer.
- **Association rules:** find items that occur together.
- **Choosing k:** the elbow method helps pick the number of clusters.

### Definitions
- **Cluster:** a group of similar data points.
- **Centroid:** the centre point of a cluster.
- **PCA (Principal Component Analysis):** a method that keeps the directions with the most variation.

### Example
```python
from sklearn.cluster import KMeans

model = KMeans(n_clusters=3, n_init=10)
groups = model.fit_predict(customer_data)
```
A shop might discover three customer segments and then market to each differently.

### Key takeaways
- There are no labels, so judging quality is harder and needs domain knowledge.
- Scale features first, because k-means uses distances.
- Clustering results still need human interpretation.

## module: Neural Networks and Deep Learning
video: aircAruvnKk

### Introduction
Neural networks stack many simple units to learn complex patterns. Deep learning uses networks with many layers, and it powers image recognition, speech and language models.

### Key concepts
- **Layers:** input, hidden and output.
- **Weights:** the numbers learned during training.
- **Activation functions:** such as ReLU, which let networks learn non-linear patterns.
- **Backpropagation:** works out how each weight affected the error.
- **Specialized networks:** CNNs for images, and transformers for language.

### Definitions
- **Neuron:** a unit that combines inputs and produces an output.
- **Epoch:** one full pass through the training data.
- **Learning rate:** the size of each weight update.

### Example
To recognize handwritten digits, the input layer receives 784 pixel values, hidden layers combine them into edges and shapes, and the output layer gives a score for each digit from 0 to 9.

### Key takeaways
- Deep learning needs a lot of data and computing power.
- Watch for overfitting using validation data.
- For small, table-shaped data, simpler models often work better.

## module: AI Applications and Machine Learning Projects
video: Hr06nSA-qww

### Introduction
A real ML project is more than training a model. It involves defining the problem, preparing data, evaluating honestly and putting the model to use.

### Key concepts
- **Project steps:** define, collect, clean, explore, train, evaluate, deploy.
- **Baseline:** a simple model to beat.
- **Deployment:** serving predictions through an API.
- **Monitoring:** models get worse as the world changes.
- **Ethics:** check for bias, privacy issues and explainability.

### Definitions
- **Baseline model:** the simplest reasonable approach.
- **Inference:** using a trained model to make predictions.
- **Model drift:** declining performance as data changes.

### Example
For predicting house prices: start with linear regression as a baseline, measure its average error, try a random forest, compare the results on test data, and deploy the better model behind an API.

### Key takeaways
- Start from a clear question and a measure of success.
- Use public datasets, such as those on Kaggle, for practice.
- Document your data, choices and results.
