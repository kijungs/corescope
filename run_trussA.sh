# Program : run_trussA.sh
# Description : Run Truss-A, an anomaly detection algorithm based on truss decomposition

java -cp ./CoreScope-2.0.jar:./library/commons-math3-3.2.jar corescope.anomaly.TrussA $@
