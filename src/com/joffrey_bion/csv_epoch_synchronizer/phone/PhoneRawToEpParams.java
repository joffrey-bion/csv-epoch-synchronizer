package com.joffrey_bion.csv_epoch_synchronizer.phone;

public interface PhoneRawToEpParams {

    public long getPhoneStartTime();

    public long getPhoneStopTime();

    public long getDelay();

    public long getWindowWidthNano();

    public long getEpochWidthNano();

    public long getWinBeginToEpBegin();

}
