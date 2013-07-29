package com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats;

import com.joffrey_bion.csv_epoch_synchronizer.k4b2.Sample;

enum StatsColumn {
    VO2(Sample.COL_VO2),
    VCO2(Sample.COL_VCO2),
    R(Sample.COL_R),
    VO2KG(Sample.COL_VO2KG),
    METS(Sample.COL_METS);
    
    public final int index;
    
    private StatsColumn(int column) {
        this.index = column;
    }
}