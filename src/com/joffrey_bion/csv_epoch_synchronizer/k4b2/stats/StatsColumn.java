package com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats;

import com.joffrey_bion.csv_epoch_synchronizer.k4b2.Sample;

/**
 * An enum listing the columns we are interested in. The statistics will be computed
 * on these columns only.
 * 
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
enum StatsColumn {
    VO2(Sample.COL_VO2),
    VCO2(Sample.COL_VCO2),
    R(Sample.COL_R),
    VO2KG(Sample.COL_VO2KG),
    METS(Sample.COL_METS);

    /**
     * The index of this column in the K4b2 file.
     */
    public final int index;

    /**
     * Creates a new {@code StatsColumn}.
     * 
     * @param column
     *            The index of this column in the K4b2 file.
     */
    private StatsColumn(int column) {
        this.index = column;
    }
}