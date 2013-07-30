package com.joffrey_bion.csv_epoch_synchronizer.k4b2;

/**
 * An enum to list the different phases expected in a K4b2 test. Each phase should be
 * delimited by 2 markers (start and end) in the K4b2 file.
 * 
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
public enum Phase {
    RESTING,
    SITTING,
    STANDING,
    WALKING1,
    WALKING2,
    WALKING3,
    STAIRS1,
    STAIRS2;
}