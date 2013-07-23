package com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats;

import com.joffrey_bion.csv_epoch_synchronizer.k4b2.K4b2Sample;

enum K4b2StatsColumn {
    VO2(K4b2Sample.COL_VO2),
    VCO2(K4b2Sample.COL_VCO2),
    R(K4b2Sample.COL_R),
    VO2KG(K4b2Sample.COL_VO2KG);
    
    public final int index;
    
    private K4b2StatsColumn(int column) {
        this.index = column;
    }
}