import java.io.FileNotFoundException;


public class GeneralSketchGeneralCountMinTests {
    public static final String CURRENT_DIR = System.getProperty("user.dir");


    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Start****************************");
        DataStream dataStream = new DataStream(
                CURRENT_DIR + "\\src\\test\\resources\\data\\datatrace.txt",
                CURRENT_DIR + "\\src\\test\\resources\\data\\srcdstsize.txt",
                CURRENT_DIR + "\\src\\test\\resources\\data\\datatrace.txt",
                CURRENT_DIR + "\\src\\test\\resources\\data\\dstspread.txt");

        GeneralSketchGeneralCountMin.setResultsDir(
                CURRENT_DIR + "\\src\\test\\results"
        );
//
//
//        /** measurement for flow sizes **/
        for (int i : GeneralSketchGeneralCountMin.sizeMeasurementConfig) {
            GeneralSketchGeneralCountMin.initCM(i);
            GeneralSketchGeneralCountMin.encodeSize(dataStream.getDataStreamForFlowSize());
            GeneralSketchGeneralCountMin.estimateSize(dataStream.getDataSummaryForFlowSize());
        }
//
//        /** measurement for flow spreads **/
//        for (int i : GeneralSketchGeneralCountMin.spreadMeasurementConfig) {
//            GeneralSketchGeneralCountMin.initCM(i);
//            GeneralSketchGeneralCountMin.encodeSpread(dataStream.getDataStreamForFlowSpread());
//            GeneralSketchGeneralCountMin.estimateSpread(dataStream.getDataSummaryForFlowSpread());
//        }

        System.out.println("DONE!****************************");
    }

}
