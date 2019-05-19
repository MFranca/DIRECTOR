package perspectives.social;

import perspectives.AbstractCandidateEvaluation;
import utils.LogUtils;

public class CandidateSocialEvaluation extends AbstractCandidateEvaluation {
	// per question (total)
	// http://api.stackexchange.com/docs/types/question
	private int hitCount;
	private int ownerReputationSum;	
	private int viewHitCountSum;
	private int scoreSum; // votes
	private int answeredHitCount;
	
	private int hitPoints;
	private int ownerReputationPoints;	
	private int viewHitPoints;
	private int scorePoints; // votes
	private int answeredHitPoints;
			
	public CandidateSocialEvaluation (Long serviceId) {		 
		this.serviceId = serviceId;
	}
	
	public int getHitCount() {
		return hitCount;
	}

	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
	}

	public int getOwnerReputationSum() {
		return ownerReputationSum;
	}

	public void setOwnerReputationSum(int ownerReputationSum) {
		this.ownerReputationSum = ownerReputationSum;
	}

	public int getAnsweredHitCount() {
		return answeredHitCount;
	}

	public void setAnsweredHitCount(int answeredHitCount) {
		this.answeredHitCount = answeredHitCount;
	}

	public int getViewHitCountSum() {
		return viewHitCountSum;
	}

	public void setViewHitCountSum(int viewHitCountSum) {
		this.viewHitCountSum = viewHitCountSum;
	}

	public int getScoreSum() {
		return scoreSum;
	}

	public void setScoreSum(int scoreSum) {
		this.scoreSum = scoreSum;
	}

	public void setHitPoints(int hitPoints) {
		this.hitPoints = hitPoints;
	}

	public void setOwnerReputationPoints(int ownerReputationPoints) {
		this.ownerReputationPoints = ownerReputationPoints;
	}

	public void setViewHitPoints(int viewHitPoints) {
		this.viewHitPoints = viewHitPoints;
	}

	public void setScorePoints(int scorePoints) {
		this.scorePoints = scorePoints;
	}

	public void setAnsweredHitPoints(int answeredHitPoints) {
		this.answeredHitPoints = answeredHitPoints;
	}
		
	public int getHitPoints() {
		return hitPoints;
	}

	public int getOwnerReputationPoints() {
		return ownerReputationPoints;
	}

	public int getViewHitPoints() {
		return viewHitPoints;
	}

	public int getScorePoints() {
		return scorePoints;
	}

	public int getAnsweredHitPoints() {
		return answeredHitPoints;
	}

	// Total
	public int getGlobalScore() {
		/*
		LogUtils.logTrace("\n* Global Score ===> "); 	
		LogUtils.logTrace("HitPoints: " + this.hitPoints);
		LogUtils.logTrace("OwnerReputationPoints: " + this.ownerReputationPoints);
		LogUtils.logTrace("ViewHitPoints: " + this.viewHitPoints);
		LogUtils.logTrace("ScorePoints: " + this.scorePoints);
		LogUtils.logTrace("AnsweredHitPoints: " + this.answeredHitPoints);
		*/
		
		return 	(this.hitPoints + this.ownerReputationPoints + this.viewHitPoints + this.scorePoints + this.answeredHitPoints);
	}
}