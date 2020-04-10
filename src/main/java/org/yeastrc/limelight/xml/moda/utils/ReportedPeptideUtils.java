package org.yeastrc.limelight.xml.moda.utils;

import org.yeastrc.limelight.xml.moda.objects.ModAPSM;
import org.yeastrc.limelight.xml.moda.objects.ModAReportedPeptide;
import org.yeastrc.limelight.xml.moda.objects.ModAResults;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ReportedPeptideUtils {

    public static String getNakedPeptide(String reportedPeptide) {
        return reportedPeptide.replaceAll("[^A-Z]", "");
    }

    /**
     * Get a map of the reported mods in the reported peptide.
     *
     * Example peptide: DEGK+39ASSAKQRLKCASLQK
     *
     * @param reportedPeptideString
     * @return
     */
    public static Map<Integer, BigDecimal> getReportedMods(String reportedPeptideString) throws Exception {

        Map<Integer, BigDecimal> reportedMods = new HashMap<>();

        String currentMod = null;
        int currentPosition = 0;

        for( String c : reportedPeptideString.split("")) {

            if(c.equals("+")) {
                // starting to build a new mod

                if(currentMod != null) {
                    throw new Exception("Got unexpected mod value in: " + reportedPeptideString);
                }

                currentMod = "";
                continue;

            }

            if(c.equals("-")) {
                // starting to build a new mod

                if(currentMod != null) {
                    throw new Exception("Got unexpected mod value in: " + reportedPeptideString);
                }

                currentMod = "-";
                continue;
            }

            if(isLetter(c)) {

                // were we building a mod?
                if(currentMod != null) {
                    reportedMods.put(currentPosition, new BigDecimal(currentMod));
                    currentMod = null;
                }

                currentPosition++;
                continue;
            }

            if(isNumber(c)) {
                if(currentMod == null) {
                    throw new Exception("Got unexpected mod value in: " + reportedPeptideString);
                }

                currentMod += c;
                continue;
            }

            throw new Exception("Got unexpected mod value in: " + reportedPeptideString);
        }

        if(currentMod != null) {
            reportedMods.put(currentPosition, new BigDecimal(currentMod));
        }

        return reportedMods;
    }

    /**
     * Return true if s is a digit, false otherwise. s should be length 1
     *
     * @param s
     * @return
     */
    private static boolean isNumber(String s) {
        return Character.isDigit(s.charAt(0));
    }

    /**
     * Return true if s is a letter, false otherwise. s should be length 1
     *
     * @param s
     * @return
     */
    private static boolean isLetter(String s) {
        return Character.isLetter(s.charAt(0));
    }


    /**
     * Get the reported peptide from the long form peptide in the form of R.PEP+23TIDER.F
     * Would return PEP+23TIDER
     *
     * @param longFormPeptide
     * @return
     * @throws Exception
     */
    public static String getReportedPeptideFromLongFormPeptide(String longFormPeptide) throws Exception {
        String[] f = longFormPeptide.split("\\.");
        if(f.length != 3) {
            throw new Exception("Got unexpected form for long form peptide: " + longFormPeptide);
        }

        return f[1];
    }


    public static boolean reportedPeptideOnlyContainsDecoys(ModAResults pfindResults, ModAReportedPeptide pfindReportedPeptide ) {

        for(ModAPSM psm : pfindResults.getPeptidePSMMap().get(pfindReportedPeptide)) {

            if( !psm.isDecoy() ) {
                return false;
            }
        }

        return true;
    }

}
