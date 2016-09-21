/* =================================================================================
 *
 * CoreScope: Graph Mining Using k-Core Analysis - Patterns, Anomalies, and Algorithms
 * Authors: Kijung Shin, Tina Eliassi-Rad, and Christos Faloutsos
 *
 * Version: 1.0
 * Date: May 24, 2016
 * Main Contact: Kijung Shin (kijungs@cs.cmu.edu)
 *
 * This software is free of charge under research purposes.
 * For commercial purposes, please contact the author.
 *
 * =================================================================================
 */

package corescope.influence;

/**
 * Compute Eigenvector Centrality Using Power Iteration
 * @author Kijung Shin
 */
public class EigenvectorCentrality {

    public static double[] run(int[][] graph) {

        int n = graph.length;
        double[] centralities = new double[n];
        for(int i=0; i<n; i++) {
            centralities[i] = 1;
        }

        double max = 0;
        while(true) {
            double[] temp = new double[n];
            max = 0;
            double changeSum = 0;
            for(int i=0; i<n; i++) {
                for(int trg : graph[i]) {
                    temp[i] += centralities[trg];
                }
                max = Math.max(max, temp[i]);
            }
            for(int i=0; i<n; i++) {
                changeSum += Math.abs(centralities[i] - temp[i]/max);
                centralities[i] = temp[i]/max;
            }

            if (changeSum < 0.00000001) {
                break;
            }
        }
        return centralities;
    }
}
