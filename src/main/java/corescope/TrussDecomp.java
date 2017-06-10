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

package corescope;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Truss Decomposition
 * @author Kijung Shin
 */
public class TrussDecomp {

    final static long base = Integer.MAX_VALUE;

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
        export(TrussDecomp.run(graph, true), output, "\t");

    }

    public static void printError() {
        System.err.println("Usage: run_truss_decomp.sh input_path output_path delimiter");
        System.err.println("delimiter should be one of [tab, space, comma]");
    }

    /**
     * compute the trussness of vertices in the given graph
     * @param graph input graph
     * @param verbose whether to print progress
     * @return trussness of vertices (vertex index -> trussness)
     */
    public static int[] run(int[][] graph, final boolean verbose) {

        if(verbose)
            System.err.println("computing truss decomposition...");

        final int n = graph.length;

        final HashMap<Long, Integer> sups = new HashMap<Long, Integer>();
        final int[] degree = new int[n];
        for(int i=0; i<n; i++) {
            degree[i] = graph[i].length;
        }
        for(int src=0; src<n; src++) {

            final int[] srcNeigbors = graph[src];
            final int srcLen = srcNeigbors.length;
            for(int j=0; j<graph[src].length; j++) {
                int dst = graph[src][j];
                int[] trgNeighbors = graph[dst];
                int trgLen = trgNeighbors.length;
                int k = 0;
                int l = 0;
                int count = 0;
                while(k < srcLen && l < trgLen) {
                    if(srcNeigbors[k] == trgNeighbors[l]) {
                        count++;
                        k++;
                        l++;
                    }
                    else if(srcNeigbors[k] > trgNeighbors [l]) {
                        l++;
                    }
                    else {
                        k++;
                    }
                }
                long edge = Math.min(src, dst) * base + Math.max(src, dst);
                sups.put(edge, count);
            }
        }

        final HashSet<Long> S = new HashSet<Long>();
        S.addAll(sups.keySet());

        int t = 2;
        final int m = S.size();
        for(;;t++) {

            if(verbose)
                System.err.println("Progress:" + (m-S.size()) +"/" +m);

            if(S.isEmpty())
                break;

            HashSet<Long> SDel = new HashSet<Long>();
            for(final long edge : S) {
                if(sups.get(edge) <= t-2) {
                    SDel.add(edge);
                }
            }

            while(!SDel.isEmpty()) {

                final HashSet<Long> SDelNew = new HashSet();
                for(final long edge : SDel) {

                    final int src = (int)(edge/base);
                    final int trg = (int)(edge - src * base);

                    final int[] srcNeigbors = graph[src];
                    final int srcLen = srcNeigbors.length;
                    final int[] trgNeighbors = graph[trg];
                    final int trgLen = trgNeighbors.length;

                    int k = 0;
                    int l = 0;
                    while(k < srcLen && l < trgLen) {
                        if(srcNeigbors[k] == trgNeighbors[l]) {
                            int w = srcNeigbors[k];

                            k++;
                            l++;

                            long edgeSrc = Math.min(src, w) * base + Math.max(src, w);
                            if(S.contains(edgeSrc)){
                                long edgeTrg = Math.min(trg, w) * base + Math.max(trg, w);
                                if (S.contains(edgeTrg)) {
                                    int currentSup = sups.get(edgeTrg);
                                    sups.put(edgeTrg, --currentSup);
                                    if (currentSup == t - 2) {
                                        SDelNew.add(edgeTrg);
                                    }
                                    currentSup = sups.get(edgeSrc);
                                    sups.put(edgeSrc, --currentSup);
                                    if (currentSup == t - 2) {
                                        SDelNew.add(edgeSrc);
                                    }
                                }
                            }
                        }
                        else if(srcNeigbors[k] > trgNeighbors [l]) {
                            l++;
                        }
                        else {
                            k++;
                        }
                    }

                    S.remove(edge);
                    sups.put(edge, t);
                }
                SDel = SDelNew;
            }
        }

        final int[] trussness = new int[n];
        for(final long edge : sups.keySet()) {
            final int sup = sups.get(edge);
            final int left = (int)(edge/base);
            final int right = (int)(edge - left * base);
            trussness[left] = Math.max(trussness[left], sup);
            trussness[right] = Math.max(trussness[right], sup);
        }

        if(verbose)
            System.err.println("Truss decomposition is computed...");

        return trussness;
    }

    /**
     * Write the trussness of verticies to a file
     * @param trussnesses    trussness of vertices
     * @param path  path of the output file
     * @param delim delimeter used in the output file
     * @throws IOException
     */
    private static void export(int[] trussnesses, String path, String delim) throws IOException {

        System.err.println("Exporting result... "+ path);
        final BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        for(int i=0; i<trussnesses.length; i++) {
            bw.write(i + delim + trussnesses[i]);
            bw.newLine();
        }
        bw.close();
        System.err.println("Result was exported.: "+ path);

    }

}
