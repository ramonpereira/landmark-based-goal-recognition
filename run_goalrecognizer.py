#!/usr/bin/env python3
import os
import subprocess
import shutil
import tarfile
import logging
import re
import time
import argparse
from datetime import datetime

# Set up logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    filename='goalrecognizer_run.log',
    filemode='a'  # Changed to append mode
)
console = logging.StreamHandler()
console.setLevel(logging.INFO)
logging.getLogger('').addHandler(console)

# Constants
CURRENT_DIR = os.getcwd()
DATASET_PATH = f"{CURRENT_DIR}/../goal-plan-recognition-dataset"
OBSERVABILITY_DEGREES = ["10", "30", "50", "70", "100"]
JAR_FILE = "goalrecognizer1.2.jar"
OUTPUT_FILE = "output.txt"
RESULTS_DIR = f"{CURRENT_DIR}/goalrecognizer_results"
WORK_DIR = f"{CURRENT_DIR}/work_dir"  # Directory to copy tar.bz2 files to
OUTPUTS_DIR = f"{CURRENT_DIR}/goalrecognizer_outputs"  # Directory to store outputs

def ensure_dir(directory):
    """Create directory if it doesn't exist"""
    if not os.path.exists(directory):
        os.makedirs(directory)

def extract_problem(problem_file, extract_dir):
    """Extract the tar.bz2 problem file to the specified directory"""
    try:
        with tarfile.open(problem_file, "r:bz2") as tar:
            tar.extractall(path=extract_dir)
        return True
    except Exception as e:
        logging.error(f"Error extracting {problem_file}: {e}")
        return False

def parse_output(output_text, execution_time):
    """Parse the output to extract the required metrics"""
    metrics = {
        "HYPS": 0,
        "OBSERVATIONS": 0,
        "CORRECT": "False",
        "SPREAD": 0,
        "TIME": execution_time
    }
    
    # Count observations
    observations_match = re.search(r'# Observations:\s*\n((?:\s*>\$ .*\n)*)', output_text)
    if observations_match:
        observations = observations_match.group(1).strip().split('\n')
        metrics["OBSERVATIONS"] = len(observations)
    
    # Count hypotheses - Using the dollar sign separators to get precisely the hypothesis section
    dollar_sections = output_text.split('$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$')
    if len(dollar_sections) >= 4:  # We expect at least 4 sections after splitting
        hyps_section = dollar_sections[2].strip()  # The third section (index 2) contains the hypotheses
        hyps_lines = [line for line in hyps_section.split('\n') if line.strip().startswith('$>')]
        metrics["HYPS"] = len(hyps_lines)
    
    # Extract recognized goals within threshold
    recognized_goals_section = re.search(r'\$> Recognized goal\(s\) within the threshold.*?\n((?:\$> .*\n)*)', output_text)
    if recognized_goals_section:
        recognized_goals = recognized_goals_section.group(1).strip().split('\n')
        metrics["SPREAD"] = len(recognized_goals)
    
    # Check if correct goal was recognized
    correct_match = re.search(r'<\?> Was the correct goal recognized correctly\? (true|false)', output_text)
    if correct_match:
        if correct_match.group(1).lower() == 'true':
            metrics["CORRECT"] = "True"
        else:
            metrics["CORRECT"] = "False"
    
    return metrics

def format_metrics_output(metrics):
    """Format the metrics for output"""
    output = f"HYPS = {metrics['HYPS']}\n"
    output += f"OBSERVATIONS = {metrics['OBSERVATIONS']}\n"
    output += f"CORRECT = {metrics['CORRECT']}\n"
    output += f"SPREAD = {metrics['SPREAD']}\n"
    output += f"TIME = {metrics['TIME']:.6f}\n"
    return output

def process_domain(domain):
    """Process all problems for a single domain"""
    domain_path = os.path.join(DATASET_PATH, domain)
    if not os.path.exists(domain_path):
        logging.error(f"Domain path not found: {domain_path}")
        return False
        
    logging.info(f"Processing domain: {domain}")
    
    # Create results and outputs directories for this domain without timestamp
    results_dir = os.path.join(RESULTS_DIR, domain)
    outputs_dir = os.path.join(OUTPUTS_DIR, domain)
    ensure_dir(results_dir)
    ensure_dir(outputs_dir)
    
    # Ensure work directory exists and is empty
    work_dir = os.path.join(WORK_DIR, domain)
    if os.path.exists(work_dir):
        shutil.rmtree(work_dir)
    os.makedirs(work_dir)
    
    # Process each observability degree
    for obs_degree in OBSERVABILITY_DEGREES:
        obs_path = os.path.join(domain_path, obs_degree)
        if not os.path.exists(obs_path):
            logging.warning(f"Observability path not found: {obs_path}")
            continue
            
        logging.info(f"Processing observability degree: {obs_degree}%")
        obs_results_dir = os.path.join(results_dir, obs_degree)
        obs_outputs_dir = os.path.join(outputs_dir, obs_degree)
        ensure_dir(obs_results_dir)
        ensure_dir(obs_outputs_dir)
        
        # Count statistics
        total_problems = 0
        successful = 0
        failed = 0
        skipped = 0
        
        # Process each problem file
        for problem_file in os.listdir(obs_path):
            if problem_file.endswith(".tar.bz2"):
                total_problems += 1
                problem_path = os.path.join(obs_path, problem_file)
                problem_name = os.path.splitext(os.path.splitext(problem_file)[0])[0]
                
                # Create output and metrics file paths
                metrics_file_path = os.path.join(obs_results_dir, f"{problem_file.replace('.tar.bz2', '.txt')}")
                output_file_path = os.path.join(obs_outputs_dir, f"{problem_file.replace('.tar.bz2', '.txt')}")
                
                # Check if this problem was already processed
                if os.path.exists(metrics_file_path):
                    logging.info(f"Problem already processed, skipping: {problem_name}")
                    skipped += 1
                    continue
                
                logging.info(f"Processing problem: {problem_name}")
                
                # Clean work directory
                for file in os.listdir(work_dir):
                    file_path = os.path.join(work_dir, file)
                    try:
                        if os.path.isfile(file_path):
                            os.unlink(file_path)
                        elif os.path.isdir(file_path):
                            shutil.rmtree(file_path)
                    except Exception as e:
                        logging.error(f"Error cleaning work directory: {e}")
                
                # Copy tar.bz2 file to work directory
                work_problem_path = os.path.join(work_dir, problem_file)
                try:
                    shutil.copy2(problem_path, work_problem_path)
                    logging.info(f"Copied {problem_file} to work directory")
                except Exception as e:
                    logging.error(f"Error copying problem file: {e}")
                    failed += 1
                    continue
                
                # Validate the problem file without extracting it
                if not tarfile.is_tarfile(work_problem_path):
                    logging.error(f"Invalid tar.bz2 file: {work_problem_path}")
                    failed += 1
                    continue

                # Copy the goalrecognizer JAR file into the work directory
                jar_work_path = os.path.join(work_dir, JAR_FILE)
                try:
                    shutil.copy2(JAR_FILE, jar_work_path)
                    logging.info(f"Copied {JAR_FILE} to work directory")
                except Exception as e:
                    logging.error(f"Error copying JAR file: {e}")
                    failed += 1
                    continue

                # Change the working directory to the work directory
                original_cwd = os.getcwd()
                os.chdir(work_dir)

                # Run goalrecognizer with the copied file
                try:
                    cmd = ["java", "-jar", JAR_FILE, "-goalcompletion", problem_file, "0"]
                    logging.info(f"Running: {' '.join(cmd)}")

                    # Start timing
                    start_time = time.time()

                    result = subprocess.run(cmd, capture_output=True, text=True)

                    # End timing
                    end_time = time.time()
                    execution_time = end_time - start_time

                    with open(output_file_path, 'w') as f:
                        f.write(result.stdout)

                    if result.stderr:
                        logging.warning(f"Error output from Java: {result.stderr}")

                    success = True
                except Exception as e:
                    logging.error(f"Error running goalrecognizer: {e}")
                    success = False
                    execution_time = 0
                    output_text = ""
                finally:
                    # Change back to the original working directory
                    os.chdir(original_cwd)

                if success:
                    successful += 1

                    # Parse output and extract metrics
                    metrics = parse_output(result.stdout, execution_time)

                    # Write metrics to problem-specific file
                    with open(metrics_file_path, 'w') as f:
                        f.write(format_metrics_output(metrics))
                else:
                    failed += 1
        
        # Update statistics in domain log
        domain_stats_path = os.path.join(results_dir, f"{obs_degree}_stats.txt")
        with open(domain_stats_path, 'w') as stats:
            stats.write(f"Domain: {domain}\n")
            stats.write(f"Observability: {obs_degree}%\n")
            stats.write(f"Total Problems: {total_problems}\n")
            stats.write(f"Processed: {successful + failed}\n")
            stats.write(f"Successful: {successful}\n")
            stats.write(f"Failed: {failed}\n")
            stats.write(f"Skipped (already processed): {skipped}\n")
    
    # Clean up work directory at the end
    if os.path.exists(work_dir):
        shutil.rmtree(work_dir)
    
    logging.info(f"Domain processing complete. Results stored in {results_dir} and {outputs_dir}")
    return True

def main():
    parser = argparse.ArgumentParser(description='Process a single domain from the goal recognition dataset.')
    parser.add_argument('domain', help='The domain to process, e.g., "blocks-world"')
    args = parser.parse_args()
    
    # Create the main results directory if it doesn't exist
    ensure_dir(RESULTS_DIR)
    
    # Process the specified domain
    domain = args.domain
    success = process_domain(domain)
    
    if success:
        logging.info(f"Successfully processed domain: {domain}")
    else:
        logging.error(f"Failed to process domain: {domain}")
        return 1
    
    return 0

if __name__ == "__main__":
    exit(main())