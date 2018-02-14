CoreScope: Graph Mining Using k-Core Analysis - Patterns, Anomalies and Algorithms
========================
**CoreScope** is a set of algorithms based on the empirical patterns related to k-coresxs in real-world graphs.
**CoreScope** consists of the following algorithms:
 * *Core-A*: anomaly detection algorithm based on Mirror Pattern
 * *Truss-A*: anomaly detection algorithm based on Truss Mirror Pattern
 * *Core-D*: streaming algorithm for degeneracy based on Core-Triangle Pattern
 * *Core-S*: influential spreader detection method based on Structured Core Pattern

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
If you use this code as part of any published research, please acknowledge the following papers.
```
@inproceedings{shin2016corescope,
  author    = {Kijung Shin and Tina Eliassi-Rad and Christos Faloutsos},
  title     = {CoreScope: Graph Mining Using k-Core Analysis - Patterns, Anomalies and Algorithms},
  booktitle = {ICDM},
  pages     = {469--478},
  year      = {2016}
}

@article{shin2018pattern,
  author={Kijung Shin and Tina Eliassi-Rad and Christos Faloutsos},
  title={Patterns and Anomalies in k-Cores of Real-World Graphs with Applications},
  journal={Knowledge and Information Systems},
  volume={54},
  number={3},
  pages={677--710},
  year={2018},
  issn={0219-3116},
  doi={10.1007/s10115-017-1077-6},
  publisher={Springer}
}
```
