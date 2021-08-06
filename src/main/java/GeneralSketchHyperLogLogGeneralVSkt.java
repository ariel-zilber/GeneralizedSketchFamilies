
import utils.HashingUtils;
import utils.MeasurementUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 * A general framework for vSketch family. The elementary data structures to be plugged into can be counter, bitmap, FM sketch, HLL sketch. Specifically, we can
 * use counter to estimate flow sizes, and use bitmap, FM sketch and HLL sketch to estimate flow sizes/spreads.
 *
 * @author Youlin
 */

public class GeneralSketchHyperLogLogGeneralVSkt {
    public static Random rand = new Random();

    public static int n = 0;                        // total number of packets
    public static int flows = 0;                    // total number of flows
    public static int avgAccess = 0;                // average memory access for each packet
    public static int M = 1024 * 1024 * 8;    // total memory space Mbits
    public static GeneralDataStructure[] C;
    public static GeneralDataStructure[] B;
    public static GeneralDataStructure[][] CP;
    public static Set<Integer> sizeMeasurementConfig = new HashSet<>(Arrays.asList(0)); // 0-couter; 1-Bitmap; 2-FM sketch; 3-HLL sketch
    public static Set<Integer> spreadMeasurementConfig = new HashSet<>(Arrays.asList()); // 1-Bitmap; 2-FM sketch; 3-HLL sketch

    /**
     * parameters for sharing approach
     **/
    public static int u = 1;            // the unit size of each virtual data structure
    public static int w;                // number of the unit data structures in the physical array
    public static int m = 1;            // number of elementrary data structures in the virtual data structure
    public static int[] S;                // random seeds for the sharing approach

    /**
     * parameters for counters
     **/
    public static int mValueCounter = 128;
    public static int counterSize = 20;

    /**
     * parameters for bitmap
     **/
    public static int bitmapSize = 1;    // sharing at bit level
    public static int virtualArrayLength = 20000;

    /**
     * parameters for FM sketch
     **/
    public static int mValueFM = 128;
    public static int FMsketchSize = 32;

    /**
     * parameters for hyperLogLog
     **/
    public static int mValueHLL = 128;
    public static int HLLSize = 5;


    public static int periods = 1;
    private static String resultsDir;

    public static void setResultsDir(String resultsDir) {
        GeneralSketchHyperLogLogGeneralVSkt.resultsDir = resultsDir;
    }


    // Init the vSketch noise approach for different elementary data structures.
    public static void initJoining(int index) {
        switch (index) {
            case 0:
                B = new Counter[1];
                B[0] = new Counter(mValueCounter, counterSize);
                break;
            case 1:
                B = new Bitmap[1];
                B[0] = new Bitmap(M);
                break;
            case 2:
                B = new FMSketch[1];
                B[0] = new FMSketch(mValueFM, FMsketchSize);
                break;
            case 3:
                B = new VIHyperLogLog[1];
                B[0] = new VIHyperLogLog(mValueHLL, HLLSize);
                break;
            default:
                break;
        }
        System.out.println("vSkt(" + C[0].getDataStructureName() + ") Initialized!");
    }

    // Init the sharing approach for different elementary data structures.
    public static void initSharing(int index) {
        switch (index) {
            case 0:
                C = generateCounter();
                break;
            case 1:
                C = generateBitmap();
                break;
            case 2:
                C = generateFMsketch();
                break;
            case 3:
                hyperLogLogMode();

                //
                VIHyperLogLog[][] BP = new VIHyperLogLog[periods][1];
                for (int t = 0; t < periods; t++) {
                    BP[t]= hyperloglogTimePeriodSketch();//new VIHyperLogLog(w, HLLSize);
                }
                CP = BP;

                //
                C= hyperloglogTimePeriodSketch();
                break;
            default:
                break;
        }
        generatevSketchRamdonSeeds();
    }

    // Generate vSkt(counter) for flow size measurement.
    public static Counter[] generateCounter() {
        m = mValueCounter;
        u = counterSize;
        w = M / u;
        Counter[] B = new Counter[1];
        B[0] = new Counter(w, counterSize);

        Counter[][] BP = new Counter[periods][1];
        for (int t = 0; t < periods; t++) {
            BP[t][0] = new Counter(w, counterSize);
        }
        CP = BP;
        return B;
    }

    // Generate vSkt(bitmap) for flow size/spread measurement.
    public static Bitmap[] generateBitmap() {
        m = virtualArrayLength;
        u = bitmapSize;
        w = M / u;
        Bitmap[] B = new Bitmap[1];
        B[0] = new Bitmap(w);

        Bitmap[][] BP = new Bitmap[periods][1];
        for (int t = 0; t < periods; t++) {
            BP[t][0] = new Bitmap(w);
        }
        CP = BP;

        return B;
    }

    // Generate vSkt(FM) for flow size/spread measurement.
    public static FMSketch[] generateFMsketch() {
        m = mValueFM;
        u = FMsketchSize;
        w = M / u;
        FMSketch[] B = new FMSketch[1];
        B[0] = new FMSketch(w, FMsketchSize);

        FMSketch[][] BP = new FMSketch[periods][1];
        for (int t = 0; t < periods; t++) {
            BP[t][0] = new FMSketch(w, FMsketchSize);
        }
        CP = BP;

        return B;
    }
    private static  void hyperLogLogMode(){
        m = mValueHLL;
        u = HLLSize;
        w = M / u;
    }
    private static VIHyperLogLog[] hyperloglogTimePeriodSketch(){


        VIHyperLogLog[] B = new VIHyperLogLog[1];
        B[0] = new VIHyperLogLog(w, HLLSize);
        return B;
    }


    // Generate vSkt(HLL) for flow size/spread measurement.
    public static VIHyperLogLog[] generateHyperLogLog() {

        hyperLogLogMode();


        VIHyperLogLog[][] BP = new VIHyperLogLog[periods][1];
        for (int t = 0; t < periods; t++) {
            BP[t]= hyperloglogTimePeriodSketch();//new VIHyperLogLog(w, HLLSize);
        }
        CP = BP;

        return hyperloglogTimePeriodSketch();
    }

    // Generate random seeds for Sharing approach.
    public static void generatevSketchRamdonSeeds() {
        HashSet<Integer> seeds = new HashSet<Integer>();
        S = new int[m];
        int num = m;
        while (num > 0) {
            int s = rand.nextInt();
            if (!seeds.contains(s)) {
                num--;
                S[num] = s;
                seeds.add(s);
            }
        }
    }

    /**
     * Encode elements to vSketch for flow size measurement.
     */
    public static void encodeSize(String filePath) throws FileNotFoundException {
        System.out.println("Encoding elements using " + C[0].getDataStructureName().toUpperCase() + "s...");
        n = 0;
        for (int t = 0; t < periods; t++) {
            Scanner sc = new Scanner(new File(filePath + "output" + t + "v.txt"));
            System.out.println("Input file: " + filePath + "output" + t + "v.txt");
            while (sc.hasNextLine()) {
                String entry = sc.nextLine();
                String[] strs = entry.split("\\s+");
                long flowid = MeasurementUtils.getSize1FlowID(strs, true);
                n++;
                // segment encode, number of segments
                CP[t][0].encodeSegment(flowid, S, w / m);
            }
            sc.close();
        }
        for (int t = 0; t < periods; t++) {
            C[0] = C[0].join(CP[t][0]);
        }
        System.out.println("Total number of encoded pakcets: " + n);

    }

    /**
     * Estiamte flow sizes.
     */
    public static void estimateSize(String filePath) throws FileNotFoundException {
        System.out.println("Estimating Flow SIZEs...");
        Scanner sc = new Scanner(new File(filePath + periods + "\\outputsrcDstCount.txt"));
        System.out.println(filePath + periods + "\\outputsrcDstCount.txt");

        String resultFilePath = resultsDir + "VSketch\\size\\v" + C[0].getDataStructureName()
                + "_M_" + M / 1024 / 1024 + "_u_" + u + "_m_" + m + "_TT_" + periods;
        PrintWriter pw = new PrintWriter(new File(resultFilePath));
        System.out.println("Result directory: " + resultFilePath);
        for (int t = 0; t < m; t++) {
            B[0] = B[0].join(C[0], w / m, t);
        }
        // Estimate noise.
        int totalSum = B[0].getValue();
        while (sc.hasNextLine()) {
            String entry = sc.nextLine();
            String[] strs = entry.split("\\s+");
            long flowid = MeasurementUtils.getSize1FlowID(strs, true);
            int num = Integer.parseInt(strs[strs.length - 1]);
            // Get the estimate of the virtual data structure.
            int virtualSum = C[0].getValueSegment(flowid, S, w / m);
            Double estimate = Math.max(1.0 * (virtualSum - 1.0 * m * totalSum / w), 1);
            if (estimate < 0.0) {
                estimate = 1.0;
            }
            pw.println(entry + "\t" + estimate.intValue());
        }
        sc.close();
        pw.close();
    }

    /**
     * Encode elements to vSketch for flow spread measurement.
     */
    public static void encodeSpread(String filePath) throws FileNotFoundException {
        System.out.println("Encoding elements using " + C[0].getDataStructureName().toUpperCase() + "s...");
        n = 0;
        for (int t = 0; t < periods; t++) {
            Scanner sc = new Scanner(new File(filePath + "output" + t + "v.txt"));
            System.out.println("Input file: " + filePath + "output" + t + "v.txt");
            while (sc.hasNextLine()) {
                String entry = sc.nextLine();
                String[] strs = entry.split("\\s+");
                String[] res = MeasurementUtils.getSperadFlowIDAndElementID(strs, true);
                long flowid = Long.parseLong(res[0]);
                long elementid = Long.parseLong(res[1]);
                n++;
                CP[t][0].encodeSegment(flowid, elementid, S, w / m);
            }
            sc.close();
        }
        for (int t = 0; t < periods; t++) {
            C[0] = C[0].join(CP[t][0]);
        }
        System.out.println("Total number of encoded pakcets: " + n);

    }

    /**
     * Estiamte flow spreads.
     */
    public static void estimateSpread(String filePath) throws FileNotFoundException {

        // INIT
        System.out.println("Estimating Flow CARDINALITY...");
        Scanner sc = new Scanner(new File(filePath));
        String resultFilePath = resultsDir + "\\VSketch\\spread\\v" + C[0].getDataStructureName()
                + "_M_" + M / 1024 / 1024 + "_u_" + u + "_m_" + m;
        PrintWriter pw = new PrintWriter(new File(resultFilePath));

        // INPUT:

        // S - number of registers used by virtual HLL sketch
        int usedVirtualRegisters = m;

        // m - number of registers in physical register array
        int usedPhysicalRegisters = w;

        // {M_j} j in [1,t]
        VIHyperLogLog[] mIntersection ;
        mIntersection = new VIHyperLogLog[1];
        mIntersection[0] = new VIHyperLogLog(mValueHLL, HLLSize);



        for (int t = 0; t < periods; t++) {
            mIntersection[0] = (VIHyperLogLog) mIntersection[0].intesection(CP[t][0]);
        }


        // {A_j} j in [1,t]

        // STEP 1:
        // M intersection <-M_1/\M_2/\M_3.../\M_t
        // estimate n_s^* in M intersection

        // STEP 2:
        // A intersection <-A_1/\A_2/\A_3.../\A_t

        // STEP 3:
        // STEP 4:


        for (int t = 0; t < m; t++) {
            B[0] = B[0].join(C[0], w / m, t);
        }
        // Estimate noise.
        int totalSum = B[0].getValue();
        n = 0;
        while (sc.hasNextLine()) {
            String entry = sc.nextLine();
            String[] strs = entry.split("\\s+");
            long flowid = Long.parseLong(MeasurementUtils.getSpreadFlowIDAndElementID(strs, false)[0]);
            //
            //

            int num = Integer.parseInt(strs[strs.length - 1]);
            n += num;
            int virtualSum = C[0].getValueSegment(flowid, S, w / m);
            Double estimate = Math.max(1.0 * (virtualSum - 1.0 * m * totalSum / w), 1);
            if (estimate < 0.0) {
                estimate = 1.0;
            }
            pw.println(entry + "\t" + estimate.intValue());
        }
        sc.close();
        pw.close();
        // obtain estimation accuracy results
        System.out.println("Total flow spread: " + n);
    }

}
