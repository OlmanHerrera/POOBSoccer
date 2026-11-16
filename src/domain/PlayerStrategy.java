package src.domain;
public interface PlayerStrategy
{
 
    Action decide(PlayerSprite p, Match m);
}