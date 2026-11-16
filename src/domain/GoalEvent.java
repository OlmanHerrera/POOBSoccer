package src.domain;


public class GoalEvent
{
  private Match source;
  private Side scorer;
  private int leftScore, rightScore;

  public GoalEvent(Match source, Side scorer, int leftScore, int rightScore)
  {
      this.source = source;
      this.scorer = scorer;
      this.leftScore = leftScore;
      this.rightScore = rightScore;
  }

  public Match getSource()
  {  return source;  }

  public Side getScorer()
  {  return scorer;  }

  public int getLeftScore()
  {  return leftScore;  }

  public int getRightScore()
  {  return rightScore;  }
}
