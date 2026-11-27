package src.domain;

/**
 * Personaje que se puede escoger: el nombre de su país, su tiro de poder y sus imágenes.
 */
public class CountryDefinition{
    private String id;
    private AbilityStrategy ability;
    private String image;

    /**
     * Crea un personaje.
     * @param id nombre del país
     * @param ability tiro de poder del país
     * @param image prefijo de sus imágenes, por ejemplo korea para korea_run_r
     */
    public CountryDefinition(String id, AbilityStrategy ability, String image){
        this.id = id;
        this.ability = ability;
        this.image = image;
    }

    /** @return nombre del país */
    public String getId(){
        return id;
    }

    /** @return tiro de poder del país */
    public AbilityStrategy getAbility(){
        return ability;
    }

    /** @return prefijo de sus imágenes en la carpeta Images */
    public String getImage(){
        return image;
    }
}
