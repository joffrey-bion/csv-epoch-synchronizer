package com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_k4b2;

import com.joffrey_bion.csv_epoch_synchronizer.config.Config;
import com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats.PhaseResults;
import com.joffrey_bion.csv_epoch_synchronizer.phone.PhoneRawToEpParams;

public class PhasePhoneParams implements PhoneRawToEpParams {

    private static final long PHONE_EPOCH_WIDTH_SEC = 2;

    private long windowWidthNano;
    private long epochWidthNano;
    private long winBeginToEpBegin;
    private long delayPhoneToK4b2;
    private long phoneStartTime;
    private long phoneStopTime;

    public PhasePhoneParams(long delayPhoneToK4b2) {
        this.delayPhoneToK4b2 = delayPhoneToK4b2;
        this.windowWidthNano = Config.get().windowWidthSec * 1000 * 1000000;
        this.epochWidthNano = PHONE_EPOCH_WIDTH_SEC * 1000 * 1000000;
        this.winBeginToEpBegin = (windowWidthNano - epochWidthNano) / 2;
    }

    public void setPhaseResults(PhaseResults p) {
        this.phoneStartTime = p.getStartTime() - delayPhoneToK4b2;
        this.phoneStopTime = p.getEndTime() - delayPhoneToK4b2;
    }
    
    @Override
    public long getPhoneStartTime() {
        return phoneStartTime;
    }

    @Override
    public long getPhoneStopTime() {
        return phoneStopTime;
    }

    @Override
    public long getDelay() {
        return delayPhoneToK4b2;
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
        return winBeginToEpBegin;
    }
}
