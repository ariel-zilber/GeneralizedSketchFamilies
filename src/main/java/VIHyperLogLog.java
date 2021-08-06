import utils.HashingUtils;

import java.util.BitSet;

public class VIHyperLogLog extends GeneralDataStructure {
    /**
     * parameters for HLL
     */

    public int HLLSize;                                // unit size for each register
    public int m;                                    // the number of registers in the HLL sketch
    public int maxRegisterValue;
    public double alpha;
    public BitSet[] HLLMatrix;

    public VIHyperLogLog(int m, int size) {
        super();
        HLLSize = size;
        this.m = m;
        maxRegisterValue = (int) (Math.pow(2, size) - 1);
        HLLMatrix = new BitSet[m];
        for (int i = 0; i < m; i++) {
            HLLMatrix[i] = new BitSet(size);
        }
        alpha = getAlpha(m);
    }

    @Override
    public String getDataStructureName() {
        return "HyperLogLog";
    }

    public BitSet[] getHLLs() {
        return HLLMatrix;
    }

    ;

    @Override
    public int getUnitSize() {
        return HLLSize * m;
    }

    @Override
    public int getValue() {
        return getValue(HLLMatrix);
    }

    @Override
    public GeneralDataStructure[] getsplit(int m, int w) {
        VIHyperLogLog[] B = new VIHyperLogLog[m];
        for (int i = 0; i < m; i++) {
            B[i] = new VIHyperLogLog(w, 5);
            for (int j = 0; j < w / m; j++)
                B[i].getHLLs()[j] = this.HLLMatrix[i * (w / m) + j];
        }
        return B;
    }

    @Override
    public void encode() {
        int r = HashingUtils.getSeed().nextInt(Integer.MAX_VALUE);
        int k = r % m;
        int hash_val = HashingUtils.intHash(r);
        int leadingZeros = Integer.numberOfLeadingZeros(hash_val) + 1;
        leadingZeros = Math.min(leadingZeros, maxRegisterValue);

        if (getBitSetValue(HLLMatrix[k]) < leadingZeros) {
            setBitSetValue(k, leadingZeros);
        }
    }

    @Override
    public void encode(String elementID) {
        int hash_val = HashingUtils.FNVHash1(elementID);
        int leadingZeros = Integer.numberOfLeadingZeros(hash_val) + 1;
        int k = (hash_val % m + m) % m;
        leadingZeros = Math.min(leadingZeros, maxRegisterValue);
        if (getBitSetValue(HLLMatrix[k]) < leadingZeros) {
            setBitSetValue(k, leadingZeros);
        }
    }

    @Override
    public void encode(int elementID) {
        int hash_val = HashingUtils.intHash(elementID);
        int leadingZeros = Integer.numberOfLeadingZeros(hash_val) + 1;
        int k = (hash_val % m + m) % m;
        leadingZeros = Math.min(leadingZeros, maxRegisterValue);
        if (getBitSetValue(HLLMatrix[k]) < leadingZeros) {
            setBitSetValue(k, leadingZeros);
        }
    }


    @Override
    public int getValue(String flowID, int[] s) {
        int ms = s.length;
        BitSet[] sketch = new BitSet[ms];
        for (int j = 0; j < ms; j++) {
            int i = (HashingUtils.intHash(HashingUtils.FNVHash1(flowID) ^ s[j]) % m + m) % m;
            sketch[j] = HLLMatrix[i];
        }
        return getValue(sketch);
    }

    public int getValue(long flowID, int[] s) {
        int ms = s.length;
        BitSet[] sketch = new BitSet[ms];
        for (int j = 0; j < ms; j++) {
            int i = (HashingUtils.intHash(HashingUtils.FNVHash1(flowID) ^ s[j]) % m + m) % m;
            sketch[j] = HLLMatrix[i];
        }
        return getValue(sketch);
    }

    /**
     * @param sketch L_f
     * @return
     */
    public int getValue(BitSet[] sketch) {
        Double sum = 0.0;
        int zeros = 0;
        int len = sketch.length;
        int lenSquared = len * 2;
        //xxx

        // for j =0..m-1 do
        //     sum := sum +2^-Lf[j]
        // end for

        for (int i = 0; i < len; i++) {
            if (getBitSetValue(sketch[i]) == 0) zeros++;
            sum = sum + Math.pow(2, -1.0 * getBitSetValue(sketch[i]));
        }

        sum = (alpha * lenSquared) / sum; //a_m m^2/sum
        if (sum <= 5.0 / 2.0 * len) {            // small flows
            sum = 1.0 * len * Math.log(1.0 * len / Math.max(zeros, 1));
        } else if (sum > Integer.MAX_VALUE / 30.0) {
            sum = -1.0 * Integer.MAX_VALUE * Math.log(1 - sum / Integer.MAX_VALUE);
        }
        return sum.intValue();
    }

    public static int getBitSetValue(BitSet b) {
        int res = 0;
        for (int i = 0; i < b.length(); i++) {
            if (b.get(i)) res += Math.pow(2, i);
        }
        return res;
    }

    public void setBitSetValue(int index, int value) {
        int i = 0;
        while (value != 0 && i < HLLSize) {
            if ((value & 1) != 0) {
                HLLMatrix[index].set(i);
            } else {
                HLLMatrix[index].clear(i);
            }
            value = value >>> 1;
            i++;
        }
    }

    public double getAlpha(int m) {
        double a;
        if (m == 16) {
            a = 0.673;
        } else if (m == 32) {
            a = 0.697;
        } else if (m == 64) {
            a = 0.709;
        } else {
            a = 0.7213 / (1 + 1.079 / m);
        }
        return a;
    }

    @Override
    public void encodeSegment(long flowID, long elementID, int[] s, int w) {
        int ms = s.length;

        int j = ((HashingUtils.FNVHash1(flowID ^ elementID)) % ms + ms) % ms;
        int k = (HashingUtils.intHash(HashingUtils.FNVHash1(flowID) ^ s[j]) % w + w) % w;
        int i = j * w + k;
        int hash_val = HashingUtils.FNVHash1(elementID);
        int leadingZeros = Integer.numberOfLeadingZeros(hash_val) + 1;
        leadingZeros = Math.min(leadingZeros, maxRegisterValue);
        if (getBitSetValue(HLLMatrix[i]) < leadingZeros) {
            setBitSetValue(i, leadingZeros);
        }

    }

    @Override
    public void encodeSegment(String flowID, String elementID, int[] s, int w) {
        int ms = s.length;
        int j = ((HashingUtils.FNVHash1(elementID) ^ HashingUtils.FNVHash1(flowID)) % ms + ms) % ms;
        int k = (HashingUtils.intHash(HashingUtils.FNVHash1(flowID) ^ s[j]) % w + w) % w;
        int i = j * w + k;
        int hash_val = HashingUtils.FNVHash1(elementID);
        int leadingZeros = Integer.numberOfLeadingZeros(hash_val) + 1;
        leadingZeros = Math.min(leadingZeros, maxRegisterValue);
        if (getBitSetValue(HLLMatrix[i]) < leadingZeros) {
            setBitSetValue(i, leadingZeros);
        }
    }

    @Override
    public void encodeSegment(int flowID, int elementID, int[] s, int w) {
        int ms = s.length;
        int j = (HashingUtils.intHash(elementID) % ms + ms) % ms;
        int k = (HashingUtils.intHash(flowID ^ s[j]) % w + w) % w;
        int i = j * w + k;
        int hash_val = HashingUtils.intHash(elementID);
        int leadingZeros = Integer.numberOfLeadingZeros(hash_val) + 1;
        leadingZeros = Math.min(leadingZeros, maxRegisterValue);
        if (getBitSetValue(HLLMatrix[i]) < leadingZeros) {
            setBitSetValue(i, leadingZeros);
        }
    }

    @Override
    public void encodeSegment(String flowID, int[] s, int w) {
        int ms = s.length;
        int r = HashingUtils.getSeed().nextInt(Integer.MAX_VALUE);
        int j = r % ms;
        int k = (HashingUtils.intHash(HashingUtils.FNVHash1(flowID) ^ s[j]) % w + w) % w;
        int i = j * w + k;
        int hash_val = HashingUtils.FNVHash1(String.valueOf(r));
        int leadingZeros = Integer.numberOfLeadingZeros(hash_val) + 1;
        leadingZeros = Math.min(leadingZeros, maxRegisterValue);
        if (getBitSetValue(HLLMatrix[i]) < leadingZeros) {
            setBitSetValue(i, leadingZeros);
        }
    }

    @Override
    public void encodeSegment(long flowID, int[] s, int w) {
        int ms = s.length;
        int r = HashingUtils.getSeed().nextInt(Integer.MAX_VALUE);
        int j = r % ms;
        int k = (HashingUtils.intHash(HashingUtils.FNVHash1(flowID) ^ s[j]) % w + w) % w;
        int i = j * w + k;
        int hash_val = HashingUtils.FNVHash1(String.valueOf(r));
        int leadingZeros = Integer.numberOfLeadingZeros(hash_val) + 1;
        leadingZeros = Math.min(leadingZeros, maxRegisterValue);
        if (getBitSetValue(HLLMatrix[i]) < leadingZeros) {
            setBitSetValue(i, leadingZeros);
        }
    }

    @Override
    public void encodeSegment(int flowID, int[] s, int w) {
        int ms = s.length;
        int r = HashingUtils.getSeed().nextInt(Integer.MAX_VALUE);
        int j = r % ms;
        int k = (HashingUtils.intHash(flowID ^ s[j]) % w + w) % w;
        int i = j * w + k;
        int hash_val = HashingUtils.intHash(r);
        int leadingZeros = Integer.numberOfLeadingZeros(hash_val) + 1;
        leadingZeros = Math.min(leadingZeros, maxRegisterValue);
        if (getBitSetValue(HLLMatrix[i]) < leadingZeros) {
            setBitSetValue(i, leadingZeros);
        }
    }

    @Override
    public int getValueSegment(String flowID, int[] s, int w) {
        BitSet[] sketch = getLogicalEstimator(flowID, s, w);
        return getValue(sketch);
    }

    @Override
    public int getValueSegment(long flowID, int[] s, int w) {
        BitSet[] sketch = getLogicalEstimator(flowID, s, w);
        return getValue(sketch);
    }

    public BitSet[] getLogicalEstimator(long flowID, int[] s, int w) {

        int ms = s.length;
        BitSet[] sketch = new BitSet[ms];
        for (int j = 0; j < ms; j++) {
            int i = j * w + (HashingUtils.intHash(HashingUtils.FNVHash1(flowID) ^ s[j]) % w + w) % w;
            sketch[j] = HLLMatrix[i];
        }
        return sketch;
    }


    public BitSet[] getLogicalEstimator(String flowID, int[] s, int w) {

        int ms = s.length;
        BitSet[] sketch = new BitSet[ms];
        for (int j = 0; j < ms; j++) {
            int i = j * w + (HashingUtils.intHash(HashingUtils.FNVHash1(flowID) ^ s[j]) % w + w) % w;
            sketch[j] = HLLMatrix[i];
        }
        return sketch;
    }


    @Override
    public GeneralDataStructure join(GeneralDataStructure gds) {
        VIHyperLogLog hll = (VIHyperLogLog) gds;
        for (int i = 0; i < m; i++) {
            if (getBitSetValue(HLLMatrix[i]) < getBitSetValue(hll.HLLMatrix[i]))
                HLLMatrix[i] = hll.HLLMatrix[i];
        }
        return this;
    }

    @Override
    public GeneralDataStructure join(GeneralDataStructure gds, int w, int i) {
        VIHyperLogLog hll = (VIHyperLogLog) gds;
        for (int j = 0; j < w; j++) {
            if (getBitSetValue(HLLMatrix[i]) < getBitSetValue(hll.HLLMatrix[i * w + j]))
                HLLMatrix[i] = hll.HLLMatrix[i * w + j];
        }
        return this;
    }


    public GeneralDataStructure intesection(GeneralDataStructure gds) {
        VIHyperLogLog hll = (VIHyperLogLog) gds;
        for (int i = 0; i < m; i++) {
            if (getBitSetValue(HLLMatrix[i]) > getBitSetValue(hll.HLLMatrix[i]))
                HLLMatrix[i] = hll.HLLMatrix[i];
        }
        return this;
    }

    public GeneralDataStructure intesection(GeneralDataStructure gds, int w, int[] s, long flowID) {
        VIHyperLogLog hll = (VIHyperLogLog) gds;
        BitSet[] sketch = hll.getLogicalEstimator(flowID, s, w);

        for (int i = 0; i < m; i++) {
            if (getBitSetValue(HLLMatrix[i]) > getBitSetValue(sketch[i]))
                HLLMatrix[i] = sketch[i];
        }

        return this;
    }
}
