package recognizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javaff.data.Action;
import javaff.data.Fact;
import javaff.data.GroundFact;
import javaff.data.GroundProblem;
import javaff.data.strips.And;
import javaff.data.strips.Not;
import javaff.planning.STRIPSState;
import landmark.LandmarkExtractor;
import landmark.LandmarkOrdering;
import parser.PDDLParser;
import extracting.PartialLandmarkGenerator;

/**
 * 
 * @author Ramon Fraga Pereira
 *
 */
public class PlanRecognizer {

	public enum Heuristic {
		COMPLETION, UNIQUENESS;
	};
	
	protected GroundProblem groundProblem;
	private List<Action> observations;
	private List<GroundFact> goals;
	private GroundFact realGoal;
	private STRIPSState initialState;
	private String planRecognitionFile;
	protected Map<GroundFact, LandmarkExtractor> goalsLandmarks;
	private Map<GroundFact, Set<Fact>> mapGoalsFactLandmarks;
	private Map<GroundFact, Set<Set<Fact>>> mapGoalsLandmarks;
	private Map<GroundFact, Float> goalsTotalLandmarksUniqueness;
	private Map<Fact, Float> factLandmarksUniqueness;
	private Map<Set<Fact>, Float> landmarksUniqueness;
	private int amountOfRecognizedGoals = 0;
	private float threshold = 0.0f;
	
	public PlanRecognizer(String fileName, float threshold){
		try{
			this.planRecognitionFile = fileName;
			if(!Files.isReadable(Paths.get(fileName)))
				throw new IOException(fileName + " not found.");
			
			System.out.println("tar -jxvf " + this.planRecognitionFile);
			Process p = Runtime.getRuntime().exec("tar -jxvf " + this.planRecognitionFile);
			p.waitFor();
			String domainFilePath = "domain.pddl";
			Path path = Paths.get(domainFilePath);
			String domainContent = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
			domainContent = domainContent.replace("(increase (total-cost) 1)", "");
			File domain = new File("domain.pddl");
			FileWriter fw = new FileWriter(domain.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(domainContent);
			bw.close();
			
			String initialFilePath = "templateInitial.pddl";
			String observationsFilePath = "obs.dat";
			String goalsFilePath = "hyps.dat";
			String realGoalFilePath = "real_hyp.dat";
			path = Paths.get("template.pddl");
			String initialContent = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
			initialContent = initialContent.replace("<HYPOTHESIS>", "");
			File templateInitial = new File("templateInitial.pddl");
			fw = new FileWriter(templateInitial.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write(initialContent);
			bw.close();
			
			this.threshold = threshold;
			this.groundProblem = PDDLParser.getGroundDomainProblem(domainFilePath, initialFilePath);
			this.observations = PDDLParser.getObservations(groundProblem, observationsFilePath);
			this.goals = PDDLParser.getGoals(groundProblem, goalsFilePath);
			this.realGoal = PDDLParser.getGoals(groundProblem, realGoalFilePath).get(0);
			this.initialState = groundProblem.getSTRIPSInitialState();
			this.goalsLandmarks = new HashMap<>();
			this.mapGoalsFactLandmarks = new HashMap<>();
			this.mapGoalsLandmarks = new HashMap<>();
			this.factLandmarksUniqueness = new HashMap<>();
			this.landmarksUniqueness = new HashMap<>();
			this.goalsTotalLandmarksUniqueness = new HashMap<>();
		} catch (IOException | InterruptedException e){
			e.printStackTrace();
		}
	}
	
	public boolean recognize() throws IOException, InterruptedException{
		Map<GroundFact, Float> goalsCompletion = recognize(false);
		this.amountOfRecognizedGoals = goalsCompletion.keySet().size();
		for(GroundFact goal: goalsCompletion.keySet())
				if(this.realGoal.equals(goal))
					return true;
		
		return false;
	}
	
	private Map<GroundFact, Float> recognize(boolean filter) throws IOException, InterruptedException{
		Map<GroundFact, Map<Fact, Integer>> filteredGoals = this.newFilter(filter);
		Map<GroundFact, Float> goalsCompletion = new HashMap<>();
		Map<GroundFact, Float> goalsCompletionReturn = new HashMap<>();
		float maxGoalCompletion = 0f;
		for(GroundFact goal: filteredGoals.keySet()){
			List<LandmarkOrdering> subgoalLandmarksOrdering = this.goalsLandmarks.get(goal).getLandmarksOrdering();
			float goalCompletion = heuristicPlanRecognition(goal, filteredGoals.get(goal), subgoalLandmarksOrdering);
			System.out.println("$> " + goal + ": " + goalCompletion);
			goalsCompletion.put(goal, goalCompletion);
			if(goalCompletion > maxGoalCompletion)
				maxGoalCompletion = goalCompletion;
		}
		for(GroundFact goal: goalsCompletion.keySet())
			if(goalsCompletion.get(goal) >= (maxGoalCompletion - threshold))
				goalsCompletionReturn.put(goal, goalsCompletion.get(goal));

		return goalsCompletionReturn;
	}
	
	public float heuristicPlanRecognition(GroundFact goal, Map<Fact, Integer> subgoalAchievedLandmarks, List<LandmarkOrdering> subgoalLandmarksOrdering){
		float subgoalCompletion = 0f;
		Map<Fact, Integer> subgoalsAchievedLandmarks = subgoalAchievedLandmarks;
		for(LandmarkOrdering subgoalLandmarks: subgoalLandmarksOrdering){
			float subgoalAmountOfAchievedLandmarks = subgoalsAchievedLandmarks.get(subgoalLandmarks.getSubGoal());
			float subgoalAmountOfLandmarks = subgoalLandmarks.getAmountOfLandmarks();
			subgoalCompletion += (subgoalAmountOfAchievedLandmarks / subgoalAmountOfLandmarks);
		}
		return (subgoalCompletion / goal.getFacts().size());
	}
	
	public Map<GroundFact, Map<Fact, Integer>> newFilter(boolean generateFile) throws IOException, InterruptedException{
		Map<GroundFact, Map<Fact, Integer>> filteredGoals = new HashMap<>();
		System.out.println("# Initial state: " + this.initialState + "\n");
		System.out.println("# Observations: ");
		STRIPSState currentState = this.initialState;
		for(Action o: this.observations)
			System.out.println("\t>$ " + o);
		
		Map<GroundFact, Float> goalsAchievedLandmarks = new HashMap<GroundFact, Float>();
		float highestPercentageAchievedLandmarks = 0;
		for(GroundFact goal: this.goals){
			System.out.println("\n---> Goal: " + goal);
			PartialLandmarkGenerator landmarkGenerator = new PartialLandmarkGenerator(this.initialState, goal.getFacts(), this.groundProblem.getActions());
			landmarkGenerator.extractLandmarks();
			this.goalsLandmarks.put(goal, landmarkGenerator);
			Map<Fact, Integer> subgoalsAchievedLandmarks = new HashMap<>();
			Set<Set<Fact>> observedLandmarks = new HashSet<>();
			Set<Fact> observedFactLandmarks = new HashSet<>();
			for(Action o: this.observations){
				Set<Fact> observedFacts = new HashSet<>();
				System.out.println("\t>$ " + o);
				observedFacts.addAll(o.getAddPropositions());
				observedFacts.addAll(o.getPreconditions());
				observedFacts.addAll(currentState.getFacts());
				for(LandmarkOrdering landmarkOrdering: landmarkGenerator.getLandmarksOrdering()){
					System.out.println("\t\t # " + landmarkOrdering);
					for(Set<Fact> factsOrdering: landmarkOrdering.getOrdering()){
						if(observedFacts.containsAll(factsOrdering) && !observedLandmarks.contains(factsOrdering)){
							observedLandmarks.add(factsOrdering);
							Set<Set<Fact>> inferredFacts = this.inferFactLandmarks(landmarkOrdering, observedFacts, observedLandmarks);
							observedLandmarks.addAll(inferredFacts);
						}
					}
				}
				if(observedLandmarks.isEmpty())
					for(Fact obs: observedFacts)
						if(landmarkGenerator.getFactLandmarks().contains(obs))
							observedFactLandmarks.add(obs);
				
				currentState = (STRIPSState) currentState.apply(o);
			}
			/* Counting achieved landmarks for every subgoals */
			for(LandmarkOrdering landmarkOrdering: landmarkGenerator.getLandmarksOrdering()){
				int subgoalCounter = 0;
				for(Set<Fact> obsLandmark: observedLandmarks)
					if(landmarkOrdering.getOrdering().contains(obsLandmark))
						subgoalCounter++;
				
				subgoalsAchievedLandmarks.put(landmarkOrdering.getSubGoal(), subgoalCounter);
			}			
			float goalAchievedLandmarks = observedLandmarks.size();
			float amountOfLandmarksGoal = landmarkGenerator.getAmountOfLandmarks();
			System.out.println("\t+> Amount of Fact Landmarks: " + amountOfLandmarksGoal);
			System.out.println("\t+> Achieved Fact Landmarks: " + goalAchievedLandmarks);
			float percentageAchievedLandmarks = goalAchievedLandmarks/amountOfLandmarksGoal;
			
			if(percentageAchievedLandmarks > highestPercentageAchievedLandmarks)
				highestPercentageAchievedLandmarks = percentageAchievedLandmarks;
			
			System.out.println("\t+> Percentage: " + percentageAchievedLandmarks);
			System.out.println("\t\t+> Observed Landmarks: " + observedLandmarks);
			
			if(percentageAchievedLandmarks == 0) continue;
			
			goalsAchievedLandmarks.put(goal, percentageAchievedLandmarks);
			filteredGoals.put(goal, subgoalsAchievedLandmarks);
			//System.out.println(subgoalsAchievedLandmarks);
		}
		//System.out.println("----------------------------------------------------------------------");
		String filteredGoalsToFile = "";
		for(GroundFact g: goalsAchievedLandmarks.keySet()){
			String goal = "";
			if(goalsAchievedLandmarks.get(g) >= (highestPercentageAchievedLandmarks - threshold)){
				int size = g.getFacts().size();
				int i = 0;
				for(Fact f: g.getFacts()){
					i++;
					goal += "(" + f.toString() + ")" + (i == size ? "" : ",");
				}
				filteredGoalsToFile += goal + "\n";
			} else filteredGoals.remove(g);
		}
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

	public Map<GroundFact, Map<Fact, Integer>> filter(boolean generateFile) throws IOException, InterruptedException{
		Map<GroundFact, Map<Fact, Integer>> filteredGoals = new HashMap<>();
		System.out.println("# Initial state: " + this.initialState + "\n");
		System.out.println("# Observations: ");
		for(Action o: this.observations)
			System.out.println("\t>$ " + o);
		
		Map<GroundFact, Float> goalsAchievedLandmarks = new HashMap<GroundFact, Float>();
		float highestPercentageAchievedLandmarks = 0;
		for(GroundFact goal: this.goals){
			System.out.println("\n---> Goal: " + goal);
			Map<Fact, Integer> subgoalsAchievedLandmarks = new HashMap<>();
			LandmarkExtractor landmarkGenerator = new PartialLandmarkGenerator(this.initialState, goal.getFacts(), this.groundProblem.getActions());
			//LandmarkExtractor landmarkGenerator = new CompleteLandmarkGenerator(this.initialState, goal.getFacts(), this.groundProblem.getActions());
			landmarkGenerator.extractLandmarks();
			this.goalsLandmarks.put(goal, landmarkGenerator);
			Set<Set<Fact>> observedFactLandmarks = new HashSet<>();
			List<LandmarkOrdering> orderedLandmarks = landmarkGenerator.getLandmarksOrdering();
			for(Fact subgoal: goal.getFacts())
				if(this.initialState.isTrue(subgoal))
					observedFactLandmarks.add(new And(subgoal).getFacts());
			
			for(Action o: this.observations){
				System.out.println("$> Observed action: " + o);
				System.out.println("\t ? Preconditions: " + o.getPreconditions());
				System.out.println("\t + Effects: " + o.getAddPropositions());
				for(LandmarkOrdering landmarkOrdering: orderedLandmarks){
					System.out.println("\t# " + landmarkOrdering.getSubGoal());
					System.out.println("\t\t " + landmarkOrdering.getOrdering());
					
					/* Verifying landmarks in observation preconditions */
					if(landmarkOrdering.getOrdering().contains(o.getPreconditions()) && !observedFactLandmarks.contains(o.getPreconditions())){
						observedFactLandmarks.add(o.getPreconditions());
						System.out.println("\t\tpre) " + o.getPreconditions());

						/* Inferring other landmarks from observed landmarks in the observation preconditions */
						float landmarkIndexOrdering = landmarkOrdering.getOrdering().indexOf(o.getPreconditions())+1;
						for(int i=0; i<landmarkIndexOrdering-1;i++){
							if(!observedFactLandmarks.contains(landmarkOrdering.getOrdering().get(i))){
								observedFactLandmarks.add(landmarkOrdering.getOrdering().get(i));
								System.out.println("\t\tinf) " + landmarkOrdering.getOrdering().get(i));
							}
						}
					}
					/* Verifying landmarks in observation effects */
					for(Fact landmark: landmarkGenerator.getFactLandmarks()){
						if(!observedFactLandmarks.contains(landmark.getFacts())){
							if(o.getAddPropositions().contains(landmark)){
								if(goal.getFacts().contains(landmark)){
									System.out.println("\t\teff) " + landmark);
									observedFactLandmarks.add(new And(landmark).getFacts());
								}
							}
						}
					}
				}
			}
			/* Counting achieved landmarks for every subgoals */
			for(LandmarkOrdering landmarkOrdering: orderedLandmarks){
				int subgoalCounter = 0;
				for(Set<Fact> obsLandmark: observedFactLandmarks)
					if(landmarkOrdering.getOrdering().contains(obsLandmark))
						subgoalCounter++;
				subgoalsAchievedLandmarks.put(landmarkOrdering.getSubGoal(), subgoalCounter);
			}
			float goalAchievedLandmarks = observedFactLandmarks.size();
			float amountOfLandmarksGoal = landmarkGenerator.getAmountOfLandmarks();
			System.out.println("\t$> Amount of Fact Landmarks: " + amountOfLandmarksGoal);
			System.out.println("\t$> Achieved Fact Landmarks: " + goalAchievedLandmarks);
			float percentageAchievedLandmarks = goalAchievedLandmarks/amountOfLandmarksGoal;
			if(percentageAchievedLandmarks > highestPercentageAchievedLandmarks)
				highestPercentageAchievedLandmarks = percentageAchievedLandmarks;
			
			goalsAchievedLandmarks.put(goal, percentageAchievedLandmarks);
			filteredGoals.put(goal, subgoalsAchievedLandmarks);
		}
		System.out.println("----------------------------------------------------------------------");
		String filteredGoalsToFile = "";
		for(GroundFact g: goalsAchievedLandmarks.keySet()){
			String goal = "";
			if(highestPercentageAchievedLandmarks == goalsAchievedLandmarks.get(g)){
				int size = g.getFacts().size();
				int i = 0;
				for(Fact f: g.getFacts()){
					i++;
					goal += "(" + f.toString() + ")" + (i == size ? "" : ",");
				}
				filteredGoalsToFile += goal + "\n";
			} else filteredGoals.remove(g);
		}
		if(generateFile){
			File newHypsGoalFile = new File("hyps.dat");
			FileWriter fw = new FileWriter(newHypsGoalFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(filteredGoalsToFile);
			bw.close();
			
			Runtime.getRuntime().exec("tar -jcvf " + this.planRecognitionFile.replace(".tar.bz2", "") + "_FILTERED.tar.bz2" +" domain.pddl template.pddl hyps.dat real_hyp.dat obs.dat");
			Thread.sleep(50);
			System.out.println("\nThe set of filtered candidate goals (hyps.dat) are in: " + this.planRecognitionFile.replace(".tar.bz2", "") + "_FILTERED.tar.bz2");	
		}			
		Runtime.getRuntime().exec("rm -rf domain.pddl template.pddl templateInitial.pddl hyps.dat real_hyp.dat obs.dat");
		return filteredGoals;
	}
	
	private Set<Set<Fact>> inferFactLandmarks(LandmarkOrdering landmarkOrdering, Set<Fact> observedFacts, Set<Set<Fact>> observedLandmarks){
		Set<Set<Fact>> inferredFactLandmarks = new HashSet<>();
		float landmarkIndexOrdering = landmarkOrdering.getOrdering().indexOf(observedFacts)+1;
		for(int i=0; i<landmarkIndexOrdering-1;i++){
			if(!observedLandmarks.contains(landmarkOrdering.getOrdering().get(i))){
				inferredFactLandmarks.add(landmarkOrdering.getOrdering().get(i));
				System.out.println("\t\t\tinf) " + landmarkOrdering.getOrdering().get(i));
			}
		}
		return inferredFactLandmarks;
	}
	
	public Set<Set<Fact>> getRemovedObservedLandmarks(STRIPSState currentState, Set<Set<Fact>> observedLandmarks){
		Set<Set<Fact>> landmarksToRemove = new HashSet<>();
		for(Fact notF: currentState.getFalseFacts()){
			for(Set<Fact> obsLandmarks: observedLandmarks){
				for(Fact obsF: obsLandmarks){
					Fact notFact = new Not(obsF);
					if(notFact.toString().equals(notF.toString())){
						landmarksToRemove.add(obsLandmarks);
					}
				}
			}
		}
		return landmarksToRemove;
	}
	
	public void verifyLandmarksUniqueness(){
		Set<Set<Fact>> extractedLandmarks = new HashSet<>();
		for(GroundFact goal: this.goals){
			PartialLandmarkGenerator landmarkGenerator = new PartialLandmarkGenerator(initialState, goal.getFacts(), groundProblem.getActions());
			landmarkGenerator.extractLandmarks();
			extractedLandmarks.addAll(landmarkGenerator.getLandmarks());
			this.mapGoalsLandmarks.put(goal, landmarkGenerator.getLandmarks());
			this.goalsLandmarks.put(goal, landmarkGenerator);
		}
		for(Set<Fact> landmark: extractedLandmarks){
			int count = 0;
			for(GroundFact goal: this.goals){
				Set<Set<Fact>> goalLandmarks = this.mapGoalsLandmarks.get(goal);
				if(goalLandmarks.contains(landmark))
					count++;
			}
			this.landmarksUniqueness.put(landmark, new Float((float) 1/count));
		}
		for(GroundFact goal: this.goals){
			Set<Set<Fact>> goalLandmarks = this.mapGoalsLandmarks.get(goal);
			float total = 0;
			System.out.println(goal);
			for(Set<Fact> landmark: goalLandmarks){
				float landmarkValue = this.landmarksUniqueness.get(landmark);
				System.out.println("\t" + landmark + " = " + landmarkValue);
				total = total + landmarkValue;
			}
			this.goalsTotalLandmarksUniqueness.put(goal, total);
		}
	}
	
	public void verifyFactLandmarksUniqueness(){
		Set<Fact> extractedLandmarks = new HashSet<>();
		for(GroundFact goal: this.goals){
			PartialLandmarkGenerator landmarkGenerator = new PartialLandmarkGenerator(initialState, goal.getFacts(), groundProblem.getActions());
			landmarkGenerator.extractLandmarks();
			extractedLandmarks.addAll(landmarkGenerator.getFactLandmarks());
			this.mapGoalsFactLandmarks.put(goal, landmarkGenerator.getFactLandmarks());
			this.goalsLandmarks.put(goal, landmarkGenerator);
		}
		for(Fact factLandmark : extractedLandmarks){
			int count = 0;
			for(GroundFact goal: this.goals){
				Set<Fact> goalFactLandmarks = this.mapGoalsFactLandmarks.get(goal);
				if(goalFactLandmarks.contains(factLandmark))
					count++;
			}
			this.factLandmarksUniqueness.put(factLandmark, new Float((float) 1/count));
		}
		for(GroundFact goal: this.goals){
			Set<Fact> goalFactLandmarks = this.mapGoalsFactLandmarks.get(goal);
			float total = 0;
			for(Fact factLandmark: goalFactLandmarks)
				total = total + this.factLandmarksUniqueness.get(factLandmark);
			
			this.goalsTotalLandmarksUniqueness.put(goal, total);
		}
	}
	
	boolean recognizeUsingFactLandmarksUniqueness(){
		this.verifyFactLandmarksUniqueness();
		
		System.out.println("# Initial state: " + this.initialState + "\n");
		System.out.println("# Observations: ");
		STRIPSState currentState = this.initialState;
		Map<GroundFact, Float> goalsTotalFromObservations = new HashMap<>();
		float highestTotal = 0; 
		for(GroundFact goal: this.goals){
			float totalGoal = 0;
			System.out.println("\n---> Goal: " + goal);
			Set<Fact> achievedFactLandmarks = new HashSet<>();
			for(Action o: this.observations){
				Set<Fact> goalFactLandmarks = this.mapGoalsFactLandmarks.get(goal);
				System.out.println("\t>$ " + o);
				System.out.println("\tPRE) " + o.getPreconditions());
				System.out.println("\tADD) " + o.getAddPropositions());
				System.out.println("\tDEL) " + o.getDeletePropositions());
				
				Set<Fact> observedFactLandmarks = new HashSet<>();
				observedFactLandmarks.addAll(o.getAddPropositions());
				observedFactLandmarks.addAll(o.getPreconditions());
				observedFactLandmarks.addAll(currentState.getFacts());
				
				for(Fact obsFact: observedFactLandmarks)
					if(goalFactLandmarks.contains(obsFact))
						achievedFactLandmarks.add(obsFact);
						
				currentState = (STRIPSState) currentState.apply(o);
			}
			for(Fact achievedLandmark: achievedFactLandmarks){
				Float landmarkValue = this.factLandmarksUniqueness.get(achievedLandmark);
				totalGoal = totalGoal + landmarkValue;
				System.out.println("\t\t- " + achievedLandmark + " = " + landmarkValue);
			}
			System.out.println();
			for(LandmarkOrdering lo: this.goalsLandmarks.get(goal).getLandmarksOrdering())
				System.out.println("\t" + lo);
			
			float totalPercentageGoal = (totalGoal / this.goalsTotalLandmarksUniqueness.get(goal));
			BigDecimal totalP = new BigDecimal(totalPercentageGoal);
			totalPercentageGoal = totalP.setScale(2, BigDecimal.ROUND_UP).floatValue();
			System.out.println("\n\t$$$$> Total = " + (totalPercentageGoal));
			System.out.println("\t\t" + totalGoal + " / " + this.goalsTotalLandmarksUniqueness.get(goal));
			if(totalPercentageGoal > highestTotal)
				highestTotal = totalPercentageGoal;
			
			goalsTotalFromObservations.put(goal, totalPercentageGoal);
		}
		Set<GroundFact> recognizedGoals = new HashSet<>();
		for(GroundFact goal : goalsTotalFromObservations.keySet()){
			if(goalsTotalFromObservations.get(goal) >= (highestTotal - this.threshold))
				recognizedGoals.add(goal);
		}
		this.amountOfRecognizedGoals = recognizedGoals.size();
		return recognizedGoals.contains(this.realGoal);
	}
	
	public boolean recognizeUsingLandmarksUniqueness(){
		this.verifyLandmarksUniqueness();
		
		System.out.println("# Initial state: " + this.initialState + "\n");
		System.out.println("# Observations: ");
		STRIPSState currentState = this.initialState;
		Map<GroundFact, Float> goalsTotalFromObservations = new HashMap<>();
		float highestTotal = 0;
		for(GroundFact goal: this.goals){
			float totalGoal = 0;
			//System.out.println("\n---> Goal: " + goal);
			LandmarkExtractor landmarkExtractor = this.goalsLandmarks.get(goal);
			Set<Set<Fact>> observedLandmarks = new HashSet<>();
			Set<Fact> allObservedFacts = new HashSet<>();
			for(Action o: this.observations){
				//System.out.println("\t>$ " + o);
				//System.out.println("\tPRE) " + o.getPreconditions());
				//System.out.println("\tADD) " + o.getAddPropositions());
				//System.out.println("\tDEL) " + o.getDeletePropositions());
				
				Set<Fact> observedFacts = new HashSet<>();
				observedFacts.addAll(o.getAddPropositions());
				observedFacts.addAll(o.getPreconditions());
				allObservedFacts.addAll(observedFacts);
				observedFacts.addAll(currentState.getFacts());
				
				for(LandmarkOrdering landmarkOrdering: landmarkExtractor.getLandmarksOrdering()){
					//System.out.println("\t\t # " + landmarkOrdering);
					for(Set<Fact> factsOrdering: landmarkOrdering.getOrdering()){
						if(observedFacts.containsAll(factsOrdering) && !observedLandmarks.contains(factsOrdering)){
							observedLandmarks.add(factsOrdering);
							//System.out.println("\t\t\tobs) " + factsOrdering);
							Set<Set<Fact>> inferredFacts = this.inferFactLandmarks(landmarkOrdering, observedFacts, observedLandmarks);
							observedLandmarks.addAll(inferredFacts);
						}
					}
				}
				currentState = (STRIPSState) currentState.apply(o);
			}
			for(Set<Fact> obsLandmark: observedLandmarks){
				float landmarkValue = this.landmarksUniqueness.get(obsLandmark);
				System.out.println(obsLandmark + " = (" + landmarkValue + ") * " + landmarkExtractor.getLandmarksAndTheirRepetition().get(obsLandmark));
				totalGoal = totalGoal + landmarkValue; 
			}
			float totalPercentageGoal = (totalGoal / this.goalsTotalLandmarksUniqueness.get(goal));
			BigDecimal totalP = new BigDecimal(totalPercentageGoal);
			totalPercentageGoal = totalP.setScale(2, BigDecimal.ROUND_UP).floatValue();

			System.out.println("\n\t$$$$> Total = " + (totalPercentageGoal));
			System.out.println("\t\t" + totalGoal + " / " + this.goalsTotalLandmarksUniqueness.get(goal));
			
			if(totalPercentageGoal > highestTotal)
				highestTotal = totalPercentageGoal;
			
			goalsTotalFromObservations.put(goal, totalPercentageGoal);
		}
		Set<GroundFact> recognizedGoals = new HashSet<>();
		for(GroundFact goal : goalsTotalFromObservations.keySet()){
			if(goalsTotalFromObservations.get(goal) >= (highestTotal - this.threshold))
				recognizedGoals.add(goal);
		}
		this.amountOfRecognizedGoals = recognizedGoals.size();
		return recognizedGoals.contains(this.realGoal);
	}
	
	public float getAverageOfFactLandmarks(){
		int size = 0;
		for(GroundFact goal: this.goals){
			LandmarkExtractor landmarkGenerator = new PartialLandmarkGenerator(this.initialState, goal.getFacts(), this.groundProblem.getActions());
			landmarkGenerator.extractLandmarks();
			size += landmarkGenerator.getFactLandmarks().size();	
		}
		float avg = (size / this.goals.size());
		System.out.println(avg);
		return avg;
	}
	
	public STRIPSState getInitialState() {
		return initialState;
	}
	
	public List<GroundFact> getGoals() {
		return goals;
	}
	
	public int getObservationsSize() {
		return observations.size();
	}
	
	public GroundFact getRealGoal() {
		return realGoal;
	}
	
	public int getAmountOfRecognizedGoals() {
		return amountOfRecognizedGoals;
	}
	
	public int getAmountOfCandidateGoals(){
		return this.goals.size();
	}
	
	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}
	
	public String getPlanRecognitionFile() {
		return planRecognitionFile;
	}
}