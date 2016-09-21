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

package corescope;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Compute k-cores
 * @author Kijung Shin
 */
public class KCore {

    /**
     * get the degeneracy-core
     * @param graph input graph
     * @param corenesses    corenesses of verticies
     * @return  degeneracy-core, indexToOriginal (index in the degeneracy-core -> index in the entire graph)
     */
    public static Pair<int[][], int[]> getDegeneracyCore(final int[][] graph, final int[] corenesses) {
        int maxK = 0;
        for(int coreNum : corenesses) {
            if(maxK < coreNum) {
                maxK = coreNum;
            }
        }
        return getKCore(graph, corenesses, maxK);
    }

    /**
     * get the k-core
     * @param graph input graph
     * @param corenesses    corenesses of verticies
     * @param k
     * @return k-core, indexToOriginal (index in the k-core -> index in the entire graph)
     */
    public static Pair<int[][], int[]> getKCore(final int[][] graph, final int[] corenesses, final int k) {

        final LinkedList<Integer> nodes = new LinkedList();
        for(int src = 0; src < graph.length; src++){
            if(corenesses[src] >= k){
                nodes.add(src);
            }
        }
        Collections.sort(nodes);

        final int newN = nodes.size();
        final int[] indexToOriginal = new int[newN];
        final HashMap<Integer, Integer> indexToNewIndex = new HashMap();
        while(nodes.size() > 0) {
            final int node = nodes.pop();
            final int newINdex = indexToNewIndex.size();
            indexToNewIndex.put(node, newINdex);
            indexToOriginal[newINdex] = node;
        }

        final int[][] newGraph = new int[newN][];
        for(int src = 0; src < graph.length; src++){
            if(corenesses[src] >= k){
                int count = 0;
                for(int trg: graph[src]){
                    if(corenesses[trg] >= k) {
                        count++;
                    }
                }
                newGraph[indexToNewIndex.get(src)] = new int[count];
                count = 0;
                for(final int trg: graph[src]){
                    if(corenesses[trg] >= k) {
                        newGraph[indexToNewIndex.get(src)][count++] = indexToNewIndex.get(trg);
                    }
                }
            }
        }

        return new Pair(newGraph, indexToOriginal);

    }
}
