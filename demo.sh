rm -rf output_demo
mkdir output_demo
echo [DEMO] running anomaly detection using CoreA...
./run_coreA.sh example_graph.tsv output_demo/coreA_result.tsv
echo [DEMO] anomaly scores are saved in output_demo/coreA_result.tsv
echo [DEMO] running anomaly detection using DSM+CoreA...
./run_comb_coreA.sh example_graph.tsv output_demo/comb_coreA_result.tsv 1
echo [DEMO] detected anomalous nodes are saved in output_demo/comb_coreA_result.tsv
echo [DEMO] running anomaly detection using TrussA...
./run_trussA.sh example_graph.tsv output_demo/trussA_result.tsv
echo [DEMO] anomaly scores are saved in output_demo/trussA_result.tsv
echo [DEMO] running anomaly detection using DSM+TrussA...
./run_comb_trussA.sh example_graph.tsv output_demo/comb_trussA_result.tsv 1
echo [DEMO] detected anomalous nodes are saved in output_demo/comb_trussA_result.tsv
echo [DEMO] running CoreD...
./run_coreD.sh example_graph.tsv overall 0.1
echo [DEMO] estimated degeneracy is printed
echo [DEMO] running influential spreader identification using CoreS...
./run_coreS.sh example_graph.tsv output_demo/coreS_result.tsv 10
echo [DEMO] list of influential spreaders is saved in output_demo/coreS_result.tsv
echo [DEMO] running SIR simulation with identified spreaders
./run_simulation.sh example_graph.tsv output_demo/simulation_result.tsv 10 0.01 100
echo [DEMO] simulation result is saved in output_demo/simulation_result.tsv
