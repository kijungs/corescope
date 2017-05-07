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

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;

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
        final int lengthOfArray = 10000;
        LinkedList<LongArrayList> listOfArrays = new LinkedList<LongArrayList>();
        LongArrayList array = new LongArrayList(lengthOfArray);
        Int2IntOpenHashMap nodeToIndex = new Int2IntOpenHashMap();

        int maxIndex = 0; // max vertex index
        int minIndex = Integer.MAX_VALUE; // min vertex index
        long n = 0; // number of vertices
        long m = 0; // number of edges
        int indexInArray = 0;
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
            else if(random.nextDouble() < p) {
                if(!nodeToIndex.containsKey(src)) {
                    nodeToIndex.put(src, nodeToIndex.size());
                }
                if(!nodeToIndex.containsKey(trg)) {
                    nodeToIndex.put(trg, nodeToIndex.size());
                }
                array.add(nodeToIndex.get(src) * ((long)Integer.MAX_VALUE) + nodeToIndex.get(trg));
                indexInArray++;
                if(indexInArray == lengthOfArray) {
                    listOfArrays.add(array);
                    array = new LongArrayList(lengthOfArray);
                    indexInArray = 0;
                }
            }
            maxIndex = Math.max(trg, Math.max(src, maxIndex));
            minIndex = Math.min(trg, Math.min(src, minIndex));
            m++;
        }
        br.readLine();
        listOfArrays.add(array);
        n = maxIndex - minIndex;


        // Step 1'. (Basic model Only)
        if(model == MODEL_BASIC) {
            return Math.pow(10, -0.34693 * Math.log10(n) + 0.61782 * Math.log10(m) + -0.032516);
        }

        //Step 2. load a sampled graph in memory
        int sampledNodeNum = nodeToIndex.size();
        nodeToIndex.clear();
        nodeToIndex = null;

        int[] nodeToDegree = new int[sampledNodeNum];
        for(LongArrayList lines : listOfArrays) {
            for (long line : lines) {
                int src = (int) (line / Integer.MAX_VALUE);
                nodeToDegree[src]++;
            }
        }

        int[][] sampledGraph = new int[sampledNodeNum][];
        for(int i=0; i<sampledNodeNum; i++) {
            if(nodeToDegree[i] > 0) {
                sampledGraph[i] = new int[nodeToDegree[i]];
            }
        }

        while(!listOfArrays.isEmpty()) {
            LongArrayList lines = listOfArrays.remove();
            for (long line : lines) {
                int src = (int) (line / Integer.MAX_VALUE);
                int trg = (int) (line - Integer.MAX_VALUE * src);
                sampledGraph[src][--nodeToDegree[src]] = trg;
            }
            lines.clear();
        }
        nodeToDegree = null;

        //Step 3. Estimate the number of triangles
        final long sampleCount = TriangleCount.run(sampledGraph);
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
