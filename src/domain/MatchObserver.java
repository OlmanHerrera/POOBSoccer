package src.domain;


public interface MatchObserver
{
  void goalScored(GoalEvent e);

  void matchEnded(GoalEvent e);
}
