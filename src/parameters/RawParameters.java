package parameters;

public class RawParameters {
    
    public static final int NB_MAX_SPIKES = 6;

    public String phoneRawFile;
    public String actigEpFile;
    public String outputFile;

    public String windowWidthSec;
    public String epochWidthSec;
    public String startTime;
    public String stopTime;
    public boolean deleteIntermediateFile;
    public String[] phoneSpikes;
    public String[] actigraphSpikes;
}
