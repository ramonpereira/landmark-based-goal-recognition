package filter;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Set;

import org.junit.Test;

import javaff.data.GroundFact;

public class GoalRecognitionFilterTest {

	@Test
	public void testFilterBlocksWorldSeparatedFiles(){
		GoalRecognitionFilter filter = new GoalRecognitionFilter(
				"experiments/blocks-test/domain.pddl", 
				"experiments/blocks-test/template.pddl", 
				"experiments/blocks-test/hyps.dat", 
				"experiments/blocks-test/obs.dat", 
				"experiments/blocks-test/real_hyp.dat", 0);
		try {
			Set<GroundFact> filteredGoals = filter.filter(false);
			assertEquals(1, filteredGoals.size());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFilterBlocksWorldCompactedFile(){
		GoalRecognitionFilter filter = new GoalRecognitionFilter("experiments/blocks-test/block-words_p03_hyp-10_30_0.tar.bz2", 0.1f);
		try {
			Set<GroundFact> filteredGoals = filter.filter(true);
			assertEquals(1, filteredGoals.size());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
