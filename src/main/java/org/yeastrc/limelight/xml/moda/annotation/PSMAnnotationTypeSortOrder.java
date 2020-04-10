package org.yeastrc.limelight.xml.moda.annotation;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.limelight.limelight_import.api.xml_dto.SearchAnnotation;
import org.yeastrc.limelight.xml.moda.constants.Constants;

public class PSMAnnotationTypeSortOrder {

	public static List<SearchAnnotation> getPSMAnnotationTypeSortOrder() {
		List<SearchAnnotation> annotations = new ArrayList<SearchAnnotation>();

		{
			SearchAnnotation annotation = new SearchAnnotation();
			annotation.setAnnotationName( PSMAnnotationTypes.MODA_ANNOTATION_TYPE_CALC_FDR );
			annotation.setSearchProgram( Constants.PROGRAM_NAME_MODA);
			annotations.add( annotation );
		}

		{
			SearchAnnotation annotation = new SearchAnnotation();
			annotation.setAnnotationName( PSMAnnotationTypes.MODA_ANNOTATION_TYPE_SCORE );
			annotation.setSearchProgram( Constants.PROGRAM_NAME_MODA);
			annotations.add( annotation );
		}
		
		return annotations;
	}
}
