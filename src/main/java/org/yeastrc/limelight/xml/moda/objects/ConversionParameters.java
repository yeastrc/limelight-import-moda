package org.yeastrc.limelight.xml.moda.objects;

import java.io.File;

public class ConversionParameters {

    public ConversionParameters(File dataTxtFile, File paramsFile, File fastaFilePath, String outputFilePath, String decoyPrefix, ConversionProgramInfo conversionProgramInfo) {
        this.dataTxtFile = dataTxtFile;
        this.paramsFile = paramsFile;
        this.fastaFilePath = fastaFilePath;
        this.outputFilePath = outputFilePath;
        this.decoyPrefix = decoyPrefix;
        this.conversionProgramInfo = conversionProgramInfo;
    }

    public File getDataTxtFile() {
        return dataTxtFile;
    }

    public File getParamsFile() {
        return paramsFile;
    }

    public String getDecoyPrefix() {
        return decoyPrefix;
    }

    public File getFastaFilePath() {
        return fastaFilePath;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public ConversionProgramInfo getConversionProgramInfo() {
        return conversionProgramInfo;
    }

    private File dataTxtFile;
    private File paramsFile;
    private File fastaFilePath;
    private String outputFilePath;
    private String decoyPrefix;

    private ConversionProgramInfo conversionProgramInfo;

}
