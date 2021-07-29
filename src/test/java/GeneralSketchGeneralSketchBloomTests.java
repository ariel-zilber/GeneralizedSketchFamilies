import java.io.FileNotFoundException;


public class GeneralSketchGeneralSketchBloomTests {
    public static final String CURRENT_DIR = System.getProperty("user.dir");


    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Start****************************");
        DataStream dataStream = new DataStream(
                CURRENT_DIR + "\\src\\test\\resources\\data\\datatrace.txt",
                CURRENT_DIR + "\\src\\test\\resources\\data\\srcdstsize.txt",
                CURRENT_DIR + "\\src\\test\\resources\\data\\datatrace.txt",
                CURRENT_DIR + "\\src\\test\\resources\\data\\dstspread.txt");

        GeneralSketchGeneralSketchBloom.setResultsDir(
                CURRENT_DIR + "\\src\\test\\results"
        );
//
//
//        /** measurement for flow sizes **/
        for (int i : GeneralSketchGeneralSketchBloom.sizeMeasurementConfig) {
            GeneralSketchGeneralSketchBloom.initCM(i);
            GeneralSketchGeneralSketchBloom.encodeSize(dataStream.getDataStreamForFlowSize());
            GeneralSketchGeneralSketchBloom.estimateSize(dataStream.getDataSummaryForFlowSize());
        }
//
//        /** measurement for flow spreads **/
        for (int i : GeneralSketchGeneralSketchBloom.spreadMeasurementConfig) {
            GeneralSketchGeneralSketchBloom.initCM(i);
            GeneralSketchGeneralSketchBloom.encodeSpread(dataStream.getDataStreamForFlowSpread());
            GeneralSketchGeneralSketchBloom.estimateSpread(dataStream.getDataSummaryForFlowSpread());
        }

        System.out.println("DONE!****************************");
    }

}
