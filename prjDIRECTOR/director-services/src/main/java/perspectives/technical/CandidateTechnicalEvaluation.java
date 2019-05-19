package perspectives.technical;

import perspectives.AbstractCandidateEvaluation;

public class CandidateTechnicalEvaluation extends AbstractCandidateEvaluation {
	
	public CandidateTechnicalEvaluation (
			int agilityPriority, 
			int assurancePriority, 
			int financialPriority, 
			int performancePriority,
			int securityPriority, 
			int usabilityPriority) {
		
		this.agilityPriority = agilityPriority;
		this.assurancePriority = assurancePriority; 
		this.financialPriority = financialPriority; 
		this.performancePriority = performancePriority;
		this.securityPriority = securityPriority; 
		this.usabilityPriority = usabilityPriority;
	}
	
	// Priorities
	private int agilityPriority; 
	private int assurancePriority; 
	private int financialPriority; 
	private int performancePriority;
	private int securityPriority;
	private int usabilityPriority;

	// Agility
	private int scalability = 0;
	private int scalabilityPoints = 0;
	private int portability = 0; // negative
	private int portabilityPoints = 0;
	private int flexibility = 0;
	private int flexibilityPoints = 0;
	private int adaptability = 0; // negative
	private int adaptabilityPoints = 0;
	
	// Assurance
	private int availability = 0; // negative
	private int availabilityPoints = 0;
	private int stability = 0;
	private int stabilityPoints = 0;

	// Financial
	private int onGoingCost = 0;
	private int onGoingCostPoints = 0;

	// Performance
	private int functionality = 0;
	private int functionalityPoints = 0;

	// Security & Privacy
	private int accessControlAndPrivilegeManagement = 0;
	private int accessControlAndPrivilegeManagementPoints = 0;

	// Usability
	private int accessibility = 0;
	private int accessibilityPoints = 0;
	private int learnability = 0;
	private int learnabilityPoints = 0;

	// Methods -------------------------------------------
	public int getScalability() {
		return scalability;
	}

	public void setScalability(int scalability) {
		this.scalability = scalability;
	}

	public int getPortability() {
		return portability;
	}

	public void setPortability(int portability) {
		this.portability = portability;
	}

	public int getFlexibility() {
		return flexibility;
	}

	public void setFlexibility(int flexibility) {
		this.flexibility = flexibility;
	}

	public int getAdaptability() {
		return adaptability;
	}

	public void setAdaptability(int adaptability) {
		this.adaptability = adaptability;
	}

	public int getAvailability() {
		return availability;
	}

	public void setAvailability(int availability) {
		this.availability = availability;
	}

	public int getStability() {
		return stability;
	}

	public void setStability(int stability) {
		this.stability = stability;
	}

	public int getOnGoingCost() {
		return onGoingCost;
	}

	public void setOnGoingCost(int onGoingCost) {
		this.onGoingCost = onGoingCost;
	}

	public int getFunctionality() {
		return functionality;
	}

	public void setFunctionality(int functionality) {
		this.functionality = functionality;
	}

	public int getAccessControlAndPrivilegeManagement() {
		return accessControlAndPrivilegeManagement;
	}

	public void setAccessControlAndPrivilegeManagement(int accessControlAndPrivilegeManagement) {
		this.accessControlAndPrivilegeManagement = accessControlAndPrivilegeManagement;
	}

	public int getAccessibility() {
		return accessibility;
	}

	public void setAccessibility(int accessibility) {
		this.accessibility = accessibility;
	}

	public int getLearnability() {
		return learnability;
	}

	public void setLearnability(int learnability) {
		this.learnability = learnability;
	}
	

	public int getScalabilityPoints() {
		return scalabilityPoints;
	}

	public void setScalabilityPoints(int scalabilityPoints) {
		this.scalabilityPoints = scalabilityPoints;
	}

	public int getPortabilityPoints() {
		return portabilityPoints;
	}

	public void setPortabilityPoints(int portabilityPoints) {
		this.portabilityPoints = portabilityPoints;
	}

	public int getFlexibilityPoints() {
		return flexibilityPoints;
	}

	public void setFlexibilityPoints(int flexibilityPoints) {
		this.flexibilityPoints = flexibilityPoints;
	}

	public int getAdaptabilityPoints() {
		return adaptabilityPoints;
	}

	public void setAdaptabilityPoints(int adaptabilityPoints) {
		this.adaptabilityPoints = adaptabilityPoints;
	}

	public int getAvailabilityPoints() {
		return availabilityPoints;
	}

	public void setAvailabilityPoints(int availabilityPoints) {
		this.availabilityPoints = availabilityPoints;
	}

	public int getStabilityPoints() {
		return stabilityPoints;
	}

	public void setStabilityPoints(int stabilityPoints) {
		this.stabilityPoints = stabilityPoints;
	}

	public int getOnGoingCostPoints() {
		return onGoingCostPoints;
	}

	public void setOnGoingCostPoints(int onGoingCostPoints) {
		this.onGoingCostPoints = onGoingCostPoints;
	}

	public int getFunctionalityPoints() {
		return functionalityPoints;
	}

	public void setFunctionalityPoints(int functionalityPoints) {
		this.functionalityPoints = functionalityPoints;
	}

	public int getAccessControlAndPrivilegeManagementPoints() {
		return accessControlAndPrivilegeManagementPoints;
	}

	public void setAccessControlAndPrivilegeManagementPoints(int accessControlAndPrivilegeManagementPoints) {
		this.accessControlAndPrivilegeManagementPoints = accessControlAndPrivilegeManagementPoints;
	}

	public int getAccessibilityPoints() {
		return accessibilityPoints;
	}

	public void setAccessibilityPoints(int accessibilityPoints) {
		this.accessibilityPoints = accessibilityPoints;
	}

	public int getLearnabilityPoints() {
		return learnabilityPoints;
	}

	public void setLearnabilityPoints(int learnabilityPoints) {
		this.learnabilityPoints = learnabilityPoints;
	}

	// Categories ---------------------------------------
	public int getAgility() {
		//return this.scalability - (this.portability) + this.flexibility - (this.adaptability);
		return (this.scalabilityPoints + this.portabilityPoints + this.flexibilityPoints + this.adaptabilityPoints) * this.agilityPriority;
	}

	public int getAssurance() {
		return (this.stabilityPoints + this.availabilityPoints) * this.assurancePriority;
	}

	public int getFinancial() {
		return this.onGoingCostPoints * this.financialPriority;
	}

	public int getPerformance() {
		return this.functionalityPoints * this.performancePriority;
	}

	public int getSecurityAndPrivacy() {
		return this.accessControlAndPrivilegeManagementPoints * securityPriority;
	}

	public int getUsability() {
		return (this.accessibilityPoints + this.learnabilityPoints) * this.usabilityPriority;
	}
	
	// Total
	public int getGlobalScore() {
		/*
		LogUtils.logTrace("\n* Global Score ===> "); 	
		LogUtils.logTrace("Agility: " + this.getAgility() + " X " + agilityPriority);
		LogUtils.logTrace("Assurance: " + this.getAssurance() + " X " + assurancePriority);
		LogUtils.logTrace("Financial: " + this.getFinancial() + " X " + financialPriority);
		LogUtils.logTrace("Performance: " + this.getPerformance() + " X " + performancePriority);
		LogUtils.logTrace("SecurityAndPrivacy: " + this.getSecurityAndPrivacy() + " X " + securityPriority);
		LogUtils.logTrace("Usability: " + this.getUsability() + " X " + usabilityPriority);
				
		LogUtils.logTrace("Total: " + ( (this.getAgility() * agilityPriority) + 
				(this.getAssurance() * assurancePriority) +
				(this.getFinancial() * financialPriority) +
				(this.getPerformance() * performancePriority) +
				(this.getSecurityAndPrivacy() * securityPriority) +
				(this.getUsability() * usabilityPriority)));
		*/
		return 	(this.getAgility() * agilityPriority) + 
				(this.getAssurance() * assurancePriority) +
				(this.getFinancial() * financialPriority) +
				(this.getPerformance() * performancePriority) +
				(this.getSecurityAndPrivacy() * securityPriority) +
				(this.getUsability() * usabilityPriority);
	}
}
