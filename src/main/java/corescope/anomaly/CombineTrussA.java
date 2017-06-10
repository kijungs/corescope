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
import corescope.Pair;
import corescope.TrussDecomp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CombineTrussA {

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

        CombineCoreA.run(Import.load(input, "\t"), output, weight, true);
    };

    private static void printError() {
        System.err.println("Usage: run_comb_trussA.sh input_path output_path weight");
        System.err.println("weight should be greater than or equal to 0");
        System.err.println("weight should be set 0 to run simple DSM without Truss-A");
    }

}
