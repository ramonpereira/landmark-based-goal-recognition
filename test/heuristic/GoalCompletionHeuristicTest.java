package heuristic;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class GoalCompletionHeuristicTest {

	@Test
	public void testGoalCompletionHeuristicBlocksWorldSeparatedFiles(){
		GoalCompletionHeuristic gcHeuristic = new GoalCompletionHeuristic(
				"experiments/blocks-test/domain.pddl", 
				"experiments/blocks-test/template.pddl", 
				"experiments/blocks-test/hyps.dat", 
				"experiments/blocks-test/obs.dat", 
				"experiments/blocks-test/real_hyp.dat", 0);
		try {
			boolean recognized = gcHeuristic.recognize();
			assertEquals(true, recognized);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGoalCompletionHeuristicBlocksWorldCompactedFile(){
		GoalCompletionHeuristic gcHeuristic = new GoalCompletionHeuristic("experiments/blocks-test/blocks-test.tar.bz2", 0);
		try {
			boolean recognized = gcHeuristic.recognize();
			assertEquals(true, recognized);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGoalCompletionHeuristic(){
		GoalCompletionHeuristic gcHeuristic = new GoalCompletionHeuristic("experiments/mnist/puzzle_mnist_01.tar.bz2", 0);
		try {
			boolean recognized = gcHeuristic.recognize();
			assertEquals(true, recognized);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}