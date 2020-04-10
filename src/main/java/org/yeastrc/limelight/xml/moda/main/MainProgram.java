/*
 * Original author: Michael Riffle <mriffle .at. uw.edu>
 *                  
 * Copyright 2018 University of Washington - Seattle, WA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.yeastrc.limelight.xml.moda.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.yeastrc.limelight.xml.moda.constants.Constants;
import org.yeastrc.limelight.xml.moda.objects.ConversionParameters;
import org.yeastrc.limelight.xml.moda.objects.ConversionProgramInfo;

import picocli.CommandLine;

@CommandLine.Command(name = "java -jar " + Constants.CONVERSION_PROGRAM_NAME,
		mixinStandardHelpOptions = true,
		version = Constants.CONVERSION_PROGRAM_NAME + " " + Constants.CONVERSION_PROGRAM_VERSION,
		sortOptions = false,
		synopsisHeading = "%n",
		descriptionHeading = "%n@|bold,underline Description:|@%n%n",
		optionListHeading = "%n@|bold,underline Options:|@%n",
		description = "Convert the results of a MODa analysis to a Limelight XML file suitable for import into Limelight.\n\n" +
				"More info at: " + Constants.CONVERSION_PROGRAM_URI
)
public class MainProgram implements Runnable {

	@CommandLine.Option(names = { "-t", "--data-txt-file" }, required = true, description = "Full path to the .txt file containing the MODa PSM results.")
	private File dataFile;

	@CommandLine.Option(names = { "-f", "--fasta-file" }, required = true, description = "Full path to FASTA file used in the experiment.")
	private File fastaFile;

	@CommandLine.Option(names = { "-p", "--params-file" }, required = true, description = "Full path to MODa params file.")
	private File paramsFile;

	@CommandLine.Option(names = { "-o", "--out-file" }, required = true, description = "Full path to use for the Limelight XML output file (including file name).")
	private String outFile;

	@CommandLine.Option(names = { "-d", "--decoy-prefix" }, required = false, description = "A prefix string that indicates a decoy proteins, e.g., random_.")
	private String decoyPrefix;

	@CommandLine.Option(names = { "-v", "--verbose" }, required = false, description = "If this parameter is present, error messages will include a full stacktrace. Helpful for debugging.")
	private boolean verboseRequested = false;

	private String[] args;

	public void run() {

		printRuntimeInfo();

		if( !fastaFile.exists() ) {
			System.err.println( "Could not find fasta file: " + fastaFile.getAbsolutePath() );
			System.exit( 1 );
		}

		if( !dataFile.exists() ) {
			System.err.println( "Could not find txt data file: " + dataFile.getAbsolutePath() );
			System.exit( 1 );
		}

		if( !paramsFile.exists() ) {
			System.err.println( "Could not find params file: " + paramsFile.getAbsolutePath() );
			System.exit( 1 );
		}

		ConversionProgramInfo cpi = ConversionProgramInfo.createInstance( String.join( " ",  args ) );
		ConversionParameters cp = new ConversionParameters(dataFile, paramsFile, fastaFile, outFile, decoyPrefix, cpi);

		try {
			ConverterRunner.createInstance().convertOpenPfindToLimelightXML(cp);
		} catch( Throwable t ) {

			if(verboseRequested) {
				t.printStackTrace();
			}

			System.err.println( "Encountered error during conversion: " + t.getMessage() );
			System.exit( 1 );
		}

		System.exit( 0 );
	}

	public static void main( String[] args ) {

		MainProgram mp = new MainProgram();
		mp.args = args;

		CommandLine.run(mp, args);
	}

	/**
	 * Print runtime info to STD ERR
	 * @throws Exception 
	 */
	public static void printRuntimeInfo() {

		try( BufferedReader br = new BufferedReader( new InputStreamReader( MainProgram.class.getResourceAsStream( "run.txt" ) ) ) ) {

			String line = null;
			while ( ( line = br.readLine() ) != null ) {

				line = line.replace( "{{URL}}", Constants.CONVERSION_PROGRAM_URI );
				line = line.replace( "{{VERSION}}", Constants.CONVERSION_PROGRAM_VERSION );

				System.err.println( line );
				
			}
			
			System.err.println( "" );

		} catch( Exception e) {
			;
		}
	}

}
