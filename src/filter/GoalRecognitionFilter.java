package filter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import extracting.PartialLandmarkGenerator;
import javaff.data.Action;
import javaff.data.Fact;
import javaff.data.GroundFact;
import javaff.planning.STRIPSState;
import landmark.LandmarkExtractor;
import landmark.LandmarkOrdering;
import recognizer.Recognizer;

/**
 * This class defines a landmark-based goal/plan recognition filter that can be used to filter candidate goals from observations. 
 * More specifically, it filters candidate goals based on the evidence of fact landmarks in preconditions and effects of observed actions (observations).
 * 
 * @author Ramon Fraga Pereira
 * 
 */
public class GoalRecognitionFilter extends Recognizer {
	
	public GoalRecognitionFilter(String fileName, float threshold){
		super(fileName, threshold);
	}
	
	public GoalRecognitionFilter(String domainFile, String problemFile, String candidateGoalsFile, String observationsFile, String correctGoalFile, float threshold) {
		super(domainFile, problemFile, candidateGoalsFile, observationsFile, correctGoalFile, threshold);
	}
	
	/** 
	 * This method analyzes fact landmarks in preconditions and effects of observed actions, and selects goals, from a set of candidate goals, 
	 * that have achieved most of their associated landmarks.
	 * 
	 * @param generateFile If it is true, it generates a .tar.bz2 file by adding the set of filtered goals. 
	 * This file can be used as input for others planning-based goal/plan recognition appraoches.
	 * 
	 * @return Map<GroundFact, Map<Fact, Integer>>
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Set<GroundFact> filter(boolean generateFile) throws IOException, InterruptedException{
		Set<GroundFact> filteredGoals = new HashSet<>();
		System.out.println("# Initial state: " + this.initialState + "\n");
		System.out.println("# Observations: ");
		STRIPSState currentState = this.initialState;
		for(Action o: this.observations)
			System.out.println("\t>$ " + o);
		
		Map<GroundFact, Float> goalsAchievedLandmarks = new HashMap<GroundFact, Float>();
		float highestPercentageAchievedLandmarks = 0;
		for(GroundFact goal: this.goals){
			/* Extracting landmarks for a candidate goal from the initial state */
			PartialLandmarkGenerator landmarkGenerator = new PartialLandmarkGenerator(this.initialState, goal.getFacts(), this.groundProblem.getActions());
			landmarkGenerator.extractLandmarks();
			this.goalsToLandmarkExtractor.put(goal, landmarkGenerator);
			
			Set<Set<Fact>> achievedLandmarksGoal = new HashSet<>();
			
			/* Computing achieved landmarks from observations for a candidate goal */
			System.out.println("\n---> Goal: " + goal);
			/* Extracting landmarks for a candidate goal from the initial state */
			LandmarkExtractor landmarkExtractor = this.goalsToLandmarkExtractor.get(goal); 
			if(landmarkExtractor == null){
				landmarkExtractor = new PartialLandmarkGenerator(this.initialState, goal.getFacts(), this.groundProblem.getActions());
				landmarkExtractor.extractLandmarks();
				this.goalsToLandmarkExtractor.put(goal, landmarkExtractor);			
			}
			System.out.println("\t\t #> Ordered Landmarks: ");
			for(LandmarkOrdering landmarkOrdering: landmarkExtractor.getLandmarksOrdering())
				System.out.println("\t\t # " + landmarkOrdering);
			
			System.out.println();
			
			/* Computing achieved landmarks from observations for a candidate goal */
			for(Action o: observations){
				Set<Fact> observedFacts = new HashSet<>();
				System.out.println("\t>$ " + o);
				observedFacts.addAll(o.getAddPropositions());
				observedFacts.addAll(o.getPreconditions());
				observedFacts.addAll(currentState.getFacts());
				for(LandmarkOrdering landmarkOrdering: landmarkExtractor.getLandmarksOrdering()){
					for(Set<Fact> factsOrdering: landmarkOrdering.getOrdering()){
						if(observedFacts.containsAll(factsOrdering) && !achievedLandmarksGoal.contains(factsOrdering)){
							achievedLandmarksGoal.add(factsOrdering);
							Set<Set<Fact>> inferredFacts = this.inferFactLandmarks(landmarkOrdering, observedFacts, achievedLandmarksGoal);
							achievedLandmarksGoal.addAll(inferredFacts);
						}
					}
				}
				currentState = (STRIPSState) currentState.apply(o);
			}
			System.out.println("\n\t># Achieved Landmarks in Observations: \n\t\t" + achievedLandmarksGoal);
			System.out.println();
			
			float goalAchievedLandmarks = achievedLandmarksGoal.size();
			float amountOfLandmarksGoal = landmarkGenerator.getAmountOfLandmarks();
			System.out.println("\t+> Amount of Fact Landmarks: " + amountOfLandmarksGoal);
			System.out.println("\t+> Achieved Fact Landmarks: " + goalAchievedLandmarks);
			
			/* Calculating the percentage of achieved landmarks */
			float percentageAchievedLandmarks = (goalAchievedLandmarks / amountOfLandmarksGoal);
			
			if(percentageAchievedLandmarks > highestPercentageAchievedLandmarks)
				highestPercentageAchievedLandmarks = percentageAchievedLandmarks;
			
			System.out.println("\t+> Percentage: " + percentageAchievedLandmarks);
			
			if(percentageAchievedLandmarks == 0) 
				continue;
			
			goalsAchievedLandmarks.put(goal, percentageAchievedLandmarks);
			filteredGoals.add(goal);
		}
		String filteredGoalsToFile = "";
		if(goalsAchievedLandmarks.keySet().isEmpty()){
			for(GroundFact g: this.goals){
				String goal = this.getGoalString(g);
				filteredGoalsToFile += goal + "\n";
				filteredGoals.add(g);
			}
		} else {
			for(GroundFact g: goalsAchievedLandmarks.keySet()){
				String goal = "";
				if(goalsAchievedLandmarks.get(g) >= (highestPercentageAchievedLandmarks - threshold)){
					goal = this.getGoalString(g);
					filteredGoalsToFile += goal + "\n";
				} else filteredGoals.remove(g);
			}
		}
		System.out.println("\n$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		System.out.println("$$$$$$$$$$ Filtered Goals $$$$$$$$$$$$");
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$\n");
		for(GroundFact goal: filteredGoals)
			System.out.println("$> " + goal + ": " + (goalsAchievedLandmarks.get(goal) == null ? 0 : goalsAchievedLandmarks.get(goal)));
		
		if(generateFile){
			File realGoalFile = new File("real_hyp.dat");
			FileWriter fw = new FileWriter(realGoalFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			String realGoal = "";
			int i = 0;
			for(Fact f: this.realGoal.getFacts()){
				int size = this.realGoal.getFacts().size();
				i++;
				realGoal += "(" + f.toString() + ")" + (i == size ? "" : ",");
			}
			bw.write(realGoal);
			bw.close();
			
			File newHypsGoalFile = new File("hyps.dat");
			fw = new FileWriter(newHypsGoalFile.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write(filteredGoalsToFile);
			bw.close();
			
			Runtime.getRuntime().exec("tar -jcvf " + this.planRecognitionFile.replace(".tar.bz2", "") + "_FILTERED.tar.bz2" +" domain.pddl template.pddl hyps.dat real_hyp.dat obs.dat");
			Thread.sleep(50);
			System.out.println("\nThe set of filtered candidate goals (hyps.dat) are in: " + this.planRecognitionFile.replace(".tar.bz2", "") + "_FILTERED.tar.bz2");	
		}			
		Runtime.getRuntime().exec("rm -rf domain.pddl template.pddl templateInitial.pddl hyps.dat real_hyp.dat obs.dat");
		return filteredGoals;
	}
	
	private String getGoalString(GroundFact g){
		String goal = "";
		int size = g.getFacts().size();
		int i = 0;
		for(Fact f: g.getFacts()){
			i++;
			goal += "(" + f.toString() + ")" + (i == size ? "" : ",");
		}
		return goal;
	}
}