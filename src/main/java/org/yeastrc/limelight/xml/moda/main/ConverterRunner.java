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

import org.yeastrc.limelight.xml.moda.builder.XMLBuilder;
import org.yeastrc.limelight.xml.moda.objects.*;
import org.yeastrc.limelight.xml.moda.reader.*;

import java.math.BigDecimal;
import java.util.Map;

public class ConverterRunner {

	// conveniently get a new instance of this class
	public static ConverterRunner createInstance() { return new ConverterRunner(); }
	
	
	public void convertOpenPfindToLimelightXML(ConversionParameters conversionParameters ) throws Throwable {

		System.err.print( "\nLoading MODa params..." );
		Map<String, BigDecimal> staticMods = ModAParamsFileReader.getStaticMods(conversionParameters.getParamsFile());
		System.err.println( " Done." );

		System.err.print( "\nLoading MODa results into memory..." );
		ModAResults results = ModAResultsReader.getModAResults( conversionParameters.getDataTxtFile(), conversionParameters.getDecoyPrefix() );
		System.err.println( " Found " + results.getPeptidePSMMap().keySet().size() + " distinct peptides..." );

		System.err.print( "Performing FDR analysis of Probabilities..." );
		TargetDecoyCounts tdCounts = TargetDecoyCountFactory.getTargetDecoyCountsByFinalScore( results );
		TargetDecoyAnalysis tdAnalysis = TargetDecoyAnalysisFactory.createTargetDecoyAnalysis( tdCounts, TargetDecoyAnalysisFactory.HIGHER_IS_BETTER );
		System.err.println( " Done." );

		System.err.print( "\nWriting out XML..." );
		(new XMLBuilder()).buildAndSaveXML(conversionParameters, results, staticMods, tdAnalysis);
		System.err.println( " Done." );

	}
}
