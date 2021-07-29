package utils;

import java.util.HashSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MeasurementUtils {
    public static Boolean isDstAsID = true;

    /**
     * Get flow id for size measurement in each row of a file.
     */
    public static String getSizeMeasurementsFlowID(String[] strs, Boolean isEncoding) {
        if (strs.length == 0)
            return "";
        if (isEncoding)
            return Arrays.stream(strs).collect(Collectors.joining("\t"));

        //
        return Arrays.stream(Arrays.copyOfRange(strs, 0, strs.length - 1)).
                collect(Collectors.joining("\t"));


    }

    /**
     * todo::?????
     *
     * @param strs
     * @param isEncoding
     * @return
     */
    public static long getSize1FlowID(String[] strs, Boolean isEncoding) {
        if (strs.length == 0) return 0;
        else if (isEncoding) return ((Long.parseLong(strs[0]) << 32) | Long.parseLong(strs[1]));
        else return Long.parseLong(strs[0]);
    }


    /**
     * Get flow id and element id for spread measurement in each row of a file.
     */
    public static String[] getSpreadFlowIDAndElementID(String[] strs, Boolean isEncoding) {
        String[] res = new String[2];
        if (isEncoding) {
            if (isDstAsID) {
                res[0] = strs[1];
                res[1] = strs[0];
            } else {
                res[0] = strs[0];
                res[1] = strs[1];
            }
        } else {
            res[0] = strs[0];
        }
        return res;
    }

    /**
     * Get flow id for size measurement in each row of a file.
     */
    public static String getSizeFlowID(String[] strs, Boolean isEncoding) {
        if (strs.length == 0) return "";
        else if (isEncoding) return Arrays.stream(strs).collect(Collectors.joining("\t"));
        else return Arrays.stream(Arrays.copyOfRange(strs, 0, strs.length - 1)).collect(Collectors.joining("\t"));
    }

    /** get flow id and element id for spread measurment in each row of a file. */
    public static String[] getSperadFlowIDAndElementID(String[] strs, Boolean isEncoding) {
        String[] res = new String[2];
        if (isEncoding) {
            if (isDstAsID) {res[0] = strs[1]; res[1] = strs[0];}
            else {res[0] = strs[0]; res[1] = strs[1];}
        } else {
            res[0] = strs[0];
        }
        return res;
    }
}