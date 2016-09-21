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
import java.util.*;

/**
 * Core Decomposition
 * @author Kijung Shin
 */
public class CoreDecomp {

    public static void main(String[] ar) throws IOException {

        if(ar.length < 2) {
            printError();
            System.exit(-1);
        }

        String input = ar[0];
        String output = ar[1];
        String delim = "\t";

        if(ar.length == 3) {
            if (ar[2].compareToIgnoreCase("tab") == 0) {
                delim = "\t";
            } else if (ar[2].compareToIgnoreCase("space") == 0){
                delim = " ";
            } else if(ar[2].compareToIgnoreCase("comma") == 0) {
                delim = ",";
            } else {
                System.err.println("Unknown Delimeter");
                printError();
                System.exit(-1);
            }
        }

        System.err.println("Input Path: " + input);
        System.err.println("Ouput Path: " + output);
        System.err.println("Delimeter: " + ar[2]);

        final int[][] graph = Import.loadLarge(input, delim);
        export(CoreDecomp.run(graph, true), output, "\t");

    }

    public static void printError() {
        System.err.println("Usage: run_core_decomp.sh input_path output_path delimiter");
        System.err.println("delimiter should be one of [tab, space, comma]");
    }

    /**
     * compute the corenesses of vertices in the given graph
     * @param graph input graph
     * @param verbose whether to print progress
     * @return corenesses of vertices (vertex index -> coreness)
     */
    public static int[] run(int[][] graph, final boolean verbose) {

        if(verbose)
            System.err.println("computing core decomposition...");

        final int n = graph.length;
        final int[] upper = new int[n];
        final int[] corenesses = new int[n];
        final Set<Integer> S = new HashSet<Integer>(n);
        for(int i=0; i<n; i++) {
            upper[i] = graph[i].length;
            S.add(i);
        }

        for(int k=1;;k++) {

            if(verbose)
                System.err.println("Progress:" + (n-S.size()) +"/" +n);

            if(S.isEmpty())
                break;
            Set<Integer> SDel = new HashSet<Integer>();
            for(int i : S) {
                if(upper[i] < k) {
                   SDel.add(i);
                }
            }
            while(!SDel.isEmpty()) {
                final Set<Integer> SDelNew = new HashSet<Integer>();
                for(int i : SDel) {
                    for(int j : graph[i]) {
                        upper[j]--;
                        if (upper[j] == k-1) {
                            SDelNew.add(j);
                        }
                    }

                    S.remove(i);
                    corenesses[i] = k-1;
                }
                SDel = SDelNew;
            }
        }

        if(verbose)
            System.err.println("Core decomposition is computed...");

        return corenesses;
    }

    /**
     * Write the corenesses of verticies to a file
     * @param corenesses    coreness of vertices
     * @param path  path of the output file
     * @param delim delimeter used in the output file
     * @throws IOException
     */
    private static void export(int[] corenesses, String path, String delim) throws IOException {

        System.err.println("Exporting result... "+ path);
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        for(int i=0; i<corenesses.length; i++) {
            bw.write(i + delim + corenesses[i]);
            bw.newLine();
        }
        bw.close();
        System.err.println("Result was exported.: "+ path);

    }

}
