#!/bin/bash -l

#SBATCH --nodes=1 # number of nodes
#SBATCH --ntasks=1
#SBATCH --mem=32G # memory pool for all cores

module load miniconda3
module load openjdk
conda activate sa-goal-recognition

srun --exclusive python3 run_goalrecognizer.py $1 &

wait