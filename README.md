CoreScope: Graph Mining Using k-Core Analysis - Patterns, Anomalies and Algorithms
========================
**CoreScope** is a set of algorithms based on empirical patterns related to k-cores in real-world graphs.
**CoreScope** consists of the following algorithms:
 * *CoreA*: anomaly detection algorithm based on Mirror Pattern
 * *CoreD*: streaming algorithm for degeneracy based on Core-Triangle Pattern
 * *CoreS*: influential spreader detection method based on Structured Core Pattern

Datasets
========================
The download links for the datasets used in the paper are [here](http://www.cs.cmu.edu/~kijungs/codes/kcore/)

Building and Running CoreScope
========================
Please see [User Guide](user_guide.pdf)

Running Demo
========================
For demo, please type 'make'

Reference
========================
If you use this code as part of any published research, please acknowledge the following paper.
```
@inproceedings{shin2016corescope,
  author    = {Kijung Shin and Tina Eliassi-Rad and Christos Faloutsos},
  title     = {CoreScope: Graph Mining Using k-Core Analysis - Patterns, Anomalies and Algorithms},
  booktitle = {ICDM},
  year      = {2016}
}
```
