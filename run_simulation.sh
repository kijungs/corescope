# Program : run_simulation.sh
# Description : Run SIR simulation with the seeds identified by the proposed influential spreader identification algorithm

java -cp "./CoreScope-2.0.jar:./library/commons-math3-3.2.jar" corescope.influence.SIRSimulation $@
