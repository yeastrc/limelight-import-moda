package org.yeastrc.limelight.xml.moda.objects;

import java.util.Collection;
import java.util.Map;

public class ModAResults {

	private Map<ModAReportedPeptide, Collection<ModAPSM>> peptidePSMMap;
	private String version;
	/**
	 * @return the peptidePSMMap
	 */
	public Map<ModAReportedPeptide, Collection<ModAPSM>> getPeptidePSMMap() {
		return peptidePSMMap;
	}
	/**
	 * @param peptidePSMMap the peptidePSMMap to set
	 */
	public void setPeptidePSMMap(Map<ModAReportedPeptide, Collection<ModAPSM>> peptidePSMMap) {
		this.peptidePSMMap = peptidePSMMap;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
