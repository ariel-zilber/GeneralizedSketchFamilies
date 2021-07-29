
public class DataStream {
    private String dataStreamForFlowSize;
    private String dataSummaryForFlowSize;
    private String dataStreamForFlowSpread;
    private String dataSummaryForFlowSpread;

    public DataStream(String dataStreamForFlowSize,
                      String dataSummaryForFlowSize,
                      String dataStreamForFlowSpread,
                      String dataSummaryForFlowSpread

    ) {
        this.setDataStreamForFlowSize(dataStreamForFlowSize);
        this.setDataSummaryForFlowSize(dataSummaryForFlowSize);
        this.setDataStreamForFlowSpread(dataStreamForFlowSpread);
        this.setDataSummaryForFlowSpread(dataSummaryForFlowSpread);
    }

    public void setDataStreamForFlowSize(String dataStreamForFlowSize) {
        this.dataStreamForFlowSize = dataStreamForFlowSize;
    }

    public void setDataSummaryForFlowSize(String dataSummaryForFlowSize) {
        this.dataSummaryForFlowSize = dataSummaryForFlowSize;
    }

    public void setDataStreamForFlowSpread(String dataStreamForFlowSpread) {
        this.dataStreamForFlowSpread = dataStreamForFlowSpread;
    }

    public void setDataSummaryForFlowSpread(String dataSummaryForFlowSpread) {
        this.dataSummaryForFlowSpread = dataSummaryForFlowSpread;
    }

    public String getDataStreamForFlowSize() {
        return dataStreamForFlowSize;
    }

    public String getDataSummaryForFlowSize() {
        return dataSummaryForFlowSize;
    }

    public String getDataStreamForFlowSpread() {
        return dataStreamForFlowSpread;
    }

    public String getDataSummaryForFlowSpread() {
        return dataSummaryForFlowSpread;
    }

}
