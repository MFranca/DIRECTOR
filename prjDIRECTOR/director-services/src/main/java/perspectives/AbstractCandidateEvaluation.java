package perspectives;

public abstract class AbstractCandidateEvaluation {
			
	protected Long serviceId;
	protected int globalRank; // 1st, 2nd etc..

	// Methods -------------------------------------------
	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long id) {
		this.serviceId = id;
	}
	
	public int getGlobalRank() {
		return globalRank;
	}

	public void setGlobalRank(int globalRank) {
		this.globalRank = globalRank;
	}

	public abstract int getGlobalScore();
}