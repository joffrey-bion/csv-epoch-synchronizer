package org.hildan.waterloo.ces.k4b2;

import java.util.HashMap;

import org.hildan.waterloo.ces.k4b2.stats.PhaseResults;
import org.hildan.waterloo.ces.k4b2.stats.RestingResults;

/**
 * Contains the results associated to each phase listed in the enum {@link Phase}.
 *
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
@SuppressWarnings("serial")
public class Results extends HashMap<Phase, PhaseResults> {

    @Override
    public PhaseResults put(Phase phase, PhaseResults results) {
        if (phase == Phase.RESTING && !(results instanceof RestingResults)) {
            throw new IllegalArgumentException("The resting must be assigned resting results.");
        }
        return super.put(phase, results);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Phase p : Phase.values()) {
            sb.append(p);
            sb.append(" ");
            sb.append(get(p));
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

}
