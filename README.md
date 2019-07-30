# Landmark-Based-GoalRecognition

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

## Observations as Facts

Our approaches also deal with observations as facts. To use the recognizers in such mode, please use the branch called *obsfacts*:

- https://github.com/ramonpereira/Landmark-Based-GoalRecognition/tree/obsfacts

At the following link we have some examples of how we use observations as facts:

- https://github.com/ramonpereira/Landmark-Based-GoalRecognition/tree/obsfacts/experiments/factobs

There is also an executable file called *goalrecognizer-obsfacts.jar*.

## Dependencies

Our goal recognizer uses the following libs (which are included in [lib](lib)):

- jgrapht-jdk1.6.jar (A free Java Graph Library);
- planning-landmarks2.3.jar (A Landmark Extraction Algorithm based on [Ordered Landmarks in Planning](https://www.aaai.org/Papers/JAIR/Vol22/JAIR-2208.pdf));
- planning-utils2.2.jar (PDDL Parser and Planning data structure from JavaFF);
