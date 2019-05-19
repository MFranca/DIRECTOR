package perspectives;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import mysqlDB.AbstractEntity;
import utils.LogUtils;

public abstract class AbstractAnalysis {
	public static final String METHOD_START = ">>> STARTING CONTROLLER triggered by: ";
	public static final String METHOD_END = ">>> ENDING CONTROLLER at: ";
	//public static final String METHOD_SUCCESS = "{\"success\":true}";
	
	protected void start(String method, String source) {		
		LogUtils.logInformation(method, METHOD_START + source);
		AbstractEntity.setupEntityManager();
	}
	
	protected void end(String method) {
		AbstractEntity.dispose();
		LogUtils.logInformation(method, METHOD_END + LocalDate.now().toString());
	}
	
	protected static int getRanking(int value, int[] values) throws Exception {
		int rank;
		int maxPoints = values.length;
		int i;
		
		/*
		LogUtils.logTrace("\n[getRanking] value: " + value);
		LogUtils.logTrace("[getRanking] source array: " + Arrays.toString(values));
		LogUtils.logTrace("[getRanking] max points: " + maxPoints);
		*/
		
		//just double checking
		boolean contains = IntStream.of(values).anyMatch(x -> x == value);
		if (!contains)
			throw new Exception("Value is NOT a candidate's one!");
		
		// {-7, 6, 45, 21, 9, 102, 102, 9};
		// removing duplicates		
		Set<Integer> set = new HashSet<Integer>();
		for(i = 0; i < values.length; i++){
		  set.add(values[i]);
		}

		// {-7, 6, 45, 21, 9, 102};
		// converting back to an array
		Integer[] distinctValues = new Integer[set.size()];
		Iterator<Integer> numbers = set.iterator();
		i = 0;
		while (numbers.hasNext()) {  
			distinctValues[i] = numbers.next().intValue();
			i++;
		}		
		//LogUtils.logTrace("[getRanking] distinct values: " + Arrays.toString(distinctValues));

		// {-7, 6, 45, 21, 9, 102};
	   	// sorting array
		Arrays.sort(distinctValues, Collections.reverseOrder()); //  It by default sorts in ascending order.		
		//LogUtils.logTrace("[getRanking] sorted values: " + Arrays.toString(distinctValues));
		
		// {102, 45, 21, 9, 6, -7};
		// finding the position (binary search only with ascending order
		int position = Arrays.asList(distinctValues).indexOf(value);		
		rank = maxPoints - position;
		/*LogUtils.logTrace("[getRanking] position: " + position);
		LogUtils.logTrace("[getRanking] rank: " + rank);*/
		
		return rank;
	}
	
	protected static Long[] sortCandidates(List<AbstractCandidateEvaluation> evaluations) {
		Long[] serviceIds= new Long[evaluations.size()];
		int[] servicePoints= new int[evaluations.size()];
		int i;
		int j;
		
		for (i = 0; i<evaluations.size(); i++) {
			servicePoints[i] = evaluations.get(i).getGlobalScore();
			serviceIds[i] = evaluations.get(i).getServiceId();
		}
		
		//LogUtils.logTrace("[sortCandidates] source serviceIds array: " + Arrays.toString(serviceIds));
		//LogUtils.logTrace("[sortCandidates] source servicePoints array: " + Arrays.toString(servicePoints));
		
		// sort
		for (i = 0; i<servicePoints.length - 1; i++) {
			for (j = i + 1; j<servicePoints.length; j++) {
				if (servicePoints[i] < servicePoints[j]) {
					int aux = servicePoints[i];
					servicePoints[i] = servicePoints[j];
					servicePoints[j] = aux;
					
					Long aux2 = serviceIds[i];
					serviceIds[i] = serviceIds[j];
					serviceIds[j] = aux2; 
				}
			}			
		}
		
		//LogUtils.logTrace("[sortCandidates] sorted serviceIds array: " + Arrays.toString(serviceIds));
		//LogUtils.logTrace("[sortCandidates] sorted servicePoints array: " + Arrays.toString(servicePoints));
		
		// finding the position for Global Rank
		for (i = 0; i<evaluations.size(); i++) {
			//int position = Arrays.binarySearch(serviceIds, evaluations.get(i).getServiceId());
			int position = Arrays.asList(serviceIds).indexOf(evaluations.get(i).getServiceId());
			evaluations.get(i).setGlobalRank(position + 1);
			
			//LogUtils.logTrace("[sortCandidates] Service id: " + evaluations.get(i).getServiceId());
			//LogUtils.logTrace("[sortCandidates] Global Score: " + evaluations.get(i).getGlobalScore());
			//LogUtils.logTrace("[sortCandidates] Global Rank position: " + evaluations.get(i).getGlobalRank());
		}
		
		return serviceIds;		
	}
}