import java.io.FileNotFoundException;


public class GeneralSketchGeneralVSktTests {
    public static final String CURRENT_DIR = System.getProperty("user.dir");

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Start****************************");
        DataStream dataStream = new DataStream(
                CURRENT_DIR + "\\src\\test\\resources\\data\\datatrace.txt",
                CURRENT_DIR + "\\src\\test\\resources\\data\\srcdstsize.txt",
                CURRENT_DIR + "\\src\\test\\resources\\data\\datatrace.txt",
                CURRENT_DIR + "\\src\\test\\resources\\data\\dstspread.txt"
        );

        GeneralSketchGeneralVSkt.setResultsDir(
                CURRENT_DIR + "\\src\\test\\results"
        );

        /////////////////////////////////////////////////
        /** measurement for flow sizes **/
        for (int i : GeneralSketchGeneralVSkt.sizeMeasurementConfig) {
            GeneralSketchGeneralVSkt.initSharing(i);
            GeneralSketchGeneralVSkt.initJoining(i);
            GeneralSketchGeneralVSkt.encodeSize(dataStream.getDataStreamForFlowSize());
            GeneralSketchGeneralVSkt.estimateSize(dataStream.getDataSummaryForFlowSize());
        }

        /** measurment for flow spreads **/
//		for (int i : GeneralSketchGeneralVSkt.spreadMeasurementConfig) {
//			GeneralSketchGeneralVSkt.	initSharing(i);
//			GeneralSketchGeneralVSkt.	initJoining(i);
//			GeneralSketchGeneralVSkt.	encodeSpread(dataStream.getDataStreamForFlowSpread());
//			GeneralSketchGeneralVSkt.	estimateSpread(dataStream.getDataSummaryForFlowSpread());
//		}
        System.out.println("DONE!****************************");
    }


}
