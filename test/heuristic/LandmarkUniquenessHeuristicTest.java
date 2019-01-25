package heuristic;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class LandmarkUniquenessHeuristicTest {

	@Test
	public void testLandmarkUniquenessHeuristicLOTwistedSeparatedFiles(){
		LandmarkUniquenessHeuristic uniqHeuristic = new LandmarkUniquenessHeuristic(
				"experiments/factobs/lotwisted/pb06_lotwisted_out_70/domain.pddl", 
				"experiments/factobs/lotwisted/pb06_lotwisted_out_70/template.pddl", 
				"experiments/factobs/lotwisted/pb06_lotwisted_out_70/hyps.dat", 
				"experiments/factobs/lotwisted/pb06_lotwisted_out_70/obs.dat", 
				"experiments/factobs/lotwisted/pb06_lotwisted_out_70/real_hyp.dat", 0.05f);
		try {
			boolean recognized = uniqHeuristic.recognize();
			assertEquals(true, recognized);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testLandmarkUniquenessHeuristicLODigitalSeparatedFiles(){
		LandmarkUniquenessHeuristic uniqHeuristic = new LandmarkUniquenessHeuristic(
				"experiments/factobs/lodigital/pb06_lodigital_out_100/domain.pddl", 
				"experiments/factobs/lodigital/pb06_lodigital_out_100/template.pddl", 
				"experiments/factobs/lodigital/pb06_lodigital_out_100/hyps.dat", 
				"experiments/factobs/lodigital/pb06_lodigital_out_100/obs.dat", 
				"experiments/factobs/lodigital/pb06_lodigital_out_100/real_hyp.dat", 0.0f);
		try {
			boolean recognized = uniqHeuristic.recognize();
			assertEquals(true, recognized);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testLandmarkUniquenessHeuristicCompactedFileSpider(){
		LandmarkUniquenessHeuristic uniqHeuristic = new LandmarkUniquenessHeuristic("experiments/factobs/spider/pb04_spider_out_30.tar.bz2", 0f);
		try {
			boolean recognized = uniqHeuristic.recognize();
			assertEquals(false, recognized);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testLandmarkUniquenessHeuristic(){
		LandmarkUniquenessHeuristic uniqHeuristic = new LandmarkUniquenessHeuristic("experiments/factobs/mandrill/pb04_mandrill_out_50.tar.bz2", 0.1f);
		try {
			boolean recognized = uniqHeuristic.recognize();
			assertEquals(true, recognized);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
