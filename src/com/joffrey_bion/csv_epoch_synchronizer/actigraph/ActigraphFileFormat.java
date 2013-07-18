package com.joffrey_bion.csv_epoch_synchronizer.actigraph;

public enum ActigraphFileFormat {
    EXPORTED("hh:mm:ss a", 11, 11),
    CONVERTED("HH:mm:ss", 5, 2);
    
    public final String DATE_FORMAT;
    public final String TIME_FORMAT;
    public final int DATE_COL;
    public final int TIME_COL;
    public final int VM_COL;
    public final int NB_HEADER_LINES;

    private ActigraphFileFormat(String timeFormat, int VMcol, int nbHeaderLines) {
        this.DATE_FORMAT = "M/d/yyyy";
        this.TIME_FORMAT = timeFormat;
        this.DATE_COL = 0;
        this.TIME_COL = 1;
        this.VM_COL = VMcol;
        this.NB_HEADER_LINES = nbHeaderLines;
    }
}