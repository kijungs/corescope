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
    static long run(int[][] graph){

        //sort neighbors in the increasing order of index
        for(int src = 0; src<graph.length; src++) {
            Arrays.sort(graph[src]);
        }

        // number of triangles
        long count = 0;

        for(int src=graph.length-1; src>=0; src--) {

            final int srcLen = graph[src].length;
            final int[] srcNeigbors = graph[src];
            for(int trg : graph[src]) {
                int i=0;
                int j=0;
                final int trgLen = graph[trg].length;
                final int[] trgNeighbors = graph[trg];

                if(src < trg) {
                    while(i < srcLen && j < trgLen) {
                        if(srcNeigbors[i] == trgNeighbors[j]) {
                            if(trg < srcNeigbors[i]) {
                                count++;
                            }
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
        }

        return count;
    }

}
