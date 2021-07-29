import java.io.FileNotFoundException;


public class GeneralTimeGeneralSketchBloomTests {
    public static final String CURRENT_DIR = System.getProperty("user.dir");

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Start****************************");
        DataStream dataStream = new DataStream(
                CURRENT_DIR + "\\src\\test\\resources\\data\\time\\",
                CURRENT_DIR + "\\src\\test\\resources\\data\\time\\T",
                CURRENT_DIR + "\\src\\test\\resources\\data\\time\\",
                CURRENT_DIR + "\\src\\test\\resources\\data\\time\\T"
        );

        GeneralTimeGeneralSketchBloom.setResultsDir(
                CURRENT_DIR + "\\src\\test\\results\\"
        );
        /** measurement for flow sizes **/
        for (int i : GeneralTimeGeneralSketchBloom.sizeMeasurementConfig) {
            GeneralTimeGeneralSketchBloom.initCM(i);
            GeneralTimeGeneralSketchBloom.encodeSize(dataStream.getDataStreamForFlowSize());
            GeneralTimeGeneralSketchBloom.estimateSize(dataStream.getDataSummaryForFlowSize());
        }

        /** measurment for flow spreads **/
        for (int i : GeneralTimeGeneralSketchBloom.spreadMeasurementConfig) {
            GeneralTimeGeneralSketchBloom.initCM(i);
            GeneralTimeGeneralSketchBloom.encodeSpread(dataStream.getDataStreamForFlowSpread());
            GeneralTimeGeneralSketchBloom.estimateSpread(dataStream.getDataSummaryForFlowSpread());
        }

        System.out.println("DONE!****************************");
    }


}
