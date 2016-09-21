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
package corescope.anomaly;

/**
 * Compute the degrees of vertices
 * @author Kijung Shin
 */
class Degree {

    /**
     * compute the degrees of vertices in the given graph
     * @param graph input graph
     * @return degrees of vertices (vertex index -> degree)
     */
    static int[] run(int[][] graph) {

        final int n = graph.length;
        final int[] degrees = new int[n];
        for(int i=0; i<n; i++) {
            degrees[i] = graph[i].length;
        }

        return degrees;
    }

}
