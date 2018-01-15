package heuristic;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class LandmarkUniquenessHeuristicTest {

	@Test
	public void testLandmarkUniquenessHeuristicBlocksWorldSeparatedFiles(){
		LandmarkUniquenessHeuristic uniqHeuristic = new LandmarkUniquenessHeuristic(
				"experiments/blocks-test/domain.pddl", 
				"experiments/blocks-test/template.pddl", 
				"experiments/blocks-test/hyps.dat", 
				"experiments/blocks-test/obs.dat", 
				"experiments/blocks-test/real_hyp.dat", 0);
		try {
			boolean recognized = uniqHeuristic.recognize();
			assertEquals(true, recognized);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testLandmarkUniquenessHeuristicBlocksWorldCompactedFile(){
		LandmarkUniquenessHeuristic uniqHeuristic = new LandmarkUniquenessHeuristic("experiments/blocks-test/blocks-test.tar.bz2", 0);
		try {
			boolean recognized = uniqHeuristic.recognize();
			assertEquals(true, recognized);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testLandmarkUniquenessHeuristic(){
		LandmarkUniquenessHeuristic uniqHeuristic = new LandmarkUniquenessHeuristic("experiments/hanoi/pb01_hanoi_out_10.tar.bz2", 0);
		try {
			boolean recognized = uniqHeuristic.recognize();
			assertEquals(true, recognized);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
