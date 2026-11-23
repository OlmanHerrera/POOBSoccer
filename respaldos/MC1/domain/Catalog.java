package src.domain;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Todo lo que se puede escoger o agregar al juego: países, perfiles de máquina,
 * tipos de partido, resoluciones, estadísticas y elementos de la cancha.
 * Las líneas de registro van solo en registerAll.
 */
public class Catalog
{
    private HashMap<String, CountryDefinition> countries;
    private HashMap<String, PlayerStrategy> profiles;
    private HashMap<String, MatchRules> rules;
    private HashMap<String, Field> fields;
    private ArrayList<Stat> stats;
    private ArrayList<FieldElement> elements;

    /**
     * Crea el catálogo con todo lo registrado.
     */
    public Catalog(){
        countries = new HashMap<String, CountryDefinition>();
        profiles = new HashMap<String, PlayerStrategy>();
        rules = new HashMap<String, MatchRules>();
        fields = new HashMap<String, Field>();
        stats = new ArrayList<Stat>();
        elements = new ArrayList<FieldElement>();
        registerAll();
    }

    /**
     * Único lugar con las líneas de registro.
     */
    private void registerAll(){
        addCountry(new CountryDefinition("korea", "Corea", new BlueAuraShot()));
        addCountry(new CountryDefinition("cameroon", "Camerún", new LightningShot()));
        addCountry(new CountryDefinition("nigeria", "Nigeria", new TornadoShot()));
        addCountry(new CountryDefinition("usa", "USA", new DeceptiveShot()));

        addProfile("clumsy", new ClumsyProfile());
        addProfile("offensive", new OffensiveProfile());
        addProfile("defensive", new DefensiveProfile());
        addProfile("expert", new ExpertProfile());

        addField(new Field("500x400", 500, 400));
        // tipos de partido: pendiente MC3
        // estadísticas: pendiente MC6
        // elementos de la cancha: pendiente MC1
    }

    private void addCountry(CountryDefinition c){
        countries.put(c.getId(), c);
    }

    private void addProfile(String id, PlayerStrategy p){
        profiles.put(id, p);
    }

    private void addRules(MatchRules r){
        rules.put(r.getId(), r);
    }

    private void addField(Field f){
        fields.put(f.getId(), f);
    }

    private void addStat(Stat s){
        stats.add(s);
    }

    private void addElement(FieldElement e){
        elements.add(e);
    }

    /**
     * @param id identificador
     * @return país, o null si no existe
     */
    public CountryDefinition getCountry(String id){
        return countries.get(id);
    }

    /**
     * @param id identificador
     * @return perfil, o null si no existe
     */
    public PlayerStrategy getProfile(String id){
        return profiles.get(id);
    }

    /**
     * @param id identificador
     * @return tipo de partido, o null si no existe
     */
    public MatchRules getRules(String id){
        return rules.get(id);
    }

    /**
     * @param id identificador
     * @return cancha, o null si no existe
     */
    public Field getField(String id){
        return fields.get(id);
    }

    /** @return estadísticas registradas */
    public ArrayList<Stat> getStats(){
        return stats;
    }

    /** @return elementos de la cancha registrados */
    public ArrayList<FieldElement> getElements(){
        return elements;
    }

    /** @return identificadores de los países */
    public ArrayList<String> getCountryIds(){
        return new ArrayList<String>(countries.keySet());
    }

    /** @return identificadores de los perfiles */
    public ArrayList<String> getProfileIds(){
        return new ArrayList<String>(profiles.keySet());
    }

    /** @return identificadores de los tipos de partido */
    public ArrayList<String> getRuleIds(){
        return new ArrayList<String>(rules.keySet());
    }

    /** @return identificadores de las resoluciones */
    public ArrayList<String> getFieldIds(){
        return new ArrayList<String>(fields.keySet());
    }

    /** @return nombres de las estadísticas */
    public ArrayList<String> getStatNames(){
        ArrayList<String> names = new ArrayList<String>();
        for (int i = 0; i < stats.size(); i++){
            names.add(stats.get(i).getName());
        }
        return names;
    }
}
