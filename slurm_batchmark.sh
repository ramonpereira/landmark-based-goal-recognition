#!/bin/bash -l

#SBATCH --job-name=Landmark-GR
#SBATCH --nodes=1 # number of nodes
#SBATCH --ntasks=20 # number of tasks total
#SBATCH --cpus-per-task=1 # number of cores
#SBATCH --mem=128G # memory pool for all cores

#SBATCH --ntasks-per-node=20 # one job per node
#SBATCH --gres=gpu:0 # 0 GPU out of 3
#SBATCH --partition=uoa-compute

#SBATCH -o slurm.%j.out # STDOUT
#SBATCH -e slurm.%j.err # STDERR

#SBATCH --mail-type=ALL
#SBATCH --mail-user=felipe.meneguzzi@abdn.ac.uk

pwd
module load miniconda3
conda info --envs
conda activate sa-goal-recognition

domains=("blocks-world" "campus" "depots" "driverlog" "dwr" "easy-ipc-grid" "ferry" "intrusion-detection" "kitchen" "logistics" "miconic" "rovers" "satellite" "sokoban" "zeno-travel")

# domains=("intrusion-detection")

mkdir results

for domain in "${domains[@]}"
do
    DIRECTORY="results/${domain}"
	echo "Launching Landmark Recognition for ${domain}"
    # if [ ! -d "$DIRECTORY" ]; then
    #     echo "$DIRECTORY does not exist."
        sbatch --job-name="${domain}" --mail-user=${USER}@abdn.ac.uk --mem=32G -o "results/slurm.${domain}.log" -e "results/slurm.${domain}.err" slurm_single_benchmark.sh $domain &
    # else
    #     echo "${DIRECTORY} is finished"
    # fi
done
wait
