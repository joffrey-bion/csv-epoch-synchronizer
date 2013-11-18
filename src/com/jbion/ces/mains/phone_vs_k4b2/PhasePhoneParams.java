package com.jbion.ces.mains.phone_vs_k4b2;

import com.jbion.ces.config.Config;
import com.jbion.ces.k4b2.Phase;
import com.jbion.ces.k4b2.stats.PhaseResults;
import com.jbion.ces.phone.PhoneRawToEpParams;
import com.jbion.ces.phone.decision.LabelAppenderParams;

public class PhasePhoneParams implements PhoneRawToEpParams, LabelAppenderParams {

    private PvKParams globalParams;
    private long windowWidthNano;
    private long epochWidthNano;
    private long phoneStartTime;
    private long phoneStopTime;
    private String phoneEpFilePath;
    private String phoneLabeledFilePath;

    public PhasePhoneParams(PvKParams params) {
        this.globalParams = params;
        this.windowWidthNano = Config.get().windowWidthSec * 1000 * 1000000;
        this.epochWidthNano = Config.get().epochWidthVsK4b2 * 1000 * 1000000;
    }

    public void setPhaseResults(Phase p, PhaseResults pr) {
        this.phoneStartTime = pr.getStartTime() * 1000000 - globalParams.delayPhoneToK4b2;
        this.phoneStopTime = pr.getEndTime() * 1000000 - globalParams.delayPhoneToK4b2;
        this.phoneEpFilePath = "temp-" + p + ".csv";
        this.phoneLabeledFilePath = "temp-" + p + "-labeled.csv";
    }

    @Override
    public long getPhoneStartTimeNano() {
        return phoneStartTime;
    }

    @Override
    public long getPhoneStopTimeNano() {
        return phoneStopTime;
    }

    @Override
    public long getDelayNano() {
        return globalParams.delayPhoneToK4b2;
    }

    @Override
    public long getWindowWidthNano() {
        return windowWidthNano;
    }

    @Override
    public long getEpochWidthNano() {
        return epochWidthNano;
    }

    @Override
    public long getWinBeginToEpBegin() {
        return (windowWidthNano - epochWidthNano) / 2;
    }

    @Override
    public String getInputFilePath() {
        return globalParams.phoneRawFile;
    }

    @Override
    public String getPhoneEpochFilePath() {
        return phoneEpFilePath;
    }

    @Override
    public String getClassifierFilePath() {
        return globalParams.classifierFile;
    }

    @Override
    public String getUnlabeledDatasetFilePath() {
        return getPhoneEpochFilePath();
    }

    @Override
    public String getLabeledDatasetFilePath() {
        return phoneLabeledFilePath;
    }

    @Override
    public boolean shouldRemoveTimestamps() {
        return true;
    }
}
