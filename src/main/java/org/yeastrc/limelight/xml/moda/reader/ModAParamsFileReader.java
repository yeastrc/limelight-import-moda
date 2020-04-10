package org.yeastrc.limelight.xml.moda.reader;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModAParamsFileReader {

    public static Map<String, BigDecimal> getStaticMods(File paramsFile) throws IOException {

        Map<String, BigDecimal> modMap = new HashMap<>();

        Pattern p = Pattern.compile("^\\s*ADD=([A-Z]),\\s+(.+)\\s*$");

        try(BufferedReader br = new BufferedReader(new FileReader( paramsFile))) {
            String line = br.readLine();

            while( line != null ) {
                Matcher m = p.matcher(line);
                if( m.matches() ) {
                    modMap.put(m.group(1), new BigDecimal(m.group(2)));
                }

                line = br.readLine();
            }
        }

        return modMap;
    }

}
