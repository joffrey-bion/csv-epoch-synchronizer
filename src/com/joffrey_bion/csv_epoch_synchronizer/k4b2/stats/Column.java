package com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats;

import com.joffrey_bion.csv_epoch_synchronizer.k4b2.K4b2CsvReader;

public enum Column {
    VO2(K4b2CsvReader.COL_VO2),
    VCO2(K4b2CsvReader.COL_VCO2),
    R(K4b2CsvReader.COL_R),
    VO2KG(K4b2CsvReader.COL_VO2KG);
    
    public final int index;
    
    private Column(int column) {
        this.index = column;
    }
}