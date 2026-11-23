package src.domain;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Todo lo que se puede escoger o agregar al juego: personajes, perfiles de máquina,
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
        addCountry(new CountryDefinition("Corea del Sur", new BlueAuraShot()));
        addCountry(new CountryDefinition("Camerún", new LightningShot()));
        addCountry(new CountryDefinition("Nigeria", new TornadoShot()));
        addCountry(new CountryDefinition("Estados Unidos", new DeceptiveShot()));

        addProfile("Torpe", new ClumsyProfile());
        addProfile("Ofensiva", new OffensiveProfile());
        addProfile("Defensiva", new DefensiveProfile());
        addProfile("Experta", new ExpertProfile());

        addField(new Field("500x400", 500, 400));

        addElement(new Crossbar()); // el travesaño se revisa antes que el gol, como estaba en Match
        addElement(new Goal());
        // tipos de partido: pendiente MC3
        // estadísticas: pendiente MC6
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
     * @param id nombre del país
     * @return personaje registrado con ese nombre
     * @throws POOBSoccerException si no existe
     */
    public CountryDefinition getCountry(String id) throws POOBSoccerException{
        CountryDefinition c = countries.get(id);
        if (c == null){
            throw new POOBSoccerException(POOBSoccerException.UNKNOWN_COUNTRY + id);
        }
        return c;
    }

    /**
     * @param id nombre del perfil
     * @return perfil de máquina registrado con ese nombre
     * @throws POOBSoccerException si no existe
     */
    public PlayerStrategy getProfile(String id) throws POOBSoccerException{
        PlayerStrategy p = profiles.get(id);
        if (p == null){
            throw new POOBSoccerException(POOBSoccerException.UNKNOWN_PROFILE + id);
        }
        return p;
    }

    /**
     * @param id nombre del tipo de partido
     * @return tipo de partido registrado con ese nombre
     * @throws POOBSoccerException si no existe
     */
    public MatchRules getRules(String id) throws POOBSoccerException{
        MatchRules r = rules.get(id);
        if (r == null){
            throw new POOBSoccerException(POOBSoccerException.UNKNOWN_RULES + id);
        }
        return r;
    }

    /**
     * @param id nombre de la resolución
     * @return cancha registrada con ese nombre
     * @throws POOBSoccerException si no existe
     */
    public Field getField(String id) throws POOBSoccerException{
        Field f = fields.get(id);
        if (f == null){
            throw new POOBSoccerException(POOBSoccerException.UNKNOWN_FIELD + id);
        }
        return f;
    }

    /** @return estadísticas registradas */
    public ArrayList<Stat> getStats(){
        return stats;
    }

    /** @return elementos de la cancha registrados */
    public ArrayList<FieldElement> getElements(){
        return elements;
    }

    /** @return nombres de los personajes */
    public ArrayList<String> getCountryIds(){
        return new ArrayList<String>(countries.keySet());
    }

    /** @return nombres de los perfiles de máquina */
    public ArrayList<String> getProfileIds(){
        return new ArrayList<String>(profiles.keySet());
    }

    /** @return nombres de los tipos de partido */
    public ArrayList<String> getRuleIds(){
        return new ArrayList<String>(rules.keySet());
    }

    /** @return nombres de las resoluciones */
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
