package recognizer;

import java.io.IOException;

import filter.GoalRecognitionFilter;
import heuristic.GoalCompletionHeuristic;
import heuristic.LandmarkUniquenessHeuristic;

public class GoalRecognizer {

	public static void main(String[] args) {
		try {
			if (args.length < 3 || args.length > 7){
				printUsage();
			} else if(args.length == 3){
				String goalRecognitionFile = args[1];
				float threshold = Float.valueOf(args[2]);
				if(args[0] == "filter"){
					GoalRecognitionFilter filter = new GoalRecognitionFilter(goalRecognitionFile, threshold);
					filter.filter(false);
				} else if(args[0] == "goalcompletion"){
					GoalCompletionHeuristic gc = new GoalCompletionHeuristic(goalRecognitionFile, threshold);
					gc.recognize();
				} else if(args[0] == "uniqueness"){
					LandmarkUniquenessHeuristic uniq = new LandmarkUniquenessHeuristic(goalRecognitionFile, threshold);
					uniq.recognize();
				} else printUsage();
			} else if(args.length == 7){
				String domain = args[1];
				String problem = args[2];
				String goals = args[3];
				String observations = args[4];
				String correctGoal = args[5];
				float threshold = Float.valueOf(args[6]);
				if(args[0] == "filter"){
					GoalRecognitionFilter filter = new GoalRecognitionFilter(domain, problem, goals, observations, correctGoal, threshold);
					filter.filter(false);
				} else if(args[0] == "goalcompletion"){
					GoalCompletionHeuristic gc = new GoalCompletionHeuristic(domain, problem, goals, observations, correctGoal, threshold);
					gc.recognize();
				} else if(args[0] == "uniqueness"){
					LandmarkUniquenessHeuristic uniq = new LandmarkUniquenessHeuristic(domain, problem, goals, observations, correctGoal, threshold);
					uniq.recognize();
				} else printUsage();
			} else printUsage();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void printUsage(){
		System.out.println("- Option (1) - Parameters needed: <filter | goalcompletion | uniqueness> <tar.bz2 file> <threshold_value>");
		System.out.println("\t $> Example: filter goal_recognition-problem.tar.bz2 0.1");
		System.out.println("\nor\n");
		System.out.println("- Option (2) - Parameters needed: <filter | goalcompletion | uniqueness> <domain.pddl> <problem.pddl> <goals.dat> <observations.dat> <correct_goal.dat> <threshold_value>");
		System.out.println("\t $> Example: goalcompletion domain.pddl template.pddl hyps.dat obs.dat real_hyp.dat 0.15");
	}
}
