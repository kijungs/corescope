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
package corescope.anomaly;

import corescope.CoreDecomp;
import corescope.Import;
import corescope.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DSM {

    /**
     * Main function
     * @param args  input_path, output_path, weight
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if(args.length != 3) {
            printError();
            System.exit(-1);
        }

        final String input = args[0];
        final String output = args[1];
        final int weight = Integer.valueOf(args[2]);

        run(Import.load(input, "\t"), output, weight);
    };

    private static void printError() {
        System.err.println("Usage: run_comb.sh input_path output_path weight");
        System.err.println("weight should be greater than or equal to 0");
        System.err.println("weight should be set 0 to run simple DSM without CoreA");
    }

    /**
     * Run DSM with the given graph and CoreA scores for nodes
     * @param graph input graph
     * @param output path of the output file
     * @param weight weight that result of CoreA is multiplied by for being balanced with average degree
     */
    public static void run(int[][] graph, String output, int weight) throws IOException {

        double[] nodeSuspiciousness = null;
        if(weight != 0) {
            int[] degree = Degree.run(graph);
            int[] coreness = CoreDecomp.run(graph, false);
            nodeSuspiciousness = CoreA.getAnomalyScore(degree, coreness);
            for(int i=0; i<nodeSuspiciousness.length; i++) {
                nodeSuspiciousness[i] = weight * nodeSuspiciousness[i];
            }
        }
        int n = graph.length;
        int[][] result = run(graph, graph, n, n, nodeSuspiciousness, nodeSuspiciousness);
        final Set<Integer> anomalies = new HashSet<Integer>();
        for(int mode=0; mode<2; mode++) {
            for(int node : result[mode]) {
                anomalies.add(node);
            }
        }
        writeResult(output, anomalies);
    }

    /**
     *
     * @param output path of the output file
     * @param anomalies list of anomalies
     */
    public static void writeResult(String output, Set<Integer> anomalies) throws IOException {
        final BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        bw.write("vertex_index");
        bw.newLine();
        for(int node : anomalies) {
            bw.write(String.valueOf(node));
            bw.newLine();
        }
        bw.close();
    }


    /**
     * Run DSM with the given graph and CoreA scores for nodes
     * @param graph graph (adjacency list)
     * @param transpose transposed graph
     * @param numRows number of rows in the graph
     * @param numCols number of columns in the graph
     * @param rowSuspiciousness CoreA score for row entries
     * @param colSuspiciousness CoreA score for column entries
     * @return <list of rows, list of columns> belongs to the densest block
     */
    public static int[][] run(int[][] graph, int[][] transpose, int numRows, int numCols, double[] rowSuspiciousness, double[] colSuspiciousness) {

        double[] rowDegree = null; // degree + suspiciousness
        double[] colDegree = null; // degree + suspiciousness
        double suspiciousSum = 0;
        if(rowSuspiciousness == null) {
            rowDegree = new double[numRows];
        }
        else {
            rowDegree = rowSuspiciousness.clone();
            for(int i=0; i<numRows; i++) {
                suspiciousSum += rowSuspiciousness[i];
            }
        }
        if(colSuspiciousness == null) {
            colDegree = new double[numCols];
        }
        else {
            colDegree = colSuspiciousness.clone();
            for(int i=0; i<numCols; i++) {
                suspiciousSum += colSuspiciousness[i];
            }
        }

        long edgeNum = 0;
        for(int src=0; src<numRows; src++) {
            for(int dst : graph[src]) {
                rowDegree[src] += 1;
                colDegree[dst] += 1;
                edgeNum += 1;
            }
        }
        suspiciousSum += edgeNum;

        HashIndexedMinHeap rowHeap = new HashIndexedMinHeap(numRows);
        for(int i=0; i<numRows; i++) {
            rowHeap.insert(i, rowDegree[i]);
        }

        HashIndexedMinHeap colHeap = new HashIndexedMinHeap(numRows);
        for(int j=0; j<numCols; j++) {
            colHeap.insert(j, colDegree[j]);
        }

        int[] modes = new int[numRows+numCols];
        int[] order = new int[numRows+numCols];
        boolean[][] removed = new boolean[2][];
        removed[0] = new boolean[numRows];
        removed[1] = new boolean[numCols];

        double maxDensity = 0; // density of the densest block so far
        int maxDensityNodesNum = 0; // number of nodes belongs to the densest block
        int numOfNodesBelong = numRows+numCols; // number of nodes belongs to the current block
        while(numOfNodesBelong >= 1) {

            Pair<Integer, Double> rowPair = rowHeap.peek();
            Pair<Integer, Double> colPair = colHeap.peek();

            Pair<Integer, Double> pair = null;
            int modeToRemove = 0;

            if(rowPair!=null && (colPair==null || rowPair.getValue() < colPair.getValue())) {
                pair = rowHeap.poll();
                modeToRemove = 0;
            }
            else {
                pair = colHeap.poll();
                modeToRemove = 1;
            }

            suspiciousSum -= pair.getValue();
            int node = pair.getKey();
            order[--numOfNodesBelong] = node;
            modes[numOfNodesBelong] = modeToRemove;

            double density = suspiciousSum/numOfNodesBelong;
            if (numOfNodesBelong >= 1 && density > maxDensity) {
                maxDensity = density;
                maxDensityNodesNum = numOfNodesBelong;
            }

            removed[modeToRemove][node] = true;
            if(modeToRemove==0) { //remove from rows
                for (int i = 0; i < graph[node].length; i++) {
                    int dst = graph[node][i];
                    if (!removed[1][dst]) {
                        colHeap.refreshPriority(dst, colHeap.getPriority(dst) - 1);
                    }
                }
            }
            else { //remove from columns
                for (int i = 0; i < transpose[node].length; i++) {
                    int dst = transpose[node][i];
                    if (!removed[0][dst]) {
                        rowHeap.refreshPriority(dst, rowHeap.getPriority(dst) - 1);
                    }
                }
            }
        }

        int numRowsBelongs = 0;
        int numColsBelongs = 0;
        for(int i=0; i<maxDensityNodesNum; i++) {
            if(modes[i] == 0) {
                numRowsBelongs++;
            }
            else {
                numColsBelongs++;
            }
        }
        int[] rows = new int[numRowsBelongs];
        int[] cols = new int[numColsBelongs];
        int rowIndex = 0;
        int colIndex = 0;
        for(int i=0; i<maxDensityNodesNum; i++) {
            if(modes[i] == 0) {
                rows[rowIndex++] = order[i];
            }
            else {
                cols[colIndex++] = order[i];
            }
        }

        return new int[][]{rows, cols};

    }

}
