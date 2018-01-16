package heuristic;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javaff.data.Fact;
import javaff.data.GroundFact;
import landmark.LandmarkOrdering;
import recognizer.Recognizer;

/**
 * 
 * This class defines a goal recognition heuristic called Goal Completion Heuristic - hgc(G).
 * <p>
 * The hgc(G) estimates the percentage of completion of a goal based on the number of landmarks that have been detected, 
 * and are required to achieve that goal. More specifically, this estimate represents the percentage of sub-goals in a goal 
 * that have been accomplished based on the evidence of achieved fact landmarks in the observations. 
 * A candidate goal is composed of sub-goals comprised of the atomic facts that are part of a conjunction of facts.
 * 
 * @author Ramon Fraga Pereira
 *
 */
public class GoalCompletionHeuristic extends Recognizer {

	public GoalCompletionHeuristic(String fileName, float threshold) {
		super(fileName, threshold);
	}
	
	public GoalCompletionHeuristic(String domainFile, String problemFile, String candidateGoalsFile, String observationsFile, String correctGoalFile, float threshold) {
		super(domainFile, problemFile, candidateGoalsFile, observationsFile, correctGoalFile, threshold);
	}
	
	@Override
	public boolean recognize() throws IOException, InterruptedException{
		/* Extracing and computing achieved landmarks in the observations */
		Map<GroundFact, Set<Set<Fact>>> achievedLandmarksForAllGoals = this.getAchievedLandmarksForAllGoals();
		
		Map<GroundFact, Float> goalsToEstimativeGoalCompletion = new HashMap<>();
		float maxGoalCompletion = 0f;
		System.out.println("\n$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		System.out.println("$$$$$$$$$$ Goal Completion Heuristic $$$$$$$$$$$");
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$\n");
		for(GroundFact goal: this.goals){
			List<LandmarkOrdering> subgoalLandmarksOrdering = this.goalsToLandmarkExtractor.get(goal).getLandmarksOrdering();
			Map<Fact, Integer> subgoalsAchievedLandmarks = new HashMap<>();
			Set<Set<Fact>> achievedLandmarksFromObservationsForGoal = achievedLandmarksForAllGoals.get(goal);
			
			/* Count the number of achieved landmarks for every subgoal of a candidate goal */
			for(LandmarkOrdering landmarkOrdering: subgoalLandmarksOrdering){
				int subgoalCounter = 0;
				for(Set<Fact> obsLandmark: achievedLandmarksFromObservationsForGoal)
					if(landmarkOrdering.getOrdering().contains(obsLandmark))
						subgoalCounter++;
				
				subgoalsAchievedLandmarks.put(landmarkOrdering.getSubGoal(), subgoalCounter);
			}
			/* Estimating goal completion using Goal Completion Heuristic - hgc(G) */
			float goalCompletion = getEstimate(goal, achievedLandmarksForAllGoals.get(goal));
			System.out.println("$> " + goal + ": " + goalCompletion);
			goalsToEstimativeGoalCompletion.put(goal, goalCompletion);
			if(goalCompletion > maxGoalCompletion)
				maxGoalCompletion = goalCompletion;
		}
		System.out.println("\n$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		System.out.println("$> Recognized goal(s) within the threshold " + this.threshold + ": ");
		Set<GroundFact> recognizedGoals = new HashSet<>();
		for(GroundFact goal: goalsToEstimativeGoalCompletion.keySet()){
			float estimatedValue = goalsToEstimativeGoalCompletion.get(goal);
			if(estimatedValue >= (maxGoalCompletion - threshold)){
				recognizedGoals.add(goal);
				System.out.println("$> " + goal + ": " + estimatedValue);
			}
		}
		
		this.amountOfRecognizedGoals = recognizedGoals.size();
		boolean correctGoalRecognized = recognizedGoals.contains(this.realGoal);
		System.out.println("\n<?> Correct goal: " + this.realGoal);
		System.out.println("<?> Was the correct goal recognized correctly? " + correctGoalRecognized);
		Process p = Runtime.getRuntime().exec("rm -rf domain.pddl template.pddl templateInitial.pddl obs.dat hyps.dat plan.png real_hyp.dat");
		p.waitFor();
		return correctGoalRecognized;
	}
	
	/**
	 * This method estimates the percentage of completion of a goal using Goal Completion Heuristic - hgc(G). 
	 * 
	 * @param goal
	 * @param subgoalAchievedLandmarks
	 * @param subgoalLandmarksOrdering
	 * @return
	 */
	public float getEstimateGoalCompletion(GroundFact goal, Map<Fact, Integer> subgoalAchievedLandmarks, List<LandmarkOrdering> subgoalLandmarksOrdering){
		float subgoalCompletion = 0f;
		Map<Fact, Integer> subgoalsAchievedLandmarks = subgoalAchievedLandmarks;
		for(LandmarkOrdering subgoalLandmarks: subgoalLandmarksOrdering){
			float subgoalAmountOfAchievedLandmarks = subgoalsAchievedLandmarks.get(subgoalLandmarks.getSubGoal());
			float subgoalAmountOfLandmarks = subgoalLandmarks.getAmountOfLandmarks();
			subgoalCompletion += (subgoalAmountOfAchievedLandmarks / subgoalAmountOfLandmarks);
		}
		return (subgoalCompletion / goal.getFacts().size());
	}
	
	/**
	 * This method estimates the percentage of completion of a goal using Goal Completion Heuristic - hgc(G).
	 * @param goal
	 * @param achievedLandmarks
	 * @return
	 */
	public float getEstimate(GroundFact goal, Set<Set<Fact>> achievedLandmarks){
		float amountOfAchievedLandmarks = achievedLandmarks.size(); 
		float amountOfLandmarks = this.goalsToLandmarkExtractor.get(goal).getAmountOfLandmarks();
		return (amountOfAchievedLandmarks / amountOfLandmarks);
	}
}