package src.domain;

/**
 * País que se puede escoger: su identificador, su nombre y su tiro de poder.
 * El identificador también es el prefijo de sus imágenes.
 */
public class CountryDefinition
{
    private String id;
    private String name;
    private AbilityStrategy ability;

    /**
     * Crea un país.
     * @param id identificador
     * @param name nombre para mostrar
     * @param ability tiro de poder del país
     */
    public CountryDefinition(String id, String name, AbilityStrategy ability){
        this.id = id;
        this.name = name;
        this.ability = ability;
    }

    /** @return identificador del país */
    public String getId(){
        return id;
    }

    /** @return nombre para mostrar */
    public String getName(){
        return name;
    }

    /** @return tiro de poder del país */
    public AbilityStrategy getAbility(){
        return ability;
    }
}
