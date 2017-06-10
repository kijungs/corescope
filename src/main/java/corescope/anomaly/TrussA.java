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

package corescope.anomaly;

import corescope.CoreDecomp;
import corescope.Import;
import corescope.TrussDecomp;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.stat.ranking.NaturalRanking;
import org.apache.commons.math3.stat.ranking.RankingAlgorithm;
import org.apache.commons.math3.stat.ranking.TiesStrategy;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Anomaly detection based on truss decomposition
 * @author Kijung Shin
 */
public class TrussA {

    /**
     * Main function
     * @param args  input_path, output_path
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if(args.length != 2) {
            printError();
            System.exit(-1);
        }

        final String input = args[0];
        final String output = args[1];

        CoreA.run(Import.load(input, "\t"), output, true);
    };

    private static void printError() {
        System.err.println("Usage: run_trussA.sh input_path output_path");
    }

}
