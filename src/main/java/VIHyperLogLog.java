import utils.HashingUtils;

import java.util.BitSet;

public class VIHyperLogLog extends GeneralDataStructure {
    /** parameters for HLL */

    public int HLLSize; 								// unit size for each register
    public int m;									// the number of registers in the HLL sketch
    public int maxRegisterValue;
    public double alpha;
    public BitSet[] HLLMatrix;

    public VIHyperLogLog(int m, int size) {
      // TODO
    }

    @Override
    public String getDataStructureName() {
        return "VIHyperLogLog";
    }

    public BitSet[] getHLLs() {
        return HLLMatrix;
    };

    @Override
    public int getUnitSize() {
        return HLLSize*m;
    }

    @Override
    public int getValue() {
        return getValue(HLLMatrix);
    }
    @Override
    public GeneralDataStructure[] getsplit(int m,int w) {
        // TODO

    }
    @Override
    public void encode() {
        // TODO

    }

    @Override
    public void encode(String elementID) {
        // TODO

    }

    @Override
    public void encode(int elementID) {
        // TODO

    }


    @Override
    public int getValue(String flowID, int[] s) {
        // TODO

    }

    public int getValue(long flowID, int[] s) {
        // TODO

    }

    public int getValue(BitSet[] sketch) {
        // TODO

    }

    public static int getBitSetValue(BitSet b) {
        // TODO

    }

    public void setBitSetValue(int index, int value) {
        // TODO

    }

    public double getAlpha(int m) {
        // TODO

    }
    @Override
    public void encodeSegment(long flowID, long elementID, int[] s, int w) {

        // TODO

    }
    @Override
    public void encodeSegment(String flowID, String elementID, int[] s, int w) {
        // TODO

    }

    @Override
    public void encodeSegment(int flowID, int elementID, int[] s, int w) {
        // TODO

    }

    @Override
    public void encodeSegment(String flowID, int[] s, int w) {
        // TODO

    }

    @Override
    public void encodeSegment(long flowID, int[] s, int w) {
        // TODO

    }

    @Override
    public void encodeSegment(int flowID, int[] s, int w) {
        // TODO

    }

    @Override
    public int getValueSegment(String flowID, int[] s, int w) {
        // TODO

    }
    @Override
    public int getValueSegment(long flowID, int[] s, int w) {
        // TODO

    }


    @Override
    public GeneralDataStructure join(GeneralDataStructure gds) {
        // TODO

    }
    @Override
    public GeneralDataStructure join(GeneralDataStructure gds,int w,int i) {
        // TODO

    }
}
