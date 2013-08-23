package com.joffrey_bion.csv_epoch_synchronizer.actigraph;

/**
 * Contains some constants describing the format of an actigraph file. Two formats
 * exist so far:
 * <ul>
 * <li> {@link #EXPORTED} is the format of the actigraph file exported via the
 * DataScoring details on Actilife (see documentation).</li>
 * <li> {@link #CONVERTED} is the format of the actigraph file directly converted via
 * {@code File>Import/Export/Convert} on Actilife.</li>
 * </ul>
 * 
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
public enum ActigraphFileFormat {

    CONVERTED("HH:mm:ss", 11, 11),
    EXPORTED("hh:mm:ss a", 5, 2);

    /**
     * The format of the timestamps in the Date column.
     */
    public final String DATE_FORMAT;
    /**
     * The format of the timestamps in the Time column.
     */
    public final String TIME_FORMAT;
    /**
     * The number of the Date column.
     */
    public final int DATE_COL;
    /**
     * The number of the Time column.
     */
    public final int TIME_COL;
    /**
     * The number of the VM (vector magnitude) column.
     */
    public final int VM_COL;
    /**
     * The number of lines before the actual values.
     */
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