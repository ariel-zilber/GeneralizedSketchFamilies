import java.io.FileNotFoundException;


public class GeneralUnionGeneralSketchBloomTests {
    public static final String CURRENT_DIR = System.getProperty("user.dir");

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Start****************************");
        DataStream dataStream = new DataStream(
                CURRENT_DIR + "\\src\\test\\resources\\data\\union\\",
                CURRENT_DIR + "\\src\\test\\resources\\data\\union\\srcdstsize.txt",
                CURRENT_DIR + "\\src\\test\\resources\\data\\union\\",
                CURRENT_DIR + "\\src\\test\\resources\\data\\union\\dstspread.txt"
        );

        GeneralUnionGeneralSketchBloom.setResultsDir(
                CURRENT_DIR + "\\src\\test\\results\\"
        );


        for (int i : GeneralUnionGeneralSketchBloom.sizeMeasurementConfig) {
            GeneralUnionGeneralSketchBloom. initCM(i);
            GeneralUnionGeneralSketchBloom.  encodeSize(dataStream.getDataStreamForFlowSize());
            GeneralUnionGeneralSketchBloom.   estimateSize(dataStream.getDataSummaryForFlowSize());
        }

        /** measurment for flow spreads **/
        for (int i : GeneralUnionGeneralSketchBloom.spreadMeasurementConfig) {
            GeneralUnionGeneralSketchBloom.     initCM(i);
            GeneralUnionGeneralSketchBloom.    encodeSpread(dataStream.getDataStreamForFlowSpread());
            GeneralUnionGeneralSketchBloom.    estimateSpread(dataStream.getDataSummaryForFlowSpread());
        }


        System.out.println("DONE!****************************");
    }


}
