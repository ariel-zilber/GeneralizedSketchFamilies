
import utils.HashingUtils;
import utils.MeasurementUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * for SIGmetrics2020 sketchBlm
 * changes based on CountMin:
 * 1) one array
 * 2) change in initialization of w (w is the number of basic data structures in each segment, w / m)
 * 3) same encode and estimate
 *
 * @author Youlin
 */

public class GeneralUnionGeneralSketchBloom {

    public static int n = 0;                        // total number of packets
    public static int flows = 0;                    // total number of flows
    public static int avgAccess = 0;                // average memory access for each packet
    public static int M = 1024 * 1024 * 32;    // total memory space Mbits
    public static GeneralDataStructure[][] C;
    public static GeneralDataStructure[][][] CP;
    public static Set<Integer> sizeMeasurementConfig = new HashSet<>(Arrays.asList(0)); // -1-regular CM; 0-enhanced CM; 1-Bitmap; 2-FM sketch; 3-HLL sketch
    public static Set<Integer> spreadMeasurementConfig = new HashSet<>(Arrays.asList()); // 1-Bitmap; 2-FM sketch; 3-HLL sketch
    public static Set<Integer> expConfig = new HashSet<>(Arrays.asList()); //0-ECountMin dist exp

    /**
     * parameters for bSketch
     */
    public static final int d = 4;            // the number of estimators for each flow in bSketch
    public static int w = 1;            // the number of arrays in bSketch
    public static int u = 1;            // the size of each elementary data structure in Count Min.
    public static int[] S = new int[d];        // random seeds for bSketch
    public static int m = 1;            // number of bits/registers in each unit (used for bitmap, FM sketch and HLL sketch)


    /**
     * parameters for counter
     */
    public static int mValueCounter = 1;            // only one counter in the counter data structure
    public static int counterSize = 32;                // size of each unit

    /**
     * parameters for bitmap
     */
    public static int bitArrayLength = 5000;

    /**
     * parameters for FM sketch
     **/
    public static int mValueFM = 128;
    public static int FMsketchSize = 32;

    /**
     * parameters for HLL sketch
     **/
    public static int mValueHLL = 128;
    public static final int HLLSize = 5;

    public static int periods = 1;

    private static String resultsDir;

    public static void setResultsDir(String resultsDir) {
        GeneralUnionGeneralSketchBloom.resultsDir = resultsDir;
    }

    // Generate bSkt(counter) for flow size measurement.
    public static Counter[][] generateCounter() {
        m = mValueCounter;
        u = counterSize * mValueCounter;
        w = (M / u);
        Counter[][] B = new Counter[1][w];
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < w; j++) {
                B[i][j] = new Counter(1, counterSize);
            }
        }

        Counter[][][] BP = new Counter[periods][1][w];
        for (int t = 0; t < periods; t++) {
            for (int j = 0; j < w; j++)
                BP[t][0][j] = new Counter(1, counterSize);
        }
        CP = BP;
        return B;
    }

    // Generate bSkt(bitmap) for flow size/spread measurement.
    public static Bitmap[][] generateBitmap() {
        m = bitArrayLength;
        u = bitArrayLength;
        w = (M / u) / 1;
        Bitmap[][] B = new Bitmap[1][w];
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < w; j++) {
                B[i][j] = new Bitmap(bitArrayLength);
            }
        }
        Bitmap[][][] BP = new Bitmap[periods][1][w];
        for (int t = 0; t < periods; t++) {
            for (int j = 0; j < w; j++)
                BP[t][0][j] = new Bitmap(bitArrayLength);
        }
        CP = BP;
        return B;
    }

    // Generate bSkt(FMsketch) for flow size/spread measurement.
    public static FMSketch[][] generateFMsketch() {
        m = mValueFM;
        u = FMsketchSize * mValueFM;
        w = (M / u) / 1;
        FMSketch[][] B = new FMSketch[1][w];
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < w; j++) {
                B[i][j] = new FMSketch(mValueFM, FMsketchSize);
            }
        }

        FMSketch[][][] BP = new FMSketch[periods][1][w];
        for (int t = 0; t < periods; t++) {
            for (int j = 0; j < w; j++)
                BP[t][0][j] = new FMSketch(mValueFM, FMsketchSize);
        }
        CP = BP;
        return B;
    }

    // Generate bSkt(HLL) for flow size/spread measurement.
    public static HyperLogLog[][] generateHyperLogLog() {
        m = mValueHLL;
        u = HLLSize * mValueHLL;
        w = (M / u) / 1;
        HyperLogLog[][] B = new HyperLogLog[1][w];
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < w; j++) {
                B[i][j] = new HyperLogLog(mValueHLL, HLLSize);
            }
        }
        HyperLogLog[][][] BP = new HyperLogLog[periods][1][w];
        for (int t = 0; t < periods; t++) {
            for (int j = 0; j < w; j++)
                BP[t][0][j] = new HyperLogLog(mValueHLL, HLLSize);
        }
        CP = BP;
        return B;
    }

    // Init bSketch for different elementary data structures.
    public static void initCM(int index) {
        switch (index) {
            case 0:
            case -1:
                C = generateCounter();
                break;
            case 1:
                C = generateBitmap();
                break;
            case 2:
                C = generateFMsketch();
                break;
            case 3:
                C = generateHyperLogLog();
                break;
            default:
                break;
        }
        generateCMRandomSeeds();
        System.out.println("\nSketchBloom-" + C[0][0].getDataStructureName() + " Initialized!-----------");
    }

    // Generate random seeds for bSketch.
    public static void generateCMRandomSeeds() {
        HashSet<Integer> seeds = new HashSet<Integer>();
        int num = d;
        while (num > 0) {
            int s = HashingUtils.getSeed().nextInt();
            if (!seeds.contains(s)) {
                num--;
                S[num] = s;
                seeds.add(s);
            }
        }
    }

    /**
     * Encode elements to bSketch for flow size measurement.
     */
    public static void encodeSize(String filePath) throws FileNotFoundException {
        System.out.println("Encoding elements using " + C[0][0].getDataStructureName().toUpperCase() + "s for flow size measurement......");
        n = 0;
        for (int t = 0; t < periods; t++) {
            Scanner sc = new Scanner(new File(filePath + "splitIn" + periods + "\\output" + t + "v.txt"));
            System.out.println("Input file: " + filePath + "splitIn" + periods + "\\output" + t + "v.txt");
            while (sc.hasNextLine()) {
                String entry = sc.nextLine();
                String[] strs = entry.split("\\s+");
                String flowid = MeasurementUtils.getSizeFlowID(strs, true);
                n++;

                for (int i = 0; i < d; i++) {
                    int j = (HashingUtils.intHash(HashingUtils.FNVHash1(flowid) ^ S[i]) % w + w) % w;
                    CP[t][0][j].encode();
                }
            }
            sc.close();
        }
        for (int t = 0; t < periods; t++) {
            for (int j = 0; j < w; j++)
                C[0][j] = C[0][j].join(CP[t][0][j]);
        }
        System.out.println("Total number of encoded pakcets: " + n);

    }


    /**
     * Estimate flow sizes.
     */
    public static void estimateSize(String filePath) throws FileNotFoundException {
        System.out.println("Estimating Flow SIZEs...");
        System.out.println(filePath);
        Scanner sc = new Scanner(new File(filePath));
        String resultFilePath = resultsDir + "BSketch\\size\\" + C[0][0].getDataStructureName()
                + "_M_" + M / 1024 / 1024 + "_d_" + d + "_u_" + u + "_m_" + m + "_TS_" + periods;
        PrintWriter pw = new PrintWriter(new File(resultFilePath));
        System.out.println("w :" + w);
        System.out.println("Result directory: " + resultFilePath);

        while (sc.hasNextLine()) {
            String entry = sc.nextLine();
            String[] strs = entry.split("\\s+");
            String flowid = MeasurementUtils.getSizeFlowID(strs, false);
            int num = Integer.parseInt(strs[strs.length - 1]);

            int estimate = Integer.MAX_VALUE;

            for (int i = 0; i < d; i++) {
                int j = (HashingUtils.intHash(HashingUtils.FNVHash1(flowid) ^ S[i]) % w + w) % w;
                estimate = Math.min(estimate, C[0][j].getValue());
            }
            pw.println(entry + "\t" + estimate);
        }
        sc.close();
        pw.close();
    }

    /**
     * Encode elements to bSketch for flow spread measurement.
     */
    public static void encodeSpread(String filePath) throws FileNotFoundException {
        System.out.println("Encoding elements using " + C[0][0].getDataStructureName().toUpperCase() + "s for flow spread measurement......");
        n = 0;
        for (int t = 0; t < periods; t++) {
            Scanner sc = new Scanner(new File(filePath + "splitIn" + periods + "\\output" + t + "v.txt"));

            System.out.println("Input file: " + filePath + "splitIn" + periods + "\\output" + t + "v.txt");
            while (sc.hasNextLine()) {
                String entry = sc.nextLine();
                String[] strs = entry.split("\\s+");
                String[] res = MeasurementUtils.getSperadFlowIDAndElementID(strs, true);
                String flowid = res[0];
                String elementid = res[1];
                n++;
                for (int i = 0; i < d; i++) {
                    int j = (HashingUtils.intHash(HashingUtils.FNVHash1(flowid) ^ S[i]) % w + w) % w;
                    CP[t][0][j].encode(elementid);
                }
            }
            System.out.println("Total number of encoded pakcets: " + n);
            sc.close();
        }
        for (int t = 0; t < periods; t++) {
            for (int j = 0; j < w; j++)
                C[0][j] = C[0][j].join(CP[t][0][j]);
        }
    }

    /**
     * Estimate flow spreads.
     */
    public static void estimateSpread(String filepath) throws FileNotFoundException {
        System.out.println("Estimating Flow CARDINALITY...");
        Scanner sc = new Scanner(new File(filepath));
        String resultFilePath = resultsDir + "BSketch\\spread\\" + C[0][0].getDataStructureName()
                + "_M_" + M / 1024 / 1024 + "_d_" + d + "_u_" + u + "_m_" + m + "_TS_" + periods;
        PrintWriter pw = new PrintWriter(new File(resultFilePath));
        System.out.println("Result directory: " + resultFilePath);
        while (sc.hasNextLine()) {
            String entry = sc.nextLine();
            String[] strs = entry.split("\\s+");
            String flowid = MeasurementUtils.getSperadFlowIDAndElementID(strs, false)[0];
            int num = Integer.parseInt(strs[strs.length - 1]);

            int estimate = Integer.MAX_VALUE;

            for (int i = 0; i < d; i++) {
                int j = (HashingUtils.intHash(HashingUtils.FNVHash1(flowid) ^ S[i]) % w + w) % w;
                estimate = Math.min(estimate, C[0][j].getValue());
            }
            pw.println(entry + "\t" + estimate);
        }
        sc.close();
        pw.close();
    }

}
