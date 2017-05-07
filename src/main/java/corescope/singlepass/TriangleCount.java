/* =================================================================================
 *
 * CoreScope: Graph Mining Using k-Core Analysis - Patterns, Anomalies, and Algorithms
 * Authors: Kijung Shin, Tina Eliassi-Rad, and Christos Faloutsos
 *
 * Version: 1.1
 * Date: May 24, 2016
 * Main Contact: Kijung Shin (kijungs@cs.cmu.edu)
 *
 * This software is free of charge under research purposes.
 * For commercial purposes, please contact the author.
 *
 * =================================================================================
 */
package corescope.singlepass;

import java.util.Arrays;

/**
 * In-memory Triangle Counting
 * @author Kijung Shin
 */
class TriangleCount {

    /**
     * count the number of triangles in a given graph
     * @param graph
     * @return
     */
    public static long run(int[][] graph){

        for(int src = 0; src<graph.length; src++) {
            if(graph[src] != null) {
                Arrays.sort(graph[src]);
            }
        }

        // number of triangles
        long count = 0;
        for(int src=graph.length-1; src>=0; src--) {

            if(graph[src] == null) {
                continue;
            }

            int srcLen = graph[src].length;
            int[] srcNeigbors = graph[src];
            for(int trg : graph[src]) {
                int i=0;
                int j=0;

                if(graph[trg] == null) {
                    continue;
                }

                int trgLen = graph[trg].length;
                int[] trgNeighbors = graph[trg];

                while(i < srcLen && j < trgLen) {
                    if(srcNeigbors[i] == trgNeighbors[j]) {
                        count++;
                        i++;
                        j++;
                    }
                    else if(srcNeigbors[i] > trgNeighbors [j]) {
                        j++;
                    }
                    else {
                        i++;
                    }
                }
            }

        }

        return count;
    }

}
