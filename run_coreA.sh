# Program : run_coreA.sh
# Description : Run CoreA, an anomaly detection algorithm based on core decomposition

java -cp "./CoreScope-2.0.jar:./library/commons-math3-3.2.jar" corescope.anomaly.CoreA $@
