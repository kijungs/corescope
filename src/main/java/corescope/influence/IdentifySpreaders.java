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

import corescope.CoreDecomp;
import corescope.Import;
import corescope.KCore;
import corescope.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.PriorityQueue;

/**
 * Influential Spreader Detection Algorithm based on Core Decomposition
 * @author Kijung Shin
 */
public class IdentifySpreaders {

    /**
     * Main function
     * @param args  input_path, output_path, output_num
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if(args.length != 3) {
            printError();
            System.exit(-1);
        }

        final String input = args[0];
        final String output = args[1];
        final int numOfSpreaders = Integer.valueOf(args[2]);

        writeResults(output, "\t", run(Import.load(input, "\t"), numOfSpreaders));
    };

    /**
     * run influential spreader detection algorithm based on core decomposition
     * @param graph input graph
     * @param numOfSpreaders number of spreaders to detect
     * @throws IOException
     */
    public static int[] run(int[][] graph, int numOfSpreaders) throws IOException {
        final Pair<int[][], int[]> pair = KCore.getDegeneracyCore(graph, CoreDecomp.run(graph, false));
        final int[][] core = pair.getKey();
        final int[] originalIndex = pair.getValue();
        final int[] spreadersInCoreIndex = topK(EigenvectorCentrality.run(core), numOfSpreaders);
        final int[] spreadersInOriginalIndex = new int[spreadersInCoreIndex.length];
        for(int i=0; i<spreadersInCoreIndex.length; i++) {
            spreadersInOriginalIndex[i] = originalIndex[spreadersInCoreIndex[i]];
        }
        return spreadersInOriginalIndex;
    }

    private static void printError() {
        System.err.println("Usage: run_coreS.sh input_path output_path num_of_spreaders");
        System.err.println("num_of_spreaders should be greater than or equal to 1");
    }

    /**
     * write results to a file
     *
     * @param output    output file path
     * @param delim delimiter used in the output file
     * @param spreaders   list of spreaders
     * @throws IOException
     */
    private static void writeResults(final String output, final String delim, final int[] spreaders) throws IOException {
        final BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        bw.write("rank" + delim + "vertex_index");
        bw.newLine();
        for(int i=0; i<spreaders.length; i++) {
            bw.write((i+1) + delim + spreaders[i]);
            bw.newLine();
        }
        bw.close();
    }

    /**
     * find the indices of the largest k values among candidates
     * @param values    values
     * @param k number of values
     * @return indices
     */
    private static int[] topK(double[] values, int k) {
        final PriorityQueue<ComparablePair<Integer, Double>> priorityQueue = new PriorityQueue();
        for(int i=0; i<values.length; i++) {
            priorityQueue.add(new ComparablePair(i, - values[i]));
        }
        final int[] result = new int[k];
        for(int i=0; i<k; i++) {
            final int index = priorityQueue.poll().getKey();
            result[i] = index;
        }
        return result;
    }

}
