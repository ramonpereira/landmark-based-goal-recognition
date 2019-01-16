package heuristic;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class GoalCompletionHeuristicTest {

	@Test
	public void testGoalCompletionHeuristicSeparatedFilesHanoi(){
		GoalCompletionHeuristic gcHeuristic = new GoalCompletionHeuristic(
				"experiments/factobs/hanoi/pb01_hanoi_out_10/domain.pddl", 
				"experiments/factobs/hanoi/pb01_hanoi_out_10/template.pddl", 
				"experiments/factobs/hanoi/pb01_hanoi_out_10/hyps.dat", 
				"experiments/factobs/hanoi/pb01_hanoi_out_10/obs.dat", 
				"experiments/factobs/hanoi/pb01_hanoi_out_10/real_hyp.dat", 0);
		try {
			boolean recognized = gcHeuristic.recognize();
			assertEquals(false, recognized);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGoalCompletionHeuristicCompactedFileHanoi(){
		GoalCompletionHeuristic gcHeuristic = new GoalCompletionHeuristic("experiments/factobs/hanoi/pb01_hanoi_out_100.tar.bz2", 0f);;
		try {
			boolean recognized = gcHeuristic.recognize();
			assertEquals(true, recognized);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}