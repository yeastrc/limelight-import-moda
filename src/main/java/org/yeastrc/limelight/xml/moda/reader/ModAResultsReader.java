package org.yeastrc.limelight.xml.moda.reader;

import org.yeastrc.limelight.xml.moda.objects.ModAPSM;
import org.yeastrc.limelight.xml.moda.objects.ModAReportedPeptide;
import org.yeastrc.limelight.xml.moda.objects.ModAResults;
import org.yeastrc.limelight.xml.moda.utils.ReportedPeptideUtils;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class ModAResultsReader {

    public static ModAResults getModAResults(File dataFile, String decoyPrefix) throws Throwable {

        ModAResults results = new ModAResults();
        Map<ModAReportedPeptide, Collection<ModAPSM>> psmMap = new HashMap<>();


        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {

            // skip first line
            results.setVersion(br.readLine());
            br.readLine();  // get rid of blank line

            String line = br.readLine();
            while(line != null) {

                if(!line.startsWith(">>")) {
                    throw new Exception("Expected a \">>\" line. Got: " + line);
                }

                line = line.replace(">>", "");
                String[] fields = line.split("\\s+");

                if(fields.length != 5) {
                    throw new Exception("Expected 5 fields, got " + fields.length + " In line: " + line);
                }

                String spectralFile = new File(fields[0]).getName();
                BigDecimal precusorMass = new BigDecimal(fields[2]);
                int charge = Integer.parseInt(fields[3]);
                int scanNumber = Integer.parseInt(fields[4]);

                line = br.readLine();
                int rank = 0;
                while( line != null && !line.equals("")) {

                    rank++;

                    fields = line.split("\\s");
                    if(fields.length != 7) {
                        throw new Exception("Expected 7 fields, got " + fields.length + " In line " + line);
                    }

                    BigDecimal deltaMass = new BigDecimal(fields[1]);
                    BigDecimal score = (new BigDecimal(fields[2])).setScale(0, RoundingMode.HALF_UP);
                    BigDecimal probability = new BigDecimal(fields[3]);
                    String longFormReportedPeptide = fields[4];
                    String proteinName = fields[5];

                    Collection<String> proteinMatches = new HashSet<>();
                    proteinMatches.add(proteinName);

                    String reportedPeptide = ReportedPeptideUtils.getReportedPeptideFromLongFormPeptide(longFormReportedPeptide);
                    Map<Integer, BigDecimal> mods = ReportedPeptideUtils.getReportedMods(reportedPeptide);

                    // create reported peptide
                    ModAReportedPeptide modAReportedPeptide = new ModAReportedPeptide();
                    modAReportedPeptide.setReportedPeptideString(reportedPeptide);
                    modAReportedPeptide.setNakedPeptide(ReportedPeptideUtils.getNakedPeptide(reportedPeptide));
                    modAReportedPeptide.setMods( mods);
                    modAReportedPeptide.setProteinMatches(proteinMatches);

                    Collection<ModAPSM> psms = null;
                    if(psmMap.containsKey(modAReportedPeptide)) {
                        psms = psmMap.get(modAReportedPeptide);
                    } else {
                        psms = new HashSet<>();
                        psmMap.put(modAReportedPeptide, psms);
                    }

                    // create psm
                    ModAPSM psm = new ModAPSM();
                    psm.setCharge( charge );

                    if(decoyPrefix != null)
                        psm.setDecoy(proteinName.startsWith(decoyPrefix));
                    else
                        psm.setDecoy(false);

                    psm.setDeltaMass(deltaMass);
                    psm.setModifications(mods);
                    psm.setPeptideSequence(ReportedPeptideUtils.getNakedPeptide(reportedPeptide));
                    psm.setPrecursorNeutralMass(precusorMass);
                    psm.setProbability(probability);
                    psm.setProteinNames(proteinMatches);
                    psm.setScanNumber(scanNumber);
                    psm.setScore(score);
                    psm.setRank((new BigDecimal(rank)).setScale(0, RoundingMode.HALF_UP));

                    psms.add(psm);

                    line = br.readLine();
                }

                line = br.readLine();
            }
        }

        results.setPeptidePSMMap( psmMap );

        return results;
    }

}
