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

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Single pass streaming algorithm for estimating degeneracy
 * @author Kijung Shin
 */
public class SinglePass {

    /**
     * Main function
     * @param args  input_path, model, sampling_ratio
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if(args.length != 3) {
            printError();
            System.exit(-1);
        }

        final String input = args[0];
        final String delim = "\t";
        int model = 0;
        if(args[1].compareToIgnoreCase("BASIC")==0) {
            model = MODEL_BASIC;
        }
        else if(args[1].compareToIgnoreCase("TRIANGLE")==0){
            model = MODEL_TRIANGLE;
        }
        else if(args[1].compareToIgnoreCase("OVERALL")==0) {
            model = MODEL_OVERALL;
        }
        else {
            System.err.println("Unknown Model Error");
            printError();
            System.exit(-1);
        }
        final double p  = Double.valueOf(args[2]);

        System.out.println("Estimated degeneray: "+run(input, delim, model, p));
    }

    private static void printError() {
        System.err.println("Usage: run_coreD.sh input_path model sampling_ratio");
        System.err.println("Model should be one of [basic, triangle, overall]");
        System.err.println("Sampling_ratio should be in (0,1]");
    }

    /**
     * Basic model
     */
    public static final int MODEL_BASIC = 0;

    /**
     * Triangle model
     */
    public static final int MODEL_TRIANGLE = 1;

    /**
     * Overall model
     */
    public static final int MODEL_OVERALL = 2;

    /**
     * run SinglePass
     * @param path   path to the input graph file
     * @param delim  delimeter used in the input graph file
     * @param p  sampling ratio
     * @return   estimated degeneracy
     * @throws IOException
     */
    public static double run(String path, String delim, final int model, double p) throws IOException {

        final Random random = new Random();

        //Step 1. (Streaming). sample edges and compute $n$ and $m$.
        final BufferedReader br = new BufferedReader(new FileReader(path));
        final List<Long> edges = new LinkedList(); // sampled edges
        int maxIndex = 0; // max vertex index
        int minIndex = Integer.MAX_VALUE; // min vertex index
        long n = 0; // number of vertices
        long m = 0; // number of edges
        while(true) {
            String line = br.readLine();
            if(line == null) {
                break;
            }
            String[] tokens = line.split(delim);
            int src = Integer.valueOf(tokens[0]);
            int trg = Integer.valueOf(tokens[1]);
            if(src > trg) {
                continue;
            }
            else {
                m++;
                if(random.nextDouble() < p) {
                    edges.add(((long)src) * Integer.MAX_VALUE + trg);
                    edges.add(((long)trg) * Integer.MAX_VALUE + src);
                    maxIndex = Math.max(trg, Math.max(src, maxIndex));
                    minIndex = Math.min(trg, Math.min(src, minIndex));
                }
            }
        }
        br.readLine();
        n = maxIndex - minIndex;


        // Step 1'. (Basic model Only)
        if(model == MODEL_BASIC) {
            return Math.pow(10, -0.34693 * Math.log10(n) + 0.61782 * Math.log10(m) + -0.032516);
        }

        //Step 2. load a sampled graph in memory
        final int lineNum = edges.size();
        final long[] lineArr = new long[lineNum];
        for(int i=0; i<lineNum; i++) {
            lineArr[i] = edges.remove(0);
        }
        Arrays.sort(lineArr);
        final int[][] graph = new int[maxIndex+1][];
        long previousLine = 0;
        int previousSrc = 0;
        final List<Integer> neighbors = new LinkedList();
        for(long line : lineArr) {
            if(line==previousLine)
                continue;
            int src = (int) (line/Integer.MAX_VALUE);
            int trg = (int) (line - Integer.MAX_VALUE*src);
            if(src!= previousSrc) {
                graph[previousSrc] = new int[neighbors.size()];
                int index = 0;
                for(int neighbor : neighbors) {
                    graph[previousSrc][index++] = neighbor;
                }
                neighbors.clear();
                previousSrc = src;
            }
            neighbors.add(trg);
            previousLine = line;
        }
        graph[previousSrc] = new int[neighbors.size()];
        int index = 0;
        for(int neighbor : neighbors) {
            graph[previousSrc][index++] = neighbor;
        }
        for(int i=0; i<=maxIndex; i++) {
            if(graph[i]==null) {
                graph[i] = new int[0];
            }
        }

        //Step 3. Estimate the number of triangles
        final long sampleCount = TriangleCount.run(graph);
        final long delta = p >= 1.0 ? sampleCount : (long) (sampleCount / p / p / p);

        //Step 4. Estimate degeneracy
        if(model == MODEL_TRIANGLE) {
            return Math.pow(10, 0.31542 * Math.log10(delta) -0.1994);
        }
        else if(model == MODEL_OVERALL){
            return Math.pow(10, 0.59436 * Math.log10(delta) + 0.18139* Math.log10(n) - 0.4948* Math.log10(m) + 0.030208);
        }
        else {
            System.err.println("UNKNOWN MODEL");
            return -1;
        }
    }

}
