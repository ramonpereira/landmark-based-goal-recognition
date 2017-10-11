# Planning-GoalRecognition

Landmark-Based Approaches For Goal Recognition.

- Goal Recognition Filter using Landmarks;

- Goal Completion Heuristic;

- Landmark Uniqueness Heuristic;

These approaches have been published in ECAI-16 and AAAI-17.

- [Landmark-Based Plan Recognition (ECAI-16)](https://arxiv.org/pdf/1604.01277.pdf);

- [Landmark-Based Heuristics for Goal Recognition (AAAI-17)](https://www.aaai.org/ocs/index.php/AAAI/AAAI17/paper/view/14666);

## Usage

- Option (1): Single tar.bz2 file containing domain, problem (initial state), set of goals, observations, correct goal, threshold value.

> Parameters needed: <-filter | -goalcompletion | -uniqueness> <tar.bz2 file> <threshold_value>

```bash
java -jar goalrecognizer1.0.jar -filter experiments/blocks-test/blocks-test.tar.bz2 0
```

- Option (2): Separated files, e.g., domain, problem (initial state), set of goals, observations, correct goal, threshold value.

> Parameters needed: <-filter | -goalcompletion | -uniqueness> <domain.pddl> <problem.pddl> <goals.dat> <observations.dat> <correct_goal.dat> <threshold_value>

```bash
java -jar goalrecognizer1.0.jar -goalcompletion experiments/blocks-test/domain.pddl experiments/blocks-test/template.pddl experiments/blocks-test/hyps.dat experiments/blocks-test/obs.dat experiments/blocks-test/real_hyp.dat 0.1
```
