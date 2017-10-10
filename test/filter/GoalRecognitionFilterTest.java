package filter;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import javaff.data.Fact;
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
		GoalRecognitionFilter filter = new GoalRecognitionFilter("experiments/blocks-test/blocks-test.tar.bz2", 0);
		try {
			Set<GroundFact> filteredGoals = filter.filter(false);
			assertEquals(1, filteredGoals.size());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
