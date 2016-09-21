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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Run SIR simulation
 * @author Kijung Shin
 */
public class SIRSimulation {

    /**
     * Main function
     * @param args  input_path, output_path, num_of_spreaders, infection_rate, repetition_num
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if(args.length != 5) {
            printError();
            System.exit(-1);
        }

        final String input = args[0];
        final String delim = "\t";
        final String output = args[1];
        final int numOfSpreaders = Integer.valueOf(args[2]);
        final double infectedRatio = Double.valueOf(args[3]);
        final int repetition = Integer.valueOf(args[4]);
        final Random random = new Random();

        final int[][] graph = Import.load(input, delim);
        final int[] seeds = IdentifySpreaders.run(graph, numOfSpreaders);
        run(graph, seeds, output, infectedRatio, repetition, random);


    }

    private static void printError() {
        System.err.println("Usage: run_SIR_simulation.sh input_path output_path num_of_spreaders, infection_rate, repetition_num");
        System.err.println("Infection_rate should be in (0,1]");
        System.err.println("Repetition_num should be an integer greater than 0");
    }

    /**
     * run SIR simulation with each of given seeds
     * @param graph input graph
     * @param output output file path
     * @param infectionRatio    probability that an infected node infects each of its neighbors
     * @param repetition    number of repetitions of simluation for each seed
     * @param random    random number generator
     */
    public static void run(final int[][] graph, final int[] seeds, final String output, final double infectionRatio, final int repetition, final Random random) throws IOException {

        int[][] seedsPool = new int[seeds.length][1];
        for(int i=0; i<seeds.length; i++) {
            seedsPool[i][0] = seeds[i];
        }
        double[] influence = runSimulation(graph, infectionRatio, seedsPool, repetition, random);
        writeResults(output, "\t", seeds, influence);
    }

    /**
     * write results to a file
     *
     * @param output    output file path
     * @param delim delimiter used in the output file
     * @param seeds   list of nodes used as seeds
     * @param influence   influence of each seed
     * @throws IOException
     */
    private static void writeResults(final String output, final String delim, final int[] seeds, final double[] influence) throws IOException {
        final BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        bw.write("vertex_index" + delim + "influence");
        bw.newLine();
        for(int i=0; i<seeds.length; i++) {
            bw.write("" + seeds[i] + delim + influence[i]);
            bw.newLine();
        }
        bw.close();
    }

    /**
     * Run SIR simluation using given set of seeds
     * @param graph input graph
     * @param infectionRatio    probability that an infected node infects each of its neighbors
     * @param seedSets  set of seeds
     * @param repetition    number of repetitions of simluation for each seed set
     * @param random    random number generator
     *
     * @return influence of each seed set
     */
    public static double[] runSimulation(final int[][] graph, final double infectionRatio, final int[][] seedSets, final int repetition, final Random random) {


        int n = graph.length;
        int seedSetNum = seedSets.length;
        double[] influence = new double[seedSetNum];
        for(int seedSetIndex = 0; seedSetIndex < seedSetNum; seedSetIndex++) {

            int[] seedSet = seedSets[seedSetIndex];
            for (int rep = 0; rep < repetition; rep++) {
                int infectedNum = 0;
                Queue<Integer> queue = new LinkedList();
                final int[] state = new int[n]; // 0: susceptible, 1: infected, 2: recovered
                for (int seed : seedSet) {
                    state[seed] = 1;
                    queue.add(seed);
                    infectedNum++;
                }

                while (!queue.isEmpty()) {
                    final Queue<Integer> newQueue = new LinkedList();
                    while (!queue.isEmpty()) {
                        int i = queue.poll();
                        for (int j : graph[i]) {
                            if (state[j] == 0) { //susceptible
                                if (random.nextDouble() < infectionRatio) {
                                    state[j] = 1;
                                    infectedNum++;
                                    newQueue.add(j);
                                }
                            }
                        }
                        state[i] = 2;
                    }
                    queue = newQueue;
                }
                influence[seedSetIndex] += infectedNum;
            }
            influence[seedSetIndex] /= repetition;
        }

        return influence;
    }
}
