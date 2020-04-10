package org.yeastrc.limelight.xml.moda.objects;

public class TargetDecoyCountFactory {

    public static TargetDecoyCounts getTargetDecoyCountsByFinalScore(ModAResults modAResults) {

        TargetDecoyCounts tdCounts = new TargetDecoyCounts();

        for (ModAReportedPeptide pfrp : modAResults.getPeptidePSMMap().keySet()) {
            for (ModAPSM psm : modAResults.getPeptidePSMMap().get(pfrp)) {

                if (psm.isDecoy())
                    tdCounts.addDecoy(psm.getProbability());
                else
                    tdCounts.addTarget(psm.getProbability());

            }
        }

        return tdCounts;
    }
}
