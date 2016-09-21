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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Export a graph to a file
 * @author Kijung Shin
 */
public class Export {

    /**
     *
     * @param graph
     * @param path
     * @param delim delimiter
     * @throws IOException
     */
    public static void run(final int[][] graph, final String path, final String delim, final boolean verbose) throws IOException {

        if(verbose)
            System.err.println("Exporting graph... "+ path);
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        for(int src=0; src<graph.length; src++) {
            for(int trg : graph[src]) {
                bw.write(src + delim + trg);
                bw.newLine();
            }
        }
        bw.close();
        if(verbose)
            System.err.println("Graph was exported.: "+ path);

    }
}
