package filter;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Set;

import org.junit.Test;

import javaff.data.GroundFact;

public class GoalRecognitionFilterTest {

	@Test
	public void testFilterSeparatedFilesLODigital(){
		GoalRecognitionFilter filter = new GoalRecognitionFilter(
				"experiments/factobs/lodigital/pb05_lodigital_out_100/domain.pddl", 
				"experiments/factobs/lodigital/pb05_lodigital_out_100/template.pddl", 
				"experiments/factobs/lodigital/pb05_lodigital_out_100/hyps.dat", 
				"experiments/factobs/lodigital/pb05_lodigital_out_100/obs.dat", 
				"experiments/factobs/lodigital/pb05_lodigital_out_100/real_hyp.dat", 0);
		try {
			Set<GroundFact> filteredGoals = filter.filter(false);
			assertEquals(3, filteredGoals.size());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFilterCompactedFileLODigital(){
		GoalRecognitionFilter filter = new GoalRecognitionFilter("experiments/factobs/lodigital/pb05_lodigital_out_10.tar.bz2", 0f);
		try {
			Set<GroundFact> filteredGoals = filter.filter(true);
			assertEquals(1, filteredGoals.size());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
