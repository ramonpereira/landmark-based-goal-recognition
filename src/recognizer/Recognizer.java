package recognizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import extracting.PartialLandmarkGenerator;
import javaff.data.Action;
import javaff.data.Fact;
import javaff.data.GroundFact;
import javaff.data.GroundProblem;
import javaff.planning.STRIPSState;
import landmark.LandmarkExtractor;
import landmark.LandmarkOrdering;
import parser.PDDLParser;

/**
 * 
 * @author Ramon Fraga Pereira
 *
 */
public abstract class Recognizer {

	protected GroundProblem groundProblem;
	protected List<Action> observations;
	protected List<GroundFact> goals;
	protected GroundFact realGoal;
	protected STRIPSState initialState;
	protected String planRecognitionFile;
	protected Map<GroundFact, LandmarkExtractor> goalsToLandmarkExtractor;
	protected Map<GroundFact, Set<Fact>> goalsToFactLandmarks;
	protected Map<GroundFact, Set<Set<Fact>>> goalsToLandmarks;
	protected int amountOfRecognizedGoals = 0;
	protected float threshold = 0.0f;
	
	public Recognizer(String fileName, float threshold){
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
			this.goalsToLandmarkExtractor = new HashMap<>();
			this.goalsToFactLandmarks = new HashMap<>();
			this.goalsToLandmarks = new HashMap<>();
		} catch (IOException | InterruptedException e){
			e.printStackTrace();
		}
	}
	
	public Recognizer(String domainFile, String problemFile, String candidateGoalsFile, String observationsFile, String correctGoalFile, float threshold){
		try{
			String domainFilePath = "domain.pddl";
			Path path = Paths.get(domainFile);
			String domainContent = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
			domainContent = domainContent.replace("(increase (total-cost) 1)", "");
			File domain = new File("domain.pddl");
			FileWriter fw = new FileWriter(domain.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(domainContent);
			bw.close();
			
			String initialFilePath = "templateInitial.pddl";
			path = Paths.get(problemFile);
			String initialContent = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
			initialContent = initialContent.replace("<HYPOTHESIS>", "");
			File templateInitial = new File("templateInitial.pddl");
			fw = new FileWriter(templateInitial.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			bw.write(initialContent);
			bw.close();
			
			this.threshold = threshold;
			this.groundProblem = PDDLParser.getGroundDomainProblem(domainFilePath, initialFilePath);
			this.observations = PDDLParser.getObservations(groundProblem, observationsFile);
			this.goals = PDDLParser.getGoals(groundProblem, candidateGoalsFile);
			this.realGoal = PDDLParser.getGoals(groundProblem, correctGoalFile).get(0);
			this.initialState = groundProblem.getSTRIPSInitialState();
			this.goalsToLandmarkExtractor = new HashMap<>();
			this.goalsToFactLandmarks = new HashMap<>();
			this.goalsToLandmarks = new HashMap<>();
		} catch (IOException e){
			e.printStackTrace();
		}	
	}
	
	public Map<GroundFact, Set<Set<Fact>>> getAchievedLandmarksForAllGoals(){
		Map<GroundFact, Set<Set<Fact>>> goalsToAchievedLandmarks = new HashMap<>();
		
		System.out.println("$$$> Computing achieved landmarks from observations for all candidate goals: \n");
		System.out.println("# Initial state: " + this.initialState + "\n");
		System.out.println("# Observations: ");
		STRIPSState currentState = this.initialState;
		for(Action o: this.observations)
			System.out.println("\t>$ " + o);
		
		for(GroundFact goal: this.goals){
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
			
			Set<Set<Fact>> achievedLandmarks = new HashSet<>();
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
						if(observedFacts.containsAll(factsOrdering) && !achievedLandmarks.contains(factsOrdering)){
							achievedLandmarks.add(factsOrdering);
							Set<Set<Fact>> inferredFacts = this.inferFactLandmarks(landmarkOrdering, observedFacts, achievedLandmarks);
							achievedLandmarks.addAll(inferredFacts);
						}
					}
				}
				currentState = (STRIPSState) currentState.apply(o);
			}
			goalsToAchievedLandmarks.put(goal, achievedLandmarks);
			System.out.println("\n\t># Achieved Landmarks in Observations: \n\t\t" + achievedLandmarks);
		}
		return goalsToAchievedLandmarks;
	}
	
	protected Set<Set<Fact>> inferFactLandmarks(LandmarkOrdering landmarkOrdering, Set<Fact> observedFacts, Set<Set<Fact>> observedLandmarks){
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
	
	/**
	 * This method returns true if the correct goal was recognized in the set of candidate goals from the observations.
	 * 
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public boolean recognize() throws IOException, InterruptedException{
		return false;
	}
}