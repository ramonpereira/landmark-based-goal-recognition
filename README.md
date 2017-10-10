# Planning-PlanRecognition

Landmark-Based Goal Recognition Approaches As Planning.

- Goal Recognition Filter using Landmarks;

- Goal Completion Heuristic;

- Landmark Uniqueness Heuristic;

These approaches have been published in ECAI-16 and AAAI-17.

- [Landmark-Based Heuristics for Goal Recognition (AAAI-17)](https://www.aaai.org/ocs/index.php/AAAI/AAAI17/paper/view/14666).

- [Landmark-Based Plan Recognition (ECAI-16)](https://arxiv.org/pdf/1604.01277.pdf)

## Usage

- Option (1) - Parameters needed: <filter | goalcompletion | uniqueness> <tar.bz2 file> <threshold_value>

> Example: filter goal_recognition-problem.tar.bz2 0.1

- Option (2) - Parameters needed: <filter | goalcompletion | uniqueness> <domain.pddl> <problem.pddl> <goals.dat> <observations.dat> <correct_goal.dat> <threshold_value>

> Example: goalcompletion domain.pddl template.pddl hyps.dat obs.dat real_hyp.dat 0.15
