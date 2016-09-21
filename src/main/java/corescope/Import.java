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

import java.io.*;
import java.util.*;

/**
 * Load an input graph in memory
 * @author Kijung Shin
 */
public class Import {

    final static int linePerFile = 30000000;

    /**
     * load an input graph in memory
     * @param path  path of the input graph file
     * @param delim delimiter used in the file
     * @return
     * @throws IOException
     */
    public static int[][] load(String path, String delim) throws IOException {
        return load(path, delim, false);
    }

    /**
     * load an input graph in memory
     * @param path  path of the input graph file
     * @param delim delimiter used in the file
     * @param verbose whether to print logs
     * @return
     * @throws IOException
     */
    public static int[][] load(String path, String delim, final boolean verbose) throws IOException {

        if(verbose)
            System.err.println("loading graph...: "+ path);

        final Map<Integer, Set<Integer>> idToNeighbors = new HashMap();

        int maxNum = 0;
        final BufferedReader br = new BufferedReader(new FileReader(path));
        while(true) {
            final String line = br.readLine();
            if(line == null) {
                break;
            }
            else if(line.startsWith("#") || line.startsWith("%") || line.startsWith("//")) { //comment

                if(verbose) {
                    System.err.println("The following line was ignored during loading a graph:");
                    System.err.println(line);
                }
                continue;
            }
            else {
                String[] tokens = line.split(delim);
                int src = Integer.valueOf(tokens[0]);
                int trg = Integer.valueOf(tokens[1]);

                if(src==trg)
                    continue;

                if(!idToNeighbors.containsKey(src)) {
                    idToNeighbors.put(src, new TreeSet());
                }
                idToNeighbors.get(src).add(trg);
                if (!idToNeighbors.containsKey(trg)) {
                    idToNeighbors.put(trg, new TreeSet());
                }
                idToNeighbors.get(trg).add(src);
                maxNum = Math.max(src, maxNum);
                maxNum = Math.max(trg, maxNum);
            }
        }

        int[][] results = new int[maxNum+1][];

        for(int i=0; i<results.length; i++) {
            if(idToNeighbors.containsKey(i)) {
                results[i] = new int[idToNeighbors.get(i).size()];
                int j=0;
                for(int neighbor : idToNeighbors.get(i)) {
                    results[i][j++] = neighbor;
                }
            }
            else {
                results[i] = new int[0];
            }
        }

        if(verbose)
           System.err.println("graph was loaded.: "+ path);

        return results;
    }

    /**
     * load a large-scale input graph in memory
     * @param path  path of the input graph file
     * @param delim delimiter used in the file
     * @return
     * @throws IOException
     */
    public static int[][] loadLarge(String path, String delim) throws IOException {

        // Scanning Graph
        System.err.println("scanning graph...");
        int maxNum = 0;
        long lineNum = 0;

        BufferedReader br = new BufferedReader(new FileReader(path));
        while(true) {
            final String line = br.readLine();
            if(line == null) {
                break;
            }
            else if(line.startsWith("#") || line.startsWith("%") || line.startsWith("//")) { //comment
                System.err.println("The following line was ignored during loading a graph:");
                System.err.println(line);
                continue;
            }
            else {
                String[] tokens = line.split(delim);
                int src = Integer.valueOf(tokens[0]);
                int trg = Integer.valueOf(tokens[1]);
                if(src==trg)
                    continue;

                maxNum = Math.max(src, maxNum);
                maxNum = Math.max(trg, maxNum);
                lineNum++;
            }
        }
        br.close();

        if(linePerFile > lineNum) {
            return load(path, delim);
        }

        System.err.println("splitting graph...");

        int splitNum = (int) (lineNum/linePerFile+1);

        DataOutputStream[] outs = new DataOutputStream[splitNum];
        int[] counts = new int[splitNum];

        for(int i=0; i<splitNum; i++) {
            outs[i] = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path+"_"+i)));
        }

        int base = maxNum + 1;

        br = new BufferedReader(new FileReader(path));
        while(true) {
            final String line = br.readLine();
            if(line == null) {
                break;
            }
            else if(line.startsWith("#") || line.startsWith("%") || line.startsWith("//")) { //comment
                System.err.println("The following line was ignored during loading a graph:");
                System.err.println(line);
                continue;
            }
            else {
                String[] tokens = line.split(delim);
                int src = Integer.valueOf(tokens[0]);
                int trg = Integer.valueOf(tokens[1]);
                if(src==trg)
                    continue;
                int srcSplit = src % splitNum;
                int trgSplit = trg % splitNum;
                outs[srcSplit].writeLong(((long)src) * base + trg);
                counts[srcSplit]++;
                outs[trgSplit].writeLong(((long)trg) * base + src);
                counts[trgSplit]++;
            }
        }
        br.close();

        for(int i=0; i<splitNum; i++) {
            outs[i].close();
        }

        System.err.println("loading graph...");

        int[][] result = new int[maxNum+1][];

        for(int i=0; i<splitNum; i++) {
            DataInputStream ins = new DataInputStream(new BufferedInputStream(new FileInputStream(path+"_"+i)));
            long[] lines = new long[counts[i]];
            for(int j=0; j<counts[i]; j++) {
                lines[j] = ins.readLong();
            }
            ins.close();
            new File(path+"_"+i).delete(); //remove temporary file

            Arrays.sort(lines);

            long previousLine = 0;
            int previousSrc = 0;
            final List<Integer> neighbors = new LinkedList<Integer>();
            for(long line : lines) {
                if(line==previousLine)
                    continue;
                int src = (int) (line/base);
                int trg = (int) (line - base*src);
                if(src!= previousSrc) {
                    result[previousSrc] = new int[neighbors.size()];
                    int index = 0;
                    for(int neighbor : neighbors) {
                        result[previousSrc][index++] = neighbor;
                    }
                    neighbors.clear();
                    previousSrc = src;
                }
                neighbors.add(trg);
                previousLine = line;
            }

            result[previousSrc] = new int[neighbors.size()];
            int index = 0;
            for(int neighbor : neighbors) {
                result[previousSrc][index++] = neighbor;
            }

            System.err.println((i+1) + "th split was loaded");
        }

        for(int i=0; i<=maxNum; i++) {
            if(result[i]==null) {
                result[i] = new int[0];
            }
        }

        System.err.println("graph was loaded.");

        return result;
    }
}
