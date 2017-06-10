/* =================================================================================
 *
 * CoreScope: Graph Mining Using k-Core Analysis - Patterns, Anomalies, and Algorithms
 * Authors: Kijung Shin, Tina Eliassi-Rad, and Christos Faloutsos
 *
 * Version: 2.0
 * Date: Mar 9, 2017
 * Main Contact: Kijung Shin (kijungs@cs.cmu.edu)
 *
 * This software is free of charge under research purposes.
 * For commercial purposes, please contact the author.
 *
 * =================================================================================
 */

package corescope.anomaly;

import corescope.CoreDecomp;
import corescope.Import;
import corescope.TrussDecomp;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.stat.ranking.NaturalRanking;
import org.apache.commons.math3.stat.ranking.RankingAlgorithm;
import org.apache.commons.math3.stat.ranking.TiesStrategy;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Anomaly detection based on core decomposition
 * @author Kijung Shin
 */
public class CoreA {

    /**
     * Main function
     * @param args  input_path, output_path
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if(args.length != 2) {
            printError();
            System.exit(-1);
        }

        final String input = args[0];
        final String output = args[1];

        run(Import.load(input, "\t"), output, false);
    };

    private static void printError() {
        System.err.println("Usage: run_coreA.sh input_path output_path");
    }

    /**
     * run anomaly detection algorithm based on core or truss decomposition
     * @param graph input graph
     * @param output path of the output file
     * @throws IOException
     */
    public static void run(int[][] graph, String output, boolean useTruss) throws IOException {

        final int n = graph.length;
        final int[] coreness = useTruss ? TrussDecomp.run(graph, false) : CoreDecomp.run(graph, false);
        final int[] degree = Degree.run(graph);

        final RankingAlgorithm rankAlgoSeq = new NaturalRanking(NaNStrategy.FAILED, TiesStrategy.SEQUENTIAL);
        final double[] anomaly = getAnomalyScore(degree, coreness);
        final double[] anomalyRankSeq = rankAlgoSeq.rank(anomaly);

        final int[] orderedIndices = new int[n];
        for(int i = 0; i < n; i++) {
            orderedIndices[(int)anomalyRankSeq[i]-1] = i;
        }

        final RankingAlgorithm rankAlgoAvg = new NaturalRanking(NaNStrategy.FAILED, TiesStrategy.AVERAGE);
        final double[] anomalyRankAvg = rankAlgoAvg.rank(anomaly);

        // write results in descending order of anomaly score
        final BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        bw.write("rank" + "\t" + "vertex_index" + "\t" + "anomaly_score" + "\t" + (useTruss ? "trussness" : "coreness") + "\t" + "degree");
        bw.newLine();
        for(int i = n - 1; i >= 0; i--) {
            final int index = orderedIndices[i];
            bw.write((n-anomalyRankAvg[index]+1) + "\t" + index + "\t" + anomaly[index] + "\t" + coreness[index] + "\t" + degree[index]);
            bw.newLine();
        }
        bw.close();

    }

    /**
     * compute anomaly score from the given degrees and corenesses of nodes
     * @param degree degrees of nodes
     * @param coreness corenesses of nodes
     * @return anomaly score of nodes
     */
    public static double[] getAnomalyScore(int[] degree, int[] coreness) {

        int n = degree.length;
        final int[] corenessWithDegree = new int[n];
        for(int i=0; i<n; i++) {
            corenessWithDegree[i] = coreness[i] * n + degree[i];
        }
        final RankingAlgorithm rankAlgoAvg = new NaturalRanking(NaNStrategy.FAILED, TiesStrategy.AVERAGE);
        final double[] corenessRank = rankAlgoAvg.rank(transform(corenessWithDegree));
        final double[] degreeRank = rankAlgoAvg.rank(transform(degree));

        //compute anomaly scores
        final double[] anomaly = new double[n];
        for(int i = 0; i < n; i++) {
            anomaly[i] = Math.abs(Math.log(n-degreeRank[i]+1) - Math.log(n-corenessRank[i]+1));
        }

        return anomaly;
    }

    /**
     * transform an int array to a double array
     * @param values
     * @return
     */
    private static double[] transform(int[] values) {
        double[] result = new double[values.length];
        for(int i = 0; i < values.length; i++) {
            result[i] = values[i];
        }
        return result;
    }

}
