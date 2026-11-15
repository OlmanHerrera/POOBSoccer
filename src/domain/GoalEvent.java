package src.domain;


public class GoalEvent
{
  private Match source;     // the match where the goal happened
  private int scorer;       // Match.LEFT or Match.RIGHT
  private int leftScore, rightScore;

  public GoalEvent(Match source, int scorer, int leftScore, int rightScore)
  {
      this.source = source;
      this.scorer = scorer;
      this.leftScore = leftScore;
      this.rightScore = rightScore;
  }

  public Match getSource()
  {  return source;  }

  public int getScorer()
  {  return scorer;  }

  public int getLeftScore()
  {  return leftScore;  }

  public int getRightScore()
  {  return rightScore;  }
} // end of GoalEvent class
