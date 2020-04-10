package org.yeastrc.limelight.xml.moda.builder;

import org.yeastrc.limelight.limelight_import.api.xml_dto.*;
import org.yeastrc.limelight.limelight_import.api.xml_dto.SearchProgram.PsmAnnotationTypes;
import org.yeastrc.limelight.limelight_import.create_import_file_from_java_objects.main.CreateImportFileFromJavaObjectsMain;
import org.yeastrc.limelight.xml.moda.annotation.PSMAnnotationTypeSortOrder;
import org.yeastrc.limelight.xml.moda.annotation.PSMAnnotationTypes;
import org.yeastrc.limelight.xml.moda.annotation.PSMDefaultVisibleAnnotationTypes;
import org.yeastrc.limelight.xml.moda.constants.Constants;
import org.yeastrc.limelight.xml.moda.objects.*;
import org.yeastrc.limelight.xml.moda.utils.ReportedPeptideUtils;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.Map;

public class XMLBuilder {

	public void buildAndSaveXML(ConversionParameters conversionParameters,
								ModAResults results,
								Map<String,BigDecimal> staticMods,
								TargetDecoyAnalysis tdAnalysis)
    throws Exception {

		LimelightInput limelightInputRoot = new LimelightInput();

		limelightInputRoot.setFastaFilename( conversionParameters.getFastaFilePath().getName() );

		// add in the conversion program (this program) information
		ConversionProgramBuilder.createInstance().buildConversionProgramSection( limelightInputRoot, conversionParameters);
		
		SearchProgramInfo searchProgramInfo = new SearchProgramInfo();
		limelightInputRoot.setSearchProgramInfo( searchProgramInfo );
		
		SearchPrograms searchPrograms = new SearchPrograms();
		searchProgramInfo.setSearchPrograms( searchPrograms );

		{
			SearchProgram searchProgram = new SearchProgram();
			searchPrograms.getSearchProgram().add( searchProgram );

			searchProgram.setName( Constants.PROGRAM_NAME_MODA);
			searchProgram.setDisplayName( Constants.PROGRAM_NAME_MODA);
			searchProgram.setVersion(results.getVersion());


			//
			// Define the annotation types present in tide data
			//
			PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
			searchProgram.setPsmAnnotationTypes( psmAnnotationTypes );

			FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = new FilterablePsmAnnotationTypes();
			psmAnnotationTypes.setFilterablePsmAnnotationTypes( filterablePsmAnnotationTypes );

			for( FilterablePsmAnnotationType annoType : PSMAnnotationTypes.getFilterablePsmAnnotationTypes() ) {
				filterablePsmAnnotationTypes.getFilterablePsmAnnotationType().add( annoType );
			}
		}

		//
		// Define which annotation types are visible by default
		//
		DefaultVisibleAnnotations xmlDefaultVisibleAnnotations = new DefaultVisibleAnnotations();
		searchProgramInfo.setDefaultVisibleAnnotations( xmlDefaultVisibleAnnotations );
		
		VisiblePsmAnnotations xmlVisiblePsmAnnotations = new VisiblePsmAnnotations();
		xmlDefaultVisibleAnnotations.setVisiblePsmAnnotations( xmlVisiblePsmAnnotations );

		for( SearchAnnotation sa : PSMDefaultVisibleAnnotationTypes.getDefaultVisibleAnnotationTypes() ) {
			xmlVisiblePsmAnnotations.getSearchAnnotation().add( sa );
		}
		
		//
		// Define the default display order in proxl
		//
		AnnotationSortOrder xmlAnnotationSortOrder = new AnnotationSortOrder();
		searchProgramInfo.setAnnotationSortOrder( xmlAnnotationSortOrder );
		
		PsmAnnotationSortOrder xmlPsmAnnotationSortOrder = new PsmAnnotationSortOrder();
		xmlAnnotationSortOrder.setPsmAnnotationSortOrder( xmlPsmAnnotationSortOrder );
		
		for( SearchAnnotation xmlSearchAnnotation : PSMAnnotationTypeSortOrder.getPSMAnnotationTypeSortOrder() ) {
			xmlPsmAnnotationSortOrder.getSearchAnnotation().add( xmlSearchAnnotation );
		}
		
		//
		// Define the static mods
		//
		if(staticMods.size() > 0) {

			StaticModifications smods = new StaticModifications();
			limelightInputRoot.setStaticModifications( smods );

			for( String residue : staticMods.keySet() ) {

				StaticModification xmlSmod = new StaticModification();
				xmlSmod.setAminoAcid( residue );
				xmlSmod.setMassChange( staticMods.get(residue) );

				smods.getStaticModification().add( xmlSmod );
			}
		}

		//
		// Build MatchedProteins section and get map of protein names to MatchedProtein ids
		//
		Map<String, Integer> proteinNameIds = MatchedProteinsBuilder.getInstance().buildMatchedProteins(
				limelightInputRoot,
				conversionParameters.getFastaFilePath(),
				results.getPeptidePSMMap().keySet(),
				conversionParameters.getDecoyPrefix()
		);

		for(String k : proteinNameIds.keySet()) {
			System.out.println(k + "\t" + proteinNameIds.get(k));
		}


		//
		// Define the peptide and PSM data
		//
		ReportedPeptides reportedPeptides = new ReportedPeptides();
		limelightInputRoot.setReportedPeptides( reportedPeptides );
		
		// iterate over each distinct reported peptide
		for( ModAReportedPeptide modAReportedPeptide : results.getPeptidePSMMap().keySet() ) {

			// skip this if it only contains decoys
			if(ReportedPeptideUtils.reportedPeptideOnlyContainsDecoys(results, modAReportedPeptide)) {
				continue;
			}

			String reportedPeptideString = modAReportedPeptide.getReportedPeptideString();

			ReportedPeptide xmlReportedPeptide = new ReportedPeptide();
			reportedPeptides.getReportedPeptide().add( xmlReportedPeptide );
			
			xmlReportedPeptide.setReportedPeptideString( reportedPeptideString );
			xmlReportedPeptide.setSequence( modAReportedPeptide.getNakedPeptide() );

			MatchedProteinsForPeptide xProteinsForPeptide = new MatchedProteinsForPeptide();
			xmlReportedPeptide.setMatchedProteinsForPeptide( xProteinsForPeptide );

			// add in protein inference info
			int proteinCount = 0;
			for( String proteinName : modAReportedPeptide.getProteinMatches() ) {

				if(proteinNameIds.containsKey( proteinName ) ) {
					proteinCount++;
					int matchedProteinId = proteinNameIds.get(proteinName);

					MatchedProteinForPeptide xProteinForPeptide = new MatchedProteinForPeptide();
					xProteinsForPeptide.getMatchedProteinForPeptide().add(xProteinForPeptide);

					xProteinForPeptide.setId(BigInteger.valueOf(matchedProteinId));
				}
			}

			if( proteinCount == 0) {
				throw new Exception("Could not find a protein for peptide: " + modAReportedPeptide);
			}

			// add in the mods for this peptide
			if( modAReportedPeptide.getMods() != null && modAReportedPeptide.getMods().keySet().size() > 0 ) {
					
				PeptideModifications xmlModifications = new PeptideModifications();
				xmlReportedPeptide.setPeptideModifications( xmlModifications );
					
				for( int position : modAReportedPeptide.getMods().keySet() ) {

					PeptideModification xmlModification = new PeptideModification();
					xmlModifications.getPeptideModification().add( xmlModification );

					xmlModification.setMass( modAReportedPeptide.getMods().get( position ) );

					if( position == 0) {

						xmlModification.setIsNTerminal( true );

					} else if( position == modAReportedPeptide.getNakedPeptide().length() + 1 ) {

						xmlModification.setIsCTerminal( true );

					} else {
						xmlModification.setPosition( BigInteger.valueOf( position ) );
					}
				}
			}

			
			// add in the PSMs and annotations
			Psms xmlPsms = new Psms();
			xmlReportedPeptide.setPsms( xmlPsms );

			// iterate over all PSMs for this reported peptide

			for( ModAPSM psm : results.getPeptidePSMMap().get(modAReportedPeptide) ) {

				Psm xmlPsm = new Psm();
				xmlPsms.getPsm().add( xmlPsm );

				xmlPsm.setScanNumber( new BigInteger( String.valueOf( psm.getScanNumber() ) ) );
				xmlPsm.setPrecursorCharge( new BigInteger( String.valueOf( psm.getCharge() ) ) );

				// add in the filterable PSM annotations (e.g., score)
				FilterablePsmAnnotations xmlFilterablePsmAnnotations = new FilterablePsmAnnotations();
				xmlPsm.setFilterablePsmAnnotations( xmlFilterablePsmAnnotations );

				// handle comet scores

				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.MODA_ANNOTATION_TYPE_CALC_FDR );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_MODA);


					DecimalFormat formatter = new DecimalFormat("0.###E0");

					double fdr = tdAnalysis.getFDRForScore( psm.getProbability() );

					BigDecimal bd = BigDecimal.valueOf( fdr );
					bd = bd.round( new MathContext( 3 ) );

					xmlFilterablePsmAnnotation.setValue( bd );
				}

				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.MODA_ANNOTATION_TYPE_DELTA_MASS );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_MODA);
					xmlFilterablePsmAnnotation.setValue( psm.getDeltaMass());
				}

				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.MODA_ANNOTATION_TYPE_SCORE );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_MODA);
					xmlFilterablePsmAnnotation.setValue( psm.getScore());
				}

				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.MODA_ANNOTATION_TYPE_PROBABILITY );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_MODA);
					xmlFilterablePsmAnnotation.setValue( psm.getProbability());
				}

				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.MODA_ANNOTATION_TYPE_CALC_RANK );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_MODA);
					xmlFilterablePsmAnnotation.setValue( psm.getRank());
				}

			}// end iterating over psms for a reported peptide
		
		}//end iterating over reported peptides

		
		
		// add in the output log file(s)
		ConfigurationFiles xmlConfigurationFiles = new ConfigurationFiles();
		limelightInputRoot.setConfigurationFiles( xmlConfigurationFiles );

		{
			ConfigurationFile xmlConfigurationFile = new ConfigurationFile();
			xmlConfigurationFiles.getConfigurationFile().add(xmlConfigurationFile);

			File paramsFile = conversionParameters.getParamsFile();

			xmlConfigurationFile.setSearchProgram(Constants.PROGRAM_NAME_MODA);
			xmlConfigurationFile.setFileName(paramsFile.getName());
			xmlConfigurationFile.setFileContent(Files.readAllBytes(FileSystems.getDefault().getPath(paramsFile.getAbsolutePath())));
		}
		
		//make the xml file
		CreateImportFileFromJavaObjectsMain.getInstance().createImportFileFromJavaObjectsMain( new File(conversionParameters.getOutputFilePath() ), limelightInputRoot);

	}
	
}
