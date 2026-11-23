package src.domain;

/**
 * Perfil experto: decide la acción según el estado del partido.
 * Activa el aura apenas puede, patea si tiene el balón al alcance del pie,
 * defiende cuando va ganando y ataca cuando va empatando o perdiendo.
 * Para defender y atacar usa los otros dos perfiles.
 */
public class ExpertProfile implements PlayerStrategy
{
    private TargetProfile offensive;
    private TargetProfile defensive;

    /**
     * Crea el perfil con un perfil ofensivo y uno defensivo.
     */
    public ExpertProfile(){
        offensive = new OffensiveProfile();
        defensive = new DefensiveProfile();
    }

    /**
     * @param p jugador que decide
     * @param m partido actual
     * @return acción que quiere hacer
     */
    public Action decide(Player p, Match m ){
        if (p.canActivatePower()){
            return Action.POWER;
        }
        if (p.canKick(m.getBallBounds())){
            return Action.KICK;
        }
        if (m.getLeader() == p.getSide()){
            return defensive.decide(p, m);
        }
        return offensive.decide(p, m);
    }
}
