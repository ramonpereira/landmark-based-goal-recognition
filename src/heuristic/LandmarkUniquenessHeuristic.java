package heuristic;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import extracting.PartialLandmarkGenerator;
import javaff.data.Fact;
import javaff.data.GroundFact;
import recognizer.Recognizer;

/**
 * This class defines a goal recognition heuristic called Landmark Uniqueness Heuristic - huniq(G).
 * <p> 
 * Clearly, landmarks that are common to multiple candidate goals are less useful for recognizing a goal 
 * than landmarks that exist for only a single goal. As a consequence, computing how unique (and thus informative) 
 * each landmark is can help disambiguate similar goals for a set of candidate goals.
 * <p>
 * To estimate what is the correct intended goal from observations, we introduce a concept called landmark uniqueness, 
 * which is the inverse frequency of a landmark among the landmarks found in a set of candidate goals.
 * Thus, this heuristic estimates the goal completion of a candidate goal G by calculating the ratio between 
 * the sum of the uniqueness value of the achieved landmarks of G and the sum of the uniqueness value of all landmarks of G. 
 * 
 * @author Ramon Fraga Pereira
 *
 */
public class LandmarkUniquenessHeuristic extends Recognizer {

	private Map<GroundFact, Float> goalsTotalLandmarksUniquenessValue = new HashMap<>();
	private Map<Set<Fact>, Float> landmarksToUniquenessValue = new HashMap<>();
	
	public LandmarkUniquenessHeuristic(String fileName, float threshold) {
		super(fileName, threshold);
	}
	
	public LandmarkUniquenessHeuristic(String domainFile, String problemFile, String candidateGoalsFile, String observationsFile, String correctGoalFile, float threshold) {
		super(domainFile, problemFile, candidateGoalsFile, observationsFile, correctGoalFile, threshold);
	}
	
	@Override
	public boolean recognize() throws IOException, InterruptedException {
		/* Calculating landmark uniqueness value for all candidate goals */
		this.calculateLandmarksUniquenessValue();
		
		Map<GroundFact, Float> goalsToEstimatedValue = new HashMap<>();
		float highestEstimate = 0;
		
		/* Computing achieved landmarks in the observations */
		Map<GroundFact, Set<Set<Fact>>> achievedLandmarksForAllGoals = this.getAchievedLandmarksForAllGoals();
		
		System.out.println("\n$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		System.out.println("$$$$$$$$$$ Landmark Uniqueness Heuristic $$$$$$$$$$$");
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		for(GroundFact goal: this.goals){
			System.out.println("\n---> Goal: " + goal);
			float totalUniquenessValueOfAchievedLandmarksGoal = 0;
			Set<Set<Fact>> achievedLandmarksFromObservationsGoal = achievedLandmarksForAllGoals.get(goal);
			for(Set<Fact> obsLandmark: achievedLandmarksFromObservationsGoal){
				float landmarkValue = this.landmarksToUniquenessValue.get(obsLandmark);
				System.out.println(obsLandmark + " = (" + landmarkValue + ")");
				totalUniquenessValueOfAchievedLandmarksGoal = totalUniquenessValueOfAchievedLandmarksGoal + landmarkValue; 
			}
			/* Estimating goal completion using Landmark Uniqueness Heuristic - huniq(G) */
			float estimatePercentageGoal = this.getEstimateLandmarkUniqueness(goal, totalUniquenessValueOfAchievedLandmarksGoal);
			BigDecimal estimatePercentageGoalBigDecimal = new BigDecimal(estimatePercentageGoal);
			estimatePercentageGoal = estimatePercentageGoalBigDecimal.setScale(2, BigDecimal.ROUND_UP).floatValue();

			System.out.println("\n\t$$$$> Heuristic Value = " + (estimatePercentageGoal));
			System.out.println("\t\t" + totalUniquenessValueOfAchievedLandmarksGoal + " / " + this.goalsTotalLandmarksUniquenessValue.get(goal));
			
			if(estimatePercentageGoal > highestEstimate)
				highestEstimate = estimatePercentageGoal;
			
			goalsToEstimatedValue.put(goal, estimatePercentageGoal);
		}
		Set<GroundFact> recognizedGoals = new HashSet<>();
		System.out.println("\n$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		System.out.println("$> Recognized goal(s) within the threshold " + this.threshold + ": ");
		for(GroundFact goal : goalsToEstimatedValue.keySet()){
			float estimatedValue = goalsToEstimatedValue.get(goal);
			if(estimatedValue>= (highestEstimate - this.threshold)){
				recognizedGoals.add(goal);
				System.out.println("$> " + goal + ": " + estimatedValue);
			}
		}
		this.amountOfRecognizedGoals = recognizedGoals.size();
		boolean correctGoalRecognized = recognizedGoals.contains(this.realGoal);
		System.out.println("\n<?> Correct goal: " + this.realGoal);
		System.out.println("<?> Was the correct goal recognized correctly? " + correctGoalRecognized);
		Process p = Runtime.getRuntime().exec("rm -rf domain.pddl template.pddl problem.pddl problem_neg.pddl templateInitial.pddl obs.dat obs2.dat hyps.dat plan.png real_hyp.dat log.txt");
		p.waitFor();
		return correctGoalRecognized;
	}
	
	/**
	 * This method estimates the percentage of completion of a goal using Landmark Uniqueness Heuristic - huniq(G).
	 * 
	 * @param goal
	 * @param totalUniquenessValueOfAchievedLandmarks
	 * @return
	 */
	public float getEstimateLandmarkUniqueness(GroundFact goal, float totalUniquenessValueOfAchievedLandmarks){
		return (totalUniquenessValueOfAchievedLandmarks / this.goalsTotalLandmarksUniquenessValue.get(goal));
	}
	
	/**
	 * This method calculates and computes the landmark uniqueness value for every landmark in the set of extracted landmarks among all candidate goals.
	 *
	 */
	private void calculateLandmarksUniquenessValue(){
		System.out.println("$$$> Calculating landmark uniqueness value for all candidate goals: \n");
		Set<Set<Fact>> extractedLandmarks = new HashSet<>();
		for(GroundFact goal: this.goals){
			/* Extracting landmarks for every candidate goal */
			PartialLandmarkGenerator landmarkGenerator = new PartialLandmarkGenerator(initialState, goal.getFacts(), groundProblem.getActions());
			landmarkGenerator.extractLandmarks();
			extractedLandmarks.addAll(landmarkGenerator.getLandmarks());
			this.goalsToLandmarks.put(goal, landmarkGenerator.getLandmarks());
			this.goalsToLandmarkExtractor.put(goal, landmarkGenerator);
		}
		for(Set<Fact> landmark: extractedLandmarks){
			int count = 0;
			for(GroundFact goal: this.goals){
				Set<Set<Fact>> goalLandmarks = this.goalsToLandmarks.get(goal);
				if(goalLandmarks.contains(landmark))
					count++;
			}
			this.landmarksToUniquenessValue.put(landmark, new Float((float) 1/count));
		}
		for(GroundFact goal: this.goals){
			Set<Set<Fact>> goalLandmarks = this.goalsToLandmarks.get(goal);
			float total = 0;
			System.out.println(goal);
			for(Set<Fact> landmark: goalLandmarks){
				float landmarkValue = this.landmarksToUniquenessValue.get(landmark);
				System.out.println("\t" + landmark + " = " + landmarkValue);
				total = total + landmarkValue;
			}
			this.goalsTotalLandmarksUniquenessValue.put(goal, total);
		}
		System.out.println();
	}
}