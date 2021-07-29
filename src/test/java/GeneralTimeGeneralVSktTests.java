import java.io.FileNotFoundException;


public class GeneralTimeGeneralVSktTests {
    public static final String CURRENT_DIR = System.getProperty("user.dir");

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Start****************************");
        DataStream dataStream = new DataStream(
                CURRENT_DIR + "\\src\\test\\resources\\data\\time\\",
                CURRENT_DIR + "\\src\\test\\resources\\data\\time\\T",
                CURRENT_DIR + "\\src\\test\\resources\\data\\time\\",
                CURRENT_DIR + "\\src\\test\\resources\\data\\time\\T"
        );

        GeneralTimeGeneralvSkt.setResultsDir(
                CURRENT_DIR + "\\src\\test\\results\\"
        );

        for (int i : GeneralTimeGeneralvSkt.sizeMeasurementConfig) {
            GeneralTimeGeneralvSkt.initSharing(i);
            GeneralTimeGeneralvSkt.initJoining(i);
            GeneralTimeGeneralvSkt.encodeSize(dataStream.getDataStreamForFlowSize());
            GeneralTimeGeneralvSkt.estimateSize(dataStream.getDataSummaryForFlowSize());
        }


        /** measurment for flow spreads **/
        for (int i : GeneralTimeGeneralvSkt.spreadMeasurementConfig) {
            GeneralTimeGeneralvSkt.initSharing(i);
            GeneralTimeGeneralvSkt.      initJoining(i);
            GeneralTimeGeneralvSkt.    encodeSpread(dataStream.getDataStreamForFlowSpread());
            GeneralTimeGeneralvSkt.   estimateSpread(dataStream.getDataSummaryForFlowSpread());
        }

        System.out.println("DONE!****************************");
    }


}
