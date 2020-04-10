package org.yeastrc.limelight.xml.moda.objects;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class ModAPSM {

	private BigDecimal deltaMass;
	private BigDecimal score;
	private BigDecimal probability;
	private BigDecimal rank;

	private int scanNumber;
	private BigDecimal precursorNeutralMass;
	private int charge;

	private String peptideSequence;
	private Map<Integer,BigDecimal> modifications;
	private Collection<String> proteinNames;

	private boolean isDecoy;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ModAPSM modAPSM = (ModAPSM) o;
		return scanNumber == modAPSM.scanNumber &&
				charge == modAPSM.charge &&
				isDecoy == modAPSM.isDecoy &&
				deltaMass.equals(modAPSM.deltaMass) &&
				score.equals(modAPSM.score) &&
				probability.equals(modAPSM.probability) &&
				rank.equals(modAPSM.rank) &&
				precursorNeutralMass.equals(modAPSM.precursorNeutralMass) &&
				peptideSequence.equals(modAPSM.peptideSequence) &&
				modifications.equals(modAPSM.modifications) &&
				proteinNames.equals(modAPSM.proteinNames);
	}

	@Override
	public int hashCode() {
		return Objects.hash(deltaMass, score, probability, rank, scanNumber, precursorNeutralMass, charge, peptideSequence, modifications, proteinNames, isDecoy);
	}

	@Override
	public String toString() {
		return "ModAPSM{" +
				"deltaMass=" + deltaMass +
				", score=" + score +
				", probability=" + probability +
				", rank=" + rank +
				", scanNumber=" + scanNumber +
				", precursorNeutralMass=" + precursorNeutralMass +
				", charge=" + charge +
				", peptideSequence='" + peptideSequence + '\'' +
				", modifications=" + modifications +
				", proteinNames=" + proteinNames +
				", isDecoy=" + isDecoy +
				'}';
	}

	public BigDecimal getDeltaMass() {
		return deltaMass;
	}

	public void setDeltaMass(BigDecimal deltaMass) {
		this.deltaMass = deltaMass;
	}

	public BigDecimal getScore() {
		return score;
	}

	public void setScore(BigDecimal score) {
		this.score = score;
	}

	public BigDecimal getProbability() {
		return probability;
	}

	public void setProbability(BigDecimal probability) {
		this.probability = probability;
	}

	public BigDecimal getRank() {
		return rank;
	}

	public void setRank(BigDecimal rank) {
		this.rank = rank;
	}

	public int getScanNumber() {
		return scanNumber;
	}

	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}

	public BigDecimal getPrecursorNeutralMass() {
		return precursorNeutralMass;
	}

	public void setPrecursorNeutralMass(BigDecimal precursorNeutralMass) {
		this.precursorNeutralMass = precursorNeutralMass;
	}

	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public String getPeptideSequence() {
		return peptideSequence;
	}

	public void setPeptideSequence(String peptideSequence) {
		this.peptideSequence = peptideSequence;
	}

	public Map<Integer, BigDecimal> getModifications() {
		return modifications;
	}

	public void setModifications(Map<Integer, BigDecimal> modifications) {
		this.modifications = modifications;
	}

	public Collection<String> getProteinNames() {
		return proteinNames;
	}

	public void setProteinNames(Collection<String> proteinNames) {
		this.proteinNames = proteinNames;
	}

	public boolean isDecoy() {
		return isDecoy;
	}

	public void setDecoy(boolean decoy) {
		isDecoy = decoy;
	}
}
