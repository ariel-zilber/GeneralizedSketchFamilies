import java.io.FileNotFoundException;


public class GeneralUnionGeneralVSktTests {
    public static final String CURRENT_DIR = System.getProperty("user.dir");

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Start****************************");
        DataStream dataStream = new DataStream(
                CURRENT_DIR + "\\src\\test\\resources\\data\\union\\",
                CURRENT_DIR + "\\src\\test\\resources\\data\\union\\srcdstsize.txt",
                CURRENT_DIR + "\\src\\test\\resources\\data\\union\\",
                CURRENT_DIR + "\\src\\test\\resources\\data\\union\\dstspread.txt"
        );

        GeneralUnionGeneralvSkt.setResultsDir(
                CURRENT_DIR + "\\src\\test\\results\\"
        );

        for (int i : GeneralUnionGeneralvSkt.sizeMeasurementConfig) {
            GeneralUnionGeneralvSkt.initSharing(i);
            GeneralUnionGeneralvSkt.initJoining(i);
            GeneralUnionGeneralvSkt.encodeSize(dataStream.getDataStreamForFlowSize());
            GeneralUnionGeneralvSkt.estimateSize(dataStream.getDataSummaryForFlowSize());
        }


        /** measurment for flow spreads **/
        for (int i : GeneralUnionGeneralvSkt.spreadMeasurementConfig) {
            GeneralUnionGeneralvSkt.initSharing(i);
            GeneralUnionGeneralvSkt.initJoining(i);
            GeneralUnionGeneralvSkt.encodeSpread(dataStream.getDataStreamForFlowSpread());
            GeneralUnionGeneralvSkt.estimateSpread(dataStream.getDataSummaryForFlowSpread());
        }

        System.out.println("DONE!****************************");
    }


}
