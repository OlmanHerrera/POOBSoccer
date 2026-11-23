package src.domain;

/**
 * Personaje que se puede escoger: el nombre de su país y su tiro de poder.
 */
public class CountryDefinition{
    private String id;
    private AbilityStrategy ability;

    /**
     * Crea un personaje.
     * @param id nombre del país
     * @param ability tiro de poder del país
     */
    public CountryDefinition(String id, AbilityStrategy ability){
        this.id = id;
        this.ability = ability;
    }

    /** @return nombre del país */
    public String getId(){
        return id;
    }

    /** @return tiro de poder del país */
    public AbilityStrategy getAbility(){
        return ability;
    }
}
