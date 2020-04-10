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

package org.yeastrc.limelight.xml.moda.annotation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.limelight.limelight_import.api.xml_dto.FilterDirectionType;
import org.yeastrc.limelight.limelight_import.api.xml_dto.FilterablePsmAnnotationType;


public class PSMAnnotationTypes {

	// modA scores
	public static final String MODA_ANNOTATION_TYPE_DELTA_MASS = "delta mass";
	public static final String MODA_ANNOTATION_TYPE_SCORE = "score";
	public static final String MODA_ANNOTATION_TYPE_PROBABILITY = "probability";
	public static final String MODA_ANNOTATION_TYPE_CALC_FDR = "fdr";
	public static final String MODA_ANNOTATION_TYPE_CALC_RANK = "rank";

	public static List<FilterablePsmAnnotationType> getFilterablePsmAnnotationTypes() {
		List<FilterablePsmAnnotationType> types = new ArrayList<FilterablePsmAnnotationType>();

		{
			FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
			type.setName( MODA_ANNOTATION_TYPE_DELTA_MASS );
			type.setDescription( "observed_MW minus calculated_MW" );
			type.setFilterDirection( FilterDirectionType.BELOW );

			types.add( type );
		}

		{
			FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
			type.setName( MODA_ANNOTATION_TYPE_SCORE );
			type.setDescription( "score of the peptide spectrum match" );
			type.setFilterDirection( FilterDirectionType.ABOVE );

			types.add( type );
		}

		{
			FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
			type.setName( MODA_ANNOTATION_TYPE_PROBABILITY );
			type.setDescription( "probability that the peptide spectrum match is correct" );
			type.setFilterDirection( FilterDirectionType.ABOVE );

			types.add( type );
		}

		{
			FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
			type.setName( MODA_ANNOTATION_TYPE_CALC_FDR );
			type.setDescription( "fdr calculated by target/decoy analysis based on probability" );
			type.setFilterDirection( FilterDirectionType.BELOW );
			type.setDefaultFilterValue(new BigDecimal("0.01"));

			types.add( type );
		}

		{
			FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
			type.setName( MODA_ANNOTATION_TYPE_CALC_RANK );
			type.setDescription( "Rank of PSM for given scan" );
			type.setFilterDirection( FilterDirectionType.BELOW );
			type.setDefaultFilterValue(new BigDecimal("1"));

			types.add( type );
		}

		return types;
	}
	
	
}
