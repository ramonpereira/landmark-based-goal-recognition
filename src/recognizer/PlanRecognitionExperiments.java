package recognizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PlanRecognitionExperiments {

	public static void main(String[] args) {
		//args = new String[]{"blocks", "heuristic-completion", "0.1"};
		try {
			switch(args[0]){
				case ("blocks"):
					if(args[1].equals("filter")){
						doBlocksFilter(new Float(args[2]));
					} else if (args[1].equals("heuristic-completion")){
						doBlocksWorldExperiments(new Float(args[2]), PlanRecognizer.Heuristic.COMPLETION);
					} else if(args[1].equals("heuristic-uniqueness"))
						doBlocksWorldExperiments(new Float(args[2]), PlanRecognizer.Heuristic.UNIQUENESS);
					break;
					
				case ("campus"):
					if(args[1].equals("filter")){
						doCampusFilter(new Float(args[2]));
					} else if (args[1].equals("heuristic-completion")){
						doCampusExperiments(new Float(args[2]), PlanRecognizer.Heuristic.COMPLETION);
					} else if(args[1].equals("heuristic-uniqueness"))
						doCampusExperiments(new Float(args[2]), PlanRecognizer.Heuristic.UNIQUENESS);
					break;
					
				case ("grid"):
					if(args[1].equals("filter")){
						doEasyIPCGridFilter(new Float(args[2]));
					} else if (args[1].equals("heuristic-completion")){
						doEasyIPCGridExperiments(new Float(args[2]), PlanRecognizer.Heuristic.COMPLETION);
					} else if(args[1].equals("heuristic-uniqueness"))
						doEasyIPCGridExperiments(new Float(args[2]), PlanRecognizer.Heuristic.UNIQUENESS);
					break;
					
				case ("intrusion"):
					if(args[1].equals("filter")){
						doIntrusionDetectionFilter(new Float(args[2]));
					} else if (args[1].equals("heuristic-completion")){
						doIntrusionDetectionExperiments(new Float(args[2]), PlanRecognizer.Heuristic.COMPLETION);
					} else if(args[1].equals("heuristic-uniqueness"))
						doIntrusionDetectionExperiments(new Float(args[2]), PlanRecognizer.Heuristic.UNIQUENESS);
					break;
					
				case ("kitchen"):
					if(args[1].equals("filter")){
						doKitchenFilter(new Float(args[2]));
					} else if (args[1].equals("heuristic-completion")){
						doKitchenExperiments(new Float(args[2]), PlanRecognizer.Heuristic.COMPLETION);
					} else if(args[1].equals("heuristic-uniqueness"))
						doKitchenExperiments(new Float(args[2]), PlanRecognizer.Heuristic.UNIQUENESS);
					break;	
					
				case ("logistics"):
					if(args[1].equals("filter")){
						doLogisticsFilter(new Float(args[2]));
					} else if (args[1].equals("heuristic-completion")){
						doLogisticsExperiments(new Float(args[2]), PlanRecognizer.Heuristic.COMPLETION);
					} else if(args[1].equals("heuristic-uniqueness"))
						doLogisticsExperiments(new Float(args[2]), PlanRecognizer.Heuristic.UNIQUENESS);
					break;
				
				case ("depots"):
				case ("driverlog"):
				case ("dwr"):
				case ("ferry"):
				case ("miconic"):
				case ("rovers"):
				case ("satellite"):
				case ("sokoban"):
				case ("zeno-travel"):
					if(args[1].equals("filter")){
						doFilter(args[0], new Float(args[2]));
					} else if (args[1].equals("heuristic-completion")){
						doExperiments(args[0], new Float(args[2]), PlanRecognizer.Heuristic.COMPLETION);
					} else if(args[1].equals("heuristic-uniqueness"))
						doExperiments(args[0], new Float(args[2]), PlanRecognizer.Heuristic.UNIQUENESS);
					break;
				default:
					System.out.println("Ooopppsss.");
					System.out.println("\n$> [domainName] [threshold] [recognize OR filter] [observability (for filter)]");
					break;
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void doBlocksFilter(float threshold) throws IOException, InterruptedException{
		for(int obs=10;obs<=100;obs+=20){
			String observability = String.valueOf(obs);
			if(obs > 70)
				observability = "full";
			boolean full = observability.equals("full");
			if(full) observability = "_full";
			else observability = "_" + observability +  "_";
			for(int problem=1;problem<=3;problem++){
				for(int problemHyp=0;problemHyp<=4;problemHyp++){
					for(int problemObs=0;problemObs<=0;problemObs++){
						String inputPRproblem = "experiments/blocks-world/block-words-aaai_p0" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						planRecognizer.newFilter(true);
						if(full) break;
					}
				}
			}
			for(int problem=1;problem<=3;problem++){
				for(int problemHyp=0;problemHyp<=19;problemHyp++){
					for(int problemObs=0;problemObs<=2;problemObs++){
						String inputPRproblem = "experiments/blocks-world/block-words_p0" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						planRecognizer.newFilter(true);
						if(full) break;
					}
				}
			}
			for(int problem=4;problem<=7;problem++){
				for(int problemHyp=1;problemHyp<=4;problemHyp++){
					for(int problemObs=1;problemObs<=3;problemObs++){
						String inputPRproblem = "experiments/blocks-world/block-words_p0" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						planRecognizer.newFilter(true);
						if(full) break;
					}
				}
			}
		}
	}
	
	private static void doCampusFilter(float threshold) throws IOException, InterruptedException{
		for(int obs=10;obs<=100;obs+=20){
			String observability = String.valueOf(obs);
			if(obs > 70)
				observability = "full";
			boolean full = observability.equals("full");
			if(full) observability = "_full";
			else observability = "_" + observability +  "_";
			for(int problem=1;problem<=3;problem++){
				for(int problemHyp=0;problemHyp<=19;problemHyp++){
					for(int problemObs=0;problemObs<=2;problemObs++){
						String inputPRproblem = "blocks/block-words_p0" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						planRecognizer.newFilter(true);
						if(full) break;
					}
				}
			}
		}
	}
	
	private static void doEasyIPCGridFilter(float threshold) throws IOException, InterruptedException{
		for(int obs=10;obs<=100;obs+=20){
			String observability = String.valueOf(obs);
			if(obs > 70)
				observability = "full";
			boolean full = observability.equals("full");
			if(full) observability = "_full";
			else observability = "_" + observability +  "_";
		
			for(int problem=1;problem<=1;problem++){
				for(int problemHyp=0;problemHyp<=4;problemHyp++){
					for(int problemObs=0;problemObs<=0;problemObs++){
						String inputPRproblem = "experiments/easy-ipc-grid/easy-ipc-grid-aaai_p5-5-5" + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						planRecognizer.newFilter(true);
						if(full) break;
					}
				}
			}
			for(int problem=1;problem<=1;problem++){
				for(int problemHyp=0;problemHyp<=4;problemHyp++){
					for(int problemObs=0;problemObs<=0;problemObs++){
						String inputPRproblem = "experiments/easy-ipc-grid/easy-ipc-grid-aaai_p5-10-10" + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						planRecognizer.newFilter(true);
						if(full) break;
					}
				}
			}
			for(int problem=1;problem<=1;problem++){
				for(int problemHyp=0;problemHyp<=4;problemHyp++){
					for(int problemObs=0;problemObs<=0;problemObs++){
						String inputPRproblem = "experiments/easy-ipc-grid/easy-ipc-grid-aaai_p10-5-5" + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						planRecognizer.newFilter(true);
						if(full) break;
					}
				}
			}
			for(int problem=1;problem<=1;problem++){
				for(int problemHyp=0;problemHyp<=4;problemHyp++){
					for(int problemObs=0;problemObs<=2;problemObs++){
						String inputPRproblem = "experiments/easy-ipc-grid/easy-ipc-grid_p5-5-5" + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						planRecognizer.newFilter(true);
						if(full) break;
					}
				}
			}
			for(int problem=1;problem<=1;problem++){
				for(int problemHyp=0;problemHyp<=9;problemHyp++){
					for(int problemObs=0;problemObs<=2;problemObs++){
						String inputPRproblem = "experiments/easy-ipc-grid/easy-ipc-grid_p5-10-10" + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						planRecognizer.newFilter(true);
						if(full) break;
					}
				}
			}
			for(int problem=1;problem<=1;problem++){
				for(int problemHyp=0;problemHyp<=4;problemHyp++){
					for(int problemObs=0;problemObs<=2;problemObs++){
						String inputPRproblem = "experiments/easy-ipc-grid/easy-ipc-grid_p10-5-5" + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						planRecognizer.newFilter(true);
						if(full) break;
					}
				}
			}
			for(int problem=1;problem<=1;problem++){
				for(int problemHyp=0;problemHyp<=9;problemHyp++){
					for(int problemObs=0;problemObs<=2;problemObs++){
						String inputPRproblem = "experiments/easy-ipc-grid/easy-ipc-grid_p10-10-10" + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						planRecognizer.newFilter(true);
						if(full) break;
					}
				}
			}
			for(int problem=4;problem<=7;problem++){
				for(int problemHyp=1;problemHyp<=4;problemHyp++){
					for(int problemObs=1;problemObs<=3;problemObs++){
						String inputPRproblem = "experiments/easy-ipc-grid/easy-ipc-grid_p0" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						planRecognizer.newFilter(true);
						if(full) break;
					}
				}
			}
		}
	}
	
	private static void doIntrusionDetectionFilter(float threshold) throws IOException, InterruptedException{
		for(int obs=10;obs<=100;obs+=20){
			String observability = String.valueOf(obs);
			if(obs > 70)
				observability = "full";
			boolean full = observability.equals("full");
			if(full) observability = "_full";
			else observability = "_" + observability +  "_";
		
			for(int problem=10;problem<=10;problem++){
				for(int problemHyp=0;problemHyp<=4;problemHyp++){
					for(int problemObs=0;problemObs<=0;problemObs++){
						String inputPRproblem = "experiments/intrusion-detection/intrusion-detection-aaai_p" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						planRecognizer.newFilter(true);
						if(full) break;
					}
				}
			}
			for(int problem=20;problem<=20;problem++){
				for(int problemHyp=0;problemHyp<=9;problemHyp++){
					for(int problemObs=0;problemObs<=0;problemObs++){
						String inputPRproblem = "experiments/intrusion-detection/intrusion-detection-aaai_p" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						planRecognizer.newFilter(true);
						if(full) break;
					}
				}
			}			
			for(int problem=10;problem<=10;problem+=10){
				for(int problemHyp=0;problemHyp<=9;problemHyp++){
					for(int problemObs=0;problemObs<=2;problemObs++){
						String inputPRproblem = "experiments/intrusion-detection/intrusion-detection_p" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						planRecognizer.newFilter(true);
						if(full) break;
					}
				}
			}
			for(int problem=20;problem<=20;problem+=10){
				for(int problemHyp=0;problemHyp<=19;problemHyp++){
					for(int problemObs=0;problemObs<=2;problemObs++){
						String inputPRproblem = "experiments/intrusion-detection/intrusion-detection_p" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						planRecognizer.newFilter(true);
						if(full) break;
					}
				}
			}
		}
	}

	private static void doKitchenFilter(float threshold) throws IOException, InterruptedException{
		for(int obs=10;obs<=100;obs+=20){
			String observability = String.valueOf(obs);
			if(obs > 70)
				observability = "full";
			boolean full = observability.equals("full");
			if(full) observability = "_full";
			else observability = "_" + observability +  "_";
		
			for(int problem=1;problem<=3;problem++){
				for(int problemHyp=0;problemHyp<=9;problemHyp++){
					for(int problemObs=0;problemObs<=2;problemObs++){
						String inputPRproblem = "logistics/logistics_p0" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						planRecognizer.newFilter(true);
						if(full) break;
					}
				}
			}
		}
	}
	
	private static void doLogisticsFilter(float threshold) throws IOException, InterruptedException{
		for(int obs=10;obs<=100;obs+=20){
			String observability = String.valueOf(obs);
			if(obs > 70)
				observability = "full";
			boolean full = observability.equals("full");
			if(full) observability = "_full";
			else observability = "_" + observability +  "_";
		
			for(int problem=1;problem<=3;problem++){
				for(int problemHyp=0;problemHyp<=4;problemHyp++){
					for(int problemObs=0;problemObs<=0;problemObs++){
						String inputPRproblem = "experiments/logistics/logistics-aaai_p0" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						planRecognizer.newFilter(true);
						if(full) break;
					}
				}
			}
			for(int problem=1;problem<=3;problem++){
				for(int problemHyp=0;problemHyp<=9;problemHyp++){
					for(int problemObs=0;problemObs<=2;problemObs++){
						String inputPRproblem = "experiments/logistics/logistics_p0" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						planRecognizer.newFilter(true);
						if(full) break;
					}
				}
			}
			for(int problem=4;problem<=7;problem++){
				for(int problemHyp=1;problemHyp<=4;problemHyp++){
					for(int problemObs=1;problemObs<=3;problemObs++){
						String inputPRproblem = "experiments/logistics/logistics_p0" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						planRecognizer.newFilter(true);
						if(full) break;
					}
				}
			}
		}
	}
	
	private static void doBlocksWorldExperiments(float threshold, PlanRecognizer.Heuristic heuristic) throws IOException, InterruptedException{
		System.out.println("$> Plan Recognition - BlocksWorld");
		String contentFile = "Obs % \tAccuracy \tPrecision \tRecall \tF1-score \tFall-out \tMiss-rate \tRecognized Goals \tTime(s) \n";
		float totalProblems = 0;
		for(int obs=10;obs<=100;obs+=20){
			long initialTime = System.currentTimeMillis();
			String observability = String.valueOf(obs);
			if(obs > 70)
				observability = "full";
			boolean full = observability.equals("full");
			if(full) observability = "_full";
			else observability = "_" + observability +  "_";
			
			int returnedGoalsCounter = 0;
			float totalPlanRecognitionProblems = 0;
			float truePositiveCounter = 0;
			int candidateGoals = 0;
			for(int problem=1;problem<=3;problem++){
				for(int problemHyp=0;problemHyp<=4;problemHyp++){
					for(int problemObs=0;problemObs<=0;problemObs++){
						String inputPRproblem = "experiments/blocks-world/block-words-aaai_p0" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						totalPlanRecognitionProblems++;
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						
						boolean goaolWasRecognized = false;
						if(heuristic == PlanRecognizer.Heuristic.COMPLETION)
							goaolWasRecognized = planRecognizer.recognize();
						else if (heuristic == PlanRecognizer.Heuristic.UNIQUENESS)
							goaolWasRecognized = planRecognizer.recognizeUsingLandmarksUniqueness();
						
						candidateGoals = candidateGoals + planRecognizer.getAmountOfCandidateGoals();
						returnedGoalsCounter = returnedGoalsCounter + planRecognizer.getAmountOfRecognizedGoals();
						if(goaolWasRecognized)
							truePositiveCounter++;
						
						if(full) break;
					}
				}
			}
			for(int problem=1;problem<=3;problem++){
				for(int problemHyp=0;problemHyp<=19;problemHyp++){
					for(int problemObs=0;problemObs<=2;problemObs++){
						String inputPRproblem = "experiments/blocks-world/block-words_p0" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						totalPlanRecognitionProblems++;
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						
						boolean goaolWasRecognized = false;
						if(heuristic == PlanRecognizer.Heuristic.COMPLETION)
							goaolWasRecognized = planRecognizer.recognize();
						else if (heuristic == PlanRecognizer.Heuristic.UNIQUENESS)
							goaolWasRecognized = planRecognizer.recognizeUsingLandmarksUniqueness();
						
						candidateGoals = candidateGoals + planRecognizer.getAmountOfCandidateGoals();
						returnedGoalsCounter = returnedGoalsCounter + planRecognizer.getAmountOfRecognizedGoals();
						if(goaolWasRecognized)
							truePositiveCounter++;
						
						if(full) break;
					}
				}
			}
			for(int problem=4;problem<=7;problem++){
				for(int problemHyp=1;problemHyp<=4;problemHyp++){
					for(int problemObs=1;problemObs<=3;problemObs++){
						String inputPRproblem = "experiments/blocks-world/block-words_p0" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						totalPlanRecognitionProblems++;
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						
						boolean goaolWasRecognized = false;
						if(heuristic == PlanRecognizer.Heuristic.COMPLETION)
							goaolWasRecognized = planRecognizer.recognize();
						else if (heuristic == PlanRecognizer.Heuristic.UNIQUENESS)
							goaolWasRecognized = planRecognizer.recognizeUsingLandmarksUniqueness();
						
						candidateGoals = candidateGoals + planRecognizer.getAmountOfCandidateGoals();
						returnedGoalsCounter = returnedGoalsCounter + planRecognizer.getAmountOfRecognizedGoals();
						if(goaolWasRecognized)
							truePositiveCounter++;
						
						if(full) break;
					}
				}
			}
			float averageCandidateGoals = (candidateGoals / totalPlanRecognitionProblems);
			float falsePositiveCounter = (returnedGoalsCounter - truePositiveCounter);
			float falseNegativeCounter = (totalPlanRecognitionProblems - truePositiveCounter);
			float trueNegativeCounter = ((totalPlanRecognitionProblems*averageCandidateGoals) - falsePositiveCounter);
			float accuracy = truePositiveCounter / totalPlanRecognitionProblems;
			float precision = truePositiveCounter / (truePositiveCounter + falsePositiveCounter);
			float recall = truePositiveCounter / (truePositiveCounter + falseNegativeCounter);
			float f1score = 2 * ( (precision*recall) / (precision+recall));
			float fallout = falsePositiveCounter / (falsePositiveCounter + trueNegativeCounter);
			float missrate = falseNegativeCounter / (falseNegativeCounter + truePositiveCounter);
			float avgRecognizedGoals = (returnedGoalsCounter/totalPlanRecognitionProblems);
			long finalTime = System.currentTimeMillis();
			float totalTime = ((finalTime - initialTime)/1000);
			contentFile += (obs > 70 ? "100" : obs) + "\t" + (accuracy) + "\t" + (precision) + "\t" + (recall) + "\t" + (f1score) + "\t" + (fallout) + "\t" + (missrate) + "\t" + (avgRecognizedGoals) + "\t" + (totalTime / totalPlanRecognitionProblems) + "\n";
			totalProblems = totalProblems + totalPlanRecognitionProblems;
		}
		contentFile += "\n$> Total Problems: " + totalProblems;
		System.out.println("###################################################################################\n");
		System.out.println(contentFile);
		System.out.println("###################################################################################");
		writeExperimentFile(contentFile,  "blocks-planrecognition-heuristic_" + heuristic.toString().toLowerCase() + "-" + (int) (threshold * 100));
	}
	
	private static void doCampusExperiments(float threshold, PlanRecognizer.Heuristic heuristic) throws IOException, InterruptedException{
		System.out.println("$> Plan Recognition - Campus");
		String contentFile = "Obs % \tAccuracy \tPrecision \tRecall \tF1-score \tFall-out \tMiss-rate \tRecognized Goals \tTime(s) \n";
		float totalProblems = 0;
		for(int obs=10;obs<=100;obs+=20){
			long initialTime = System.currentTimeMillis();
			String observability = String.valueOf(obs);
			if(obs > 70)
				observability = "full";
			boolean full = observability.equals("full");
			if(full) observability = "_full_";
			else observability = "_" + observability +  "_";

			int problemBegin = 0;
			int problemEnd = 0;
			switch (obs){
				case (10): 
					problemBegin = 1;
					problemEnd = 15;
					break;
				case (30): 
					problemBegin = 16;
					problemEnd = 30;
					break;
				case (50): 
					problemBegin = 31;
					problemEnd = 45;
					break;
				case (70): 
					problemBegin = 46;
					problemEnd = 60;
					break;
				case (90): 
					problemBegin = 61;
					problemEnd = 75;
					break;		
			}
			int returnedGoalsCounter = 0;
			float totalPlanRecognitionProblems = 0;
			float truePositiveCounter = 0;
			int candidateGoals = 0;
			for(int problemHyp=0;problemHyp<=0;problemHyp++){
				for(int problem=problemBegin;problem<=problemEnd;problem++){
					String inputPRproblem = "experiments/campus/bui-campus_generic_hyp-" + problemHyp + observability + problem + ".tar.bz2";
					totalPlanRecognitionProblems++;
					PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
					
					boolean goaolWasRecognized = false;
					if(heuristic == PlanRecognizer.Heuristic.COMPLETION)
						goaolWasRecognized = planRecognizer.recognize();
					else if (heuristic == PlanRecognizer.Heuristic.UNIQUENESS)
						goaolWasRecognized = planRecognizer.recognizeUsingLandmarksUniqueness();
					
					candidateGoals = candidateGoals + planRecognizer.getAmountOfCandidateGoals();
					returnedGoalsCounter = returnedGoalsCounter + planRecognizer.getAmountOfRecognizedGoals();
					if(goaolWasRecognized)
						truePositiveCounter++;
					
					if(full) break;
				}
			}
			float averageCandidateGoals = (candidateGoals / totalPlanRecognitionProblems);
			float falsePositiveCounter = (returnedGoalsCounter - truePositiveCounter);
			float falseNegativeCounter = (totalPlanRecognitionProblems - truePositiveCounter);
			float trueNegativeCounter = ((totalPlanRecognitionProblems*averageCandidateGoals) - falsePositiveCounter);
			float accuracy = truePositiveCounter / totalPlanRecognitionProblems;
			float precision = truePositiveCounter / (truePositiveCounter + falsePositiveCounter);
			float recall = truePositiveCounter / (truePositiveCounter + falseNegativeCounter);
			float f1score = 2 * ( (precision*recall) / (precision+recall));
			float fallout = falsePositiveCounter / (falsePositiveCounter + trueNegativeCounter);
			float missrate = falseNegativeCounter / (falseNegativeCounter + truePositiveCounter);
			float avgRecognizedGoals = (returnedGoalsCounter/totalPlanRecognitionProblems);
			long finalTime = System.currentTimeMillis();
			float totalTime = ((finalTime - initialTime)/1000);
			contentFile += (obs > 70 ? "100" : obs) + "\t" + (accuracy) + "\t" + (precision) + "\t" + (recall) + "\t" + (f1score) + "\t" + (fallout) + "\t" + (missrate) + "\t" + (avgRecognizedGoals) + "\t" + (totalTime / totalPlanRecognitionProblems) + "\n";
			totalProblems = totalProblems + totalPlanRecognitionProblems;
		}
		contentFile += "\n$> Total Problems: " + totalProblems;
		System.out.println("###################################################################################\n");
		System.out.println(contentFile);
		System.out.println("###################################################################################");
		writeExperimentFile(contentFile,  "campus-planrecognition-heuristic_" + heuristic.toString().toLowerCase() + "-" + (int) (threshold * 100));
	}
	
	private static void doEasyIPCGridExperiments(float threshold, PlanRecognizer.Heuristic heuristic) throws IOException, InterruptedException{
		System.out.println("$> Plan Recognition - Easy IPC Grid");
		String contentFile = "Obs % \tAccuracy \tPrecision \tRecall \tF1-score \tFall-out \tMiss-rate \tRecognized Goals \tTime(s) \n";
		float totalProblems = 0;
		for(int obs=10;obs<=100;obs+=20){
			long initialTime = System.currentTimeMillis();
			String observability = String.valueOf(obs);
			if(obs > 70)
				observability = "full";
			boolean full = observability.equals("full");
			if(full) observability = "_full";
			else observability = "_" + observability +  "_";
			
			int returnedGoalsCounter = 0;
			float totalPlanRecognitionProblems = 0;
			float truePositiveCounter = 0;
			int candidateGoals = 0;
			
			for(int problem=1;problem<=1;problem++){
				for(int problemHyp=0;problemHyp<=4;problemHyp++){
					for(int problemObs=0;problemObs<=0;problemObs++){
						String inputPRproblem = "experiments/easy-ipc-grid/easy-ipc-grid-aaai_p5-5-5" + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						totalPlanRecognitionProblems++;
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						
						boolean goaolWasRecognized = false;
						if(heuristic == PlanRecognizer.Heuristic.COMPLETION)
							goaolWasRecognized = planRecognizer.recognize();
						else if (heuristic == PlanRecognizer.Heuristic.UNIQUENESS)
							goaolWasRecognized = planRecognizer.recognizeUsingLandmarksUniqueness();
						
						candidateGoals = candidateGoals + planRecognizer.getAmountOfCandidateGoals();
						returnedGoalsCounter = returnedGoalsCounter + planRecognizer.getAmountOfRecognizedGoals();
						if(goaolWasRecognized)
							truePositiveCounter++;
						
						if(full) break;
					}
				}
			}
			for(int problem=1;problem<=1;problem++){
				for(int problemHyp=0;problemHyp<=4;problemHyp++){
					for(int problemObs=0;problemObs<=0;problemObs++){
						String inputPRproblem = "experiments/easy-ipc-grid/easy-ipc-grid-aaai_p5-10-10" + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						totalPlanRecognitionProblems++;
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						
						boolean goaolWasRecognized = false;
						if(heuristic == PlanRecognizer.Heuristic.COMPLETION)
							goaolWasRecognized = planRecognizer.recognize();
						else if (heuristic == PlanRecognizer.Heuristic.UNIQUENESS)
							goaolWasRecognized = planRecognizer.recognizeUsingLandmarksUniqueness();
						
						candidateGoals = candidateGoals + planRecognizer.getAmountOfCandidateGoals();
						returnedGoalsCounter = returnedGoalsCounter + planRecognizer.getAmountOfRecognizedGoals();
						if(goaolWasRecognized)
							truePositiveCounter++;
					}
				}
			}
			for(int problem=1;problem<=1;problem++){
				for(int problemHyp=0;problemHyp<=4;problemHyp++){
					for(int problemObs=0;problemObs<=0;problemObs++){
						String inputPRproblem = "experiments/easy-ipc-grid/easy-ipc-grid-aaai_p10-5-5" + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						totalPlanRecognitionProblems++;
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						
						boolean goaolWasRecognized = false;
						if(heuristic == PlanRecognizer.Heuristic.COMPLETION)
							goaolWasRecognized = planRecognizer.recognize();
						else if (heuristic == PlanRecognizer.Heuristic.UNIQUENESS)
							goaolWasRecognized = planRecognizer.recognizeUsingLandmarksUniqueness();
						
						candidateGoals = candidateGoals + planRecognizer.getAmountOfCandidateGoals();
						returnedGoalsCounter = returnedGoalsCounter + planRecognizer.getAmountOfRecognizedGoals();
						if(goaolWasRecognized)
							truePositiveCounter++;
					}
				}
			}
			for(int problem=1;problem<=1;problem++){
				for(int problemHyp=0;problemHyp<=4;problemHyp++){
					for(int problemObs=0;problemObs<=2;problemObs++){
						String inputPRproblem = "experiments/easy-ipc-grid/easy-ipc-grid_p5-5-5" + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						totalPlanRecognitionProblems++;
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						
						boolean goaolWasRecognized = false;
						if(heuristic == PlanRecognizer.Heuristic.COMPLETION)
							goaolWasRecognized = planRecognizer.recognize();
						else if (heuristic == PlanRecognizer.Heuristic.UNIQUENESS)
							goaolWasRecognized = planRecognizer.recognizeUsingLandmarksUniqueness();
						
						candidateGoals = candidateGoals + planRecognizer.getAmountOfCandidateGoals();
						returnedGoalsCounter = returnedGoalsCounter + planRecognizer.getAmountOfRecognizedGoals();
						if(goaolWasRecognized)
							truePositiveCounter++;
						
						if(full) break;
					}
				}
			}
			for(int problem=1;problem<=1;problem++){
				for(int problemHyp=0;problemHyp<=9;problemHyp++){
					for(int problemObs=0;problemObs<=2;problemObs++){
						String inputPRproblem = "experiments/easy-ipc-grid/easy-ipc-grid_p5-10-10" + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						totalPlanRecognitionProblems++;
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						
						boolean goaolWasRecognized = false;
						if(heuristic == PlanRecognizer.Heuristic.COMPLETION)
							goaolWasRecognized = planRecognizer.recognize();
						else if (heuristic == PlanRecognizer.Heuristic.UNIQUENESS)
							goaolWasRecognized = planRecognizer.recognizeUsingLandmarksUniqueness();
						
						candidateGoals = candidateGoals + planRecognizer.getAmountOfCandidateGoals();
						returnedGoalsCounter = returnedGoalsCounter + planRecognizer.getAmountOfRecognizedGoals();
						if(goaolWasRecognized)
							truePositiveCounter++;
						
						if(full) break;
					}
				}
			}
			for(int problem=1;problem<=1;problem++){
				for(int problemHyp=0;problemHyp<=4;problemHyp++){
					for(int problemObs=0;problemObs<=2;problemObs++){
						String inputPRproblem = "experiments/easy-ipc-grid/easy-ipc-grid_p10-5-5" + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						totalPlanRecognitionProblems++;
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						
						boolean goaolWasRecognized = false;
						if(heuristic == PlanRecognizer.Heuristic.COMPLETION)
							goaolWasRecognized = planRecognizer.recognize();
						else if (heuristic == PlanRecognizer.Heuristic.UNIQUENESS)
							goaolWasRecognized = planRecognizer.recognizeUsingLandmarksUniqueness();
						
						candidateGoals = candidateGoals + planRecognizer.getAmountOfCandidateGoals();
						returnedGoalsCounter = returnedGoalsCounter + planRecognizer.getAmountOfRecognizedGoals();
						if(goaolWasRecognized)
							truePositiveCounter++;
						
						if(full) break;
					}
				}
			}
			for(int problem=1;problem<=1;problem++){
				for(int problemHyp=0;problemHyp<=9;problemHyp++){
					for(int problemObs=0;problemObs<=2;problemObs++){
						String inputPRproblem = "experiments/easy-ipc-grid/easy-ipc-grid_p10-10-10" + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						totalPlanRecognitionProblems++;
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						
						boolean goaolWasRecognized = false;
						if(heuristic == PlanRecognizer.Heuristic.COMPLETION)
							goaolWasRecognized = planRecognizer.recognize();
						else if (heuristic == PlanRecognizer.Heuristic.UNIQUENESS)
							goaolWasRecognized = planRecognizer.recognizeUsingLandmarksUniqueness();
						
						candidateGoals = candidateGoals + planRecognizer.getAmountOfCandidateGoals();
						returnedGoalsCounter = returnedGoalsCounter + planRecognizer.getAmountOfRecognizedGoals();
						if(goaolWasRecognized)
							truePositiveCounter++;
						
						if(full) break;
					}
				}
			}
			for(int problem=4;problem<=7;problem++){
				for(int problemHyp=1;problemHyp<=4;problemHyp++){
					for(int problemObs=1;problemObs<=3;problemObs++){
						String inputPRproblem = "experiments/easy-ipc-grid/easy-ipc-grid_p0" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						totalPlanRecognitionProblems++;
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						
						boolean goaolWasRecognized = false;
						if(heuristic == PlanRecognizer.Heuristic.COMPLETION)
							goaolWasRecognized = planRecognizer.recognize();
						else if (heuristic == PlanRecognizer.Heuristic.UNIQUENESS)
							goaolWasRecognized = planRecognizer.recognizeUsingLandmarksUniqueness();
						
						candidateGoals = candidateGoals + planRecognizer.getAmountOfCandidateGoals();
						returnedGoalsCounter = returnedGoalsCounter + planRecognizer.getAmountOfRecognizedGoals();
						if(goaolWasRecognized)
							truePositiveCounter++;
						
						if(full) break;
					}
				}
			}
			float averageCandidateGoals = (candidateGoals / totalPlanRecognitionProblems);
			float falsePositiveCounter = (returnedGoalsCounter - truePositiveCounter);
			float falseNegativeCounter = (totalPlanRecognitionProblems - truePositiveCounter);
			float trueNegativeCounter = ((totalPlanRecognitionProblems*averageCandidateGoals) - falsePositiveCounter);
			float accuracy = truePositiveCounter / totalPlanRecognitionProblems;
			float precision = truePositiveCounter / (truePositiveCounter + falsePositiveCounter);
			float recall = truePositiveCounter / (truePositiveCounter + falseNegativeCounter);
			float f1score = 2 * ( (precision*recall) / (precision+recall));
			float fallout = falsePositiveCounter / (falsePositiveCounter + trueNegativeCounter);
			float missrate = falseNegativeCounter / (falseNegativeCounter + truePositiveCounter);
			float avgRecognizedGoals = (returnedGoalsCounter/totalPlanRecognitionProblems);
			long finalTime = System.currentTimeMillis();
			float totalTime = ((finalTime - initialTime)/1000);
			contentFile += (obs > 70 ? "100" : obs) + "\t" + (accuracy) + "\t" + (precision) + "\t" + (recall) + "\t" + (f1score) + "\t" + (fallout) + "\t" + (missrate) + "\t" + (avgRecognizedGoals) + "\t" + (totalTime / totalPlanRecognitionProblems) + "\n";
			totalProblems = totalProblems + totalPlanRecognitionProblems;
		}
		contentFile += "\n$> Total Problems: " + totalProblems;
		System.out.println("###################################################################################\n");
		System.out.println(contentFile);
		System.out.println("###################################################################################");
		writeExperimentFile(contentFile,  "grid-planrecognition-heuristic_" + heuristic.toString().toLowerCase() + "-" + (int) (threshold * 100));
	}
	
	private static void doIntrusionDetectionExperiments(float threshold, PlanRecognizer.Heuristic heuristic) throws IOException, InterruptedException{
		System.out.println("$> Plan Recognition - Intrusion Detection");
		String contentFile = "Obs % \tAccuracy \tPrecision \tRecall \tF1-score \tFall-out \tMiss-rate \tRecognized Goals \tTime(s) \n";
		float totalProblems = 0;
		for(int obs=10;obs<=100;obs+=20){
			long initialTime = System.currentTimeMillis();
			String observability = String.valueOf(obs);
			if(obs > 70)
				observability = "full";
			boolean full = observability.equals("full");
			if(full) observability = "_full";
			else observability = "_" + observability +  "_";
			
			int returnedGoalsCounter = 0;
			float totalPlanRecognitionProblems = 0;
			float truePositiveCounter = 0;
			int candidateGoals = 0;
			for(int problem=10;problem<=10;problem++){
				for(int problemHyp=0;problemHyp<=4;problemHyp++){
					for(int problemObs=0;problemObs<=0;problemObs++){
						String inputPRproblem = "experiments/intrusion-detection/intrusion-detection-aaai_p" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						totalPlanRecognitionProblems++;
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						
						boolean goaolWasRecognized = false;
						if(heuristic == PlanRecognizer.Heuristic.COMPLETION)
							goaolWasRecognized = planRecognizer.recognize();
						else if (heuristic == PlanRecognizer.Heuristic.UNIQUENESS)
							goaolWasRecognized = planRecognizer.recognizeUsingLandmarksUniqueness();
						
						candidateGoals = candidateGoals + planRecognizer.getAmountOfCandidateGoals();
						returnedGoalsCounter = returnedGoalsCounter + planRecognizer.getAmountOfRecognizedGoals();
						if(goaolWasRecognized)
							truePositiveCounter++;
						
						if(full) break;
					}
				}
			}
			for(int problem=20;problem<=20;problem++){
				for(int problemHyp=0;problemHyp<=9;problemHyp++){
					for(int problemObs=0;problemObs<=0;problemObs++){
						String inputPRproblem = "experiments/intrusion-detection/intrusion-detection-aaai_p" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						totalPlanRecognitionProblems++;
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						
						boolean goaolWasRecognized = false;
						if(heuristic == PlanRecognizer.Heuristic.COMPLETION)
							goaolWasRecognized = planRecognizer.recognize();
						else if (heuristic == PlanRecognizer.Heuristic.UNIQUENESS)
							goaolWasRecognized = planRecognizer.recognizeUsingLandmarksUniqueness();
						
						candidateGoals = candidateGoals + planRecognizer.getAmountOfCandidateGoals();
						returnedGoalsCounter = returnedGoalsCounter + planRecognizer.getAmountOfRecognizedGoals();
						if(goaolWasRecognized)
							truePositiveCounter++;
						
						if(full) break;
					}
				}
			}			
			for(int problem=10;problem<=10;problem+=10){
				for(int problemHyp=0;problemHyp<=9;problemHyp++){
					for(int problemObs=0;problemObs<=2;problemObs++){
						String inputPRproblem = "experiments/intrusion-detection/intrusion-detection_p" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						totalPlanRecognitionProblems++;
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						
						boolean goaolWasRecognized = false;
						if(heuristic == PlanRecognizer.Heuristic.COMPLETION)
							goaolWasRecognized = planRecognizer.recognize();
						else if (heuristic == PlanRecognizer.Heuristic.UNIQUENESS)
							goaolWasRecognized = planRecognizer.recognizeUsingLandmarksUniqueness();
						
						candidateGoals = candidateGoals + planRecognizer.getAmountOfCandidateGoals();
						returnedGoalsCounter = returnedGoalsCounter + planRecognizer.getAmountOfRecognizedGoals();
						if(goaolWasRecognized)
							truePositiveCounter++;
						
						if(full) break;
					}
				}
			}
			for(int problem=20;problem<=20;problem+=10){
				for(int problemHyp=0;problemHyp<=19;problemHyp++){
					for(int problemObs=0;problemObs<=2;problemObs++){
						String inputPRproblem = "experiments/intrusion-detection/intrusion-detection_p" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						totalPlanRecognitionProblems++;
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						
						boolean goaolWasRecognized = false;
						if(heuristic == PlanRecognizer.Heuristic.COMPLETION)
							goaolWasRecognized = planRecognizer.recognize();
						else if (heuristic == PlanRecognizer.Heuristic.UNIQUENESS)
							goaolWasRecognized = planRecognizer.recognizeUsingLandmarksUniqueness();
						
						candidateGoals = candidateGoals + planRecognizer.getAmountOfCandidateGoals();
						returnedGoalsCounter = returnedGoalsCounter + planRecognizer.getAmountOfRecognizedGoals();
						if(goaolWasRecognized)
							truePositiveCounter++;
						
						if(full) break;
					}
				}
			}
			float averageCandidateGoals = (candidateGoals / totalPlanRecognitionProblems);
			float falsePositiveCounter = (returnedGoalsCounter - truePositiveCounter);
			float falseNegativeCounter = (totalPlanRecognitionProblems - truePositiveCounter);
			float trueNegativeCounter = ((totalPlanRecognitionProblems*averageCandidateGoals) - falsePositiveCounter);
			float accuracy = truePositiveCounter / totalPlanRecognitionProblems;
			float precision = truePositiveCounter / (truePositiveCounter + falsePositiveCounter);
			float recall = truePositiveCounter / (truePositiveCounter + falseNegativeCounter);
			float f1score = 2 * ( (precision*recall) / (precision+recall));
			float fallout = falsePositiveCounter / (falsePositiveCounter + trueNegativeCounter);
			float missrate = falseNegativeCounter / (falseNegativeCounter + truePositiveCounter);
			float avgRecognizedGoals = (returnedGoalsCounter/totalPlanRecognitionProblems);
			long finalTime = System.currentTimeMillis();
			float totalTime = ((finalTime - initialTime)/1000);
			contentFile += (obs > 70 ? "100" : obs) + "\t" + (accuracy) + "\t" + (precision) + "\t" + (recall) + "\t" + (f1score) + "\t" + (fallout) + "\t" + (missrate) + "\t" + (avgRecognizedGoals) + "\t" + (totalTime / totalPlanRecognitionProblems) + "\n";
			totalProblems = totalProblems + totalPlanRecognitionProblems;
		}
		contentFile += "\n$> Total Problems: " + totalProblems;
		System.out.println("###################################################################################\n");
		System.out.println(contentFile);
		System.out.println("###################################################################################");
		writeExperimentFile(contentFile,  "intrusiondetection-planrecognition-heuristic_" + heuristic.toString().toLowerCase() + "-" + (int) (threshold * 100));
	}
	
	private static void doKitchenExperiments(float threshold, PlanRecognizer.Heuristic heuristic) throws IOException, InterruptedException{
		System.out.println("$> Plan Recognition - Logistics");
		String contentFile = "Obs % \tAccuracy \tPrecision \tRecall \tF1-score \tFall-out \tMiss-rate \tRecognized Goals \tTime(s) \n";
		float totalProblems = 0;
		for(int obs=10;obs<=100;obs+=20){
			long initialTime = System.currentTimeMillis();
			String observability = String.valueOf(obs);
			if(obs > 70)
				observability = "full";
			boolean full = observability.equals("full");
			if(full) observability = "_full_";
			else observability = "_" + observability +  "_";
			
			int returnedGoalsCounter = 0;
			float totalPlanRecognitionProblems = 0;
			float truePositiveCounter = 0;
			int candidateGoals = 0;
			for(int problemHyp=0;problemHyp<=0;problemHyp++){
				for(int problem=0;problem<=14;problem++){
					String inputPRproblem = "experiments/kitchen/kitchen_generic_hyp-" + problemHyp + observability + problem + ".tar.bz2";
					totalPlanRecognitionProblems++;
					PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
					
					boolean goaolWasRecognized = false;
					if(heuristic == PlanRecognizer.Heuristic.COMPLETION)
						goaolWasRecognized = planRecognizer.recognize();
					else if (heuristic == PlanRecognizer.Heuristic.UNIQUENESS)
						goaolWasRecognized = planRecognizer.recognizeUsingLandmarksUniqueness();
					
					candidateGoals = candidateGoals + planRecognizer.getAmountOfCandidateGoals();
					returnedGoalsCounter = returnedGoalsCounter + planRecognizer.getAmountOfRecognizedGoals();
					if(goaolWasRecognized)
						truePositiveCounter++;
					
					if(full) break;
				}
			}
			float averageCandidateGoals = (candidateGoals / totalPlanRecognitionProblems);
			float falsePositiveCounter = (returnedGoalsCounter - truePositiveCounter);
			float falseNegativeCounter = (totalPlanRecognitionProblems - truePositiveCounter);
			float trueNegativeCounter = ((totalPlanRecognitionProblems*averageCandidateGoals) - falsePositiveCounter);
			float accuracy = truePositiveCounter / totalPlanRecognitionProblems;
			float precision = truePositiveCounter / (truePositiveCounter + falsePositiveCounter);
			float recall = truePositiveCounter / (truePositiveCounter + falseNegativeCounter);
			float f1score = 2 * ( (precision*recall) / (precision+recall));
			float fallout = falsePositiveCounter / (falsePositiveCounter + trueNegativeCounter);
			float missrate = falseNegativeCounter / (falseNegativeCounter + truePositiveCounter);
			float avgRecognizedGoals = (returnedGoalsCounter/totalPlanRecognitionProblems);
			long finalTime = System.currentTimeMillis();
			float totalTime = ((finalTime - initialTime)/1000);
			contentFile += (obs > 70 ? "100" : obs) + "\t" + (accuracy) + "\t" + (precision) + "\t" + (recall) + "\t" + (f1score) + "\t" + (fallout) + "\t" + (missrate) + "\t" + (avgRecognizedGoals) + "\t" + (totalTime / totalPlanRecognitionProblems) + "\n";
			totalProblems = totalProblems + totalPlanRecognitionProblems;
		}
		contentFile += "\n$> Total Problems: " + totalProblems;
		System.out.println("###################################################################################\n");
		System.out.println(contentFile);
		System.out.println("###################################################################################");
		writeExperimentFile(contentFile,  "kitchen-planrecognition-heuristic_" + heuristic.toString().toLowerCase() + "-" + (int) (threshold * 100));
	}
	
	private static void doLogisticsExperiments(float threshold, PlanRecognizer.Heuristic heuristic) throws IOException, InterruptedException{
		System.out.println("$> Plan Recognition - Logistics");
		String contentFile = "Obs % \tAccuracy \tPrecision \tRecall \tF1-score \tFall-out \tMiss-rate \tRecognized Goals \tTime(s) \n";
		float totalProblems = 0;
		for(int obs=10;obs<=100;obs+=20){
			long initialTime = System.currentTimeMillis();
			String observability = String.valueOf(obs);
			if(obs > 70)
				observability = "full";
			boolean full = observability.equals("full");
			if(full) observability = "_full";
			else observability = "_" + observability +  "_";
			
			int returnedGoalsCounter = 0;
			float totalPlanRecognitionProblems = 0;
			float truePositiveCounter = 0;
			int candidateGoals = 0;
			
			for(int problem=1;problem<=3;problem++){
				for(int problemHyp=0;problemHyp<=4;problemHyp++){
					for(int problemObs=0;problemObs<=0;problemObs++){
						String inputPRproblem = "experiments/logistics/logistics-aaai_p0" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						totalPlanRecognitionProblems++;
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						
						boolean goaolWasRecognized = false;
						if(heuristic == PlanRecognizer.Heuristic.COMPLETION)
							goaolWasRecognized = planRecognizer.recognize();
						else if (heuristic == PlanRecognizer.Heuristic.UNIQUENESS)
							goaolWasRecognized = planRecognizer.recognizeUsingLandmarksUniqueness();
						
						candidateGoals = candidateGoals + planRecognizer.getAmountOfCandidateGoals();
						returnedGoalsCounter = returnedGoalsCounter + planRecognizer.getAmountOfRecognizedGoals();
						if(goaolWasRecognized)
							truePositiveCounter++;
						
						if(full) break;
					}
				}
			}
			for(int problem=1;problem<=3;problem++){
				for(int problemHyp=0;problemHyp<=9;problemHyp++){
					for(int problemObs=0;problemObs<=2;problemObs++){
						String inputPRproblem = "experiments/logistics/logistics_p0" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						totalPlanRecognitionProblems++;
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						
						boolean goaolWasRecognized = false;
						if(heuristic == PlanRecognizer.Heuristic.COMPLETION)
							goaolWasRecognized = planRecognizer.recognize();
						else if (heuristic == PlanRecognizer.Heuristic.UNIQUENESS)
							goaolWasRecognized = planRecognizer.recognizeUsingLandmarksUniqueness();
						
						candidateGoals = candidateGoals + planRecognizer.getAmountOfCandidateGoals();
						returnedGoalsCounter = returnedGoalsCounter + planRecognizer.getAmountOfRecognizedGoals();
						if(goaolWasRecognized)
							truePositiveCounter++;
						
						if(full) break;
					}
				}
			}
			for(int problem=4;problem<=7;problem++){
				for(int problemHyp=1;problemHyp<=4;problemHyp++){
					for(int problemObs=1;problemObs<=3;problemObs++){
						String inputPRproblem = "experiments/logistics/logistics_p0" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						totalPlanRecognitionProblems++;
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						candidateGoals = candidateGoals + planRecognizer.getAmountOfCandidateGoals();
						
						boolean goaolWasRecognized = false;
						if(heuristic == PlanRecognizer.Heuristic.COMPLETION)
							goaolWasRecognized = planRecognizer.recognize();
						else if (heuristic == PlanRecognizer.Heuristic.UNIQUENESS)
							goaolWasRecognized = planRecognizer.recognizeUsingLandmarksUniqueness();
						
						returnedGoalsCounter = returnedGoalsCounter + planRecognizer.getAmountOfRecognizedGoals();
						if(goaolWasRecognized)
							truePositiveCounter++;
						
						if(full) break;
					}
				}
			}
			float averageCandidateGoals = (candidateGoals / totalPlanRecognitionProblems);
			float falsePositiveCounter = (returnedGoalsCounter - truePositiveCounter);
			float falseNegativeCounter = (totalPlanRecognitionProblems - truePositiveCounter);
			float trueNegativeCounter = ((totalPlanRecognitionProblems*averageCandidateGoals) - falsePositiveCounter);
			float accuracy = truePositiveCounter / totalPlanRecognitionProblems;
			float precision = truePositiveCounter / (truePositiveCounter + falsePositiveCounter);
			float recall = truePositiveCounter / (truePositiveCounter + falseNegativeCounter);
			float f1score = 2 * ( (precision*recall) / (precision+recall));
			float fallout = falsePositiveCounter / (falsePositiveCounter + trueNegativeCounter);
			float missrate = falseNegativeCounter / (falseNegativeCounter + truePositiveCounter);
			float avgRecognizedGoals = (returnedGoalsCounter/totalPlanRecognitionProblems);
			long finalTime = System.currentTimeMillis();
			float totalTime = ((finalTime - initialTime)/1000);
			contentFile += (obs > 70 ? "100" : obs) + "\t" + (accuracy) + "\t" + (precision) + "\t" + (recall) + "\t" + (f1score) + "\t" + (fallout) + "\t" + (missrate) + "\t" + (avgRecognizedGoals) + "\t" + (totalTime / totalPlanRecognitionProblems) + "\n";
			totalProblems = totalProblems + totalPlanRecognitionProblems;
		}
		contentFile += "\n$> Total Problems: " + totalProblems;
		System.out.println("###################################################################################\n");
		System.out.println(contentFile);
		System.out.println("###################################################################################");
		writeExperimentFile(contentFile,  "logistics-planrecognition-heuristic_" + heuristic.toString().toLowerCase() + "-" + (int) (threshold * 100));
	}
	
	private static void doFilter(String domainName, float threshold) throws IOException, InterruptedException{
		for(int obs=10;obs<=100;obs+=20){
			String observability = String.valueOf(obs);
			if(obs > 70)
				observability = "full";
			boolean full = observability.equals("full");
			if(full) observability = "_full";
			else observability = "_" + observability +  "_";
		
			for(int problem=1;problem<=7;problem++){
				for(int problemHyp=1;problemHyp<=4;problemHyp++){
					for(int problemObs=1;problemObs<=3;problemObs++){
						String inputPRproblem = "experiments/" + domainName + "/" + domainName + "_p0" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						planRecognizer.newFilter(true);
						
						if(full) break;
					}
				}
			}
		}
	}
	
	private static void doExperiments(String domainName, float threshold, PlanRecognizer.Heuristic heuristic) throws IOException, InterruptedException{
		System.out.println("$> Plan Recognition - " + domainName);
		String contentFile = "Obs % \tAccuracy \tPrecision \tRecall \tF1-score \tFall-out \tMiss-rate \tRecognized Goals \tTime(s) \n";
		float totalProblems = 0;
		for(int obs=10;obs<=100;obs+=20){
			long initialTime = System.currentTimeMillis();
			String observability = String.valueOf(obs);
			if(obs > 70)
				observability = "full";
			boolean full = observability.equals("full");
			if(full) observability = "_full";
			else observability = "_" + observability +  "_";
			
			int returnedGoalsCounter = 0;
			float totalPlanRecognitionProblems = 0;
			float truePositiveCounter = 0;
			int candidateGoals = 0;
			for(int problem=1;problem<=7;problem++){
				for(int problemHyp=1;problemHyp<=4;problemHyp++){
					for(int problemObs=1;problemObs<=3;problemObs++){
						String inputPRproblem = "experiments/" + domainName + "/" + domainName + "_p0" + problem + "_hyp-" + problemHyp + observability + (full ? "" : problemObs) + ".tar.bz2";
						totalPlanRecognitionProblems++;
						PlanRecognizer planRecognizer = new PlanRecognizer(inputPRproblem, threshold);
						
						boolean goaolWasRecognized = false;
						if(heuristic == PlanRecognizer.Heuristic.COMPLETION)
							goaolWasRecognized = planRecognizer.recognize();
						else if (heuristic == PlanRecognizer.Heuristic.UNIQUENESS)
							goaolWasRecognized = planRecognizer.recognizeUsingLandmarksUniqueness();
						
						candidateGoals = candidateGoals + planRecognizer.getAmountOfCandidateGoals();
						returnedGoalsCounter = returnedGoalsCounter + planRecognizer.getAmountOfRecognizedGoals();
						if(goaolWasRecognized)
							truePositiveCounter++;
						
						if(full) break;
					}
				}
			}
			float averageCandidateGoals = (candidateGoals / totalPlanRecognitionProblems);
			float falsePositiveCounter = (returnedGoalsCounter - truePositiveCounter);
			float falseNegativeCounter = (totalPlanRecognitionProblems - truePositiveCounter);
			float trueNegativeCounter = ((totalPlanRecognitionProblems*averageCandidateGoals) - falsePositiveCounter);
			float accuracy = truePositiveCounter / totalPlanRecognitionProblems;
			float precision = truePositiveCounter / (truePositiveCounter + falsePositiveCounter);
			float recall = truePositiveCounter / (truePositiveCounter + falseNegativeCounter);
			float f1score = 2 * ( (precision*recall) / (precision+recall));
			float fallout = falsePositiveCounter / (falsePositiveCounter + trueNegativeCounter);
			float missrate = falseNegativeCounter / (falseNegativeCounter + truePositiveCounter);
			float avgRecognizedGoals = (returnedGoalsCounter/totalPlanRecognitionProblems);
			long finalTime = System.currentTimeMillis();
			float totalTime = ((finalTime - initialTime)/1000);
			contentFile += (obs > 70 ? "100" : obs) + "\t" + (accuracy) + "\t" + (precision) + "\t" + (recall) + "\t" + (f1score) + "\t" + (fallout) + "\t" + (missrate) + "\t" + (avgRecognizedGoals) + "\t" + (totalTime / totalPlanRecognitionProblems) + "\n";
			totalProblems = totalProblems + totalPlanRecognitionProblems;
		}
		contentFile += "\n$> Total Problems: " + totalProblems;
		System.out.println("###################################################################################\n");
		System.out.println(contentFile);
		System.out.println("###################################################################################");
		writeExperimentFile(contentFile, domainName + "-planrecognition-heuristic_" + heuristic.toString().toLowerCase() + "-" + (int) (threshold * 100));
	}
	
	private static void writeExperimentFile(String content, String outputFile) throws IOException{
		File file = new File(outputFile + ".txt");
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();
		System.out.println("\nWriting file " + file.getAbsolutePath());
	}
}