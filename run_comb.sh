# Program : run_comb.sh
# Description : Run combination of DSM and CoreScope

java -cp "./CoreScope-1.0.jar:./library/commons-math3-3.2.jar" corescope.anomaly.DSM $@
