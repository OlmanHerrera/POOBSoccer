package src.domain;


public interface MatchObserver
{
  void goalScored(GoalEvent e);
  // called after a goal is added to the score

  void matchEnded(GoalEvent e);
  // called after the goal that wins the match; e.getScorer() is the winner
}
