package src.domain;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Registro de personajes, perfiles, tipos de partido, canchas, estadísticas y elementos de la cancha.
 */
public class Catalog{
    private LinkedHashMap<String, CountryDefinition> countries;
    private LinkedHashMap<String, PlayerStrategy> profiles;
    private LinkedHashMap<String, MatchRules> rules;
    private LinkedHashMap<String, Field> fields;
    private ArrayList<Stat> stats;
    private ArrayList<FieldElement> elements;

    /**
     * Crea el catálogo con todo lo registrado.
     */
    public Catalog(){
        countries = new LinkedHashMap<String, CountryDefinition>();
        profiles = new LinkedHashMap<String, PlayerStrategy>();
        rules = new LinkedHashMap<String, MatchRules>();
        fields = new LinkedHashMap<String, Field>();
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

        addProfile(new ClumsyProfile());
        addProfile(new OffensiveProfile());
        addProfile(new DefensiveProfile());
        addProfile(new ExpertProfile());

        addField(new Field("500x400", 500, 400));
        addField(new Field("625x500", 625, 500));
        addField(new Field("750x600", 750, 600));

        addElement(new Crossbar()); // va antes que el arco
        addElement(new Goal());

        addRules(new TimedRules());
        addRules(new GoalsRules());

        addStat(new SpeedStat());
        addStat(new KickStat());
        addStat(new JumpStat());
        addStat(new SurvivalStat());
    }

    private void addCountry(CountryDefinition c){
        countries.put(c.getId(), c);
    }

    private void addProfile(PlayerStrategy p){
        profiles.put(p.getId(), p);
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

    /**
     * @param name nombre de la estadística
     * @return estadística registrada con ese nombre
     * @throws POOBSoccerException si no existe
     */
    public Stat getStat(String name) throws POOBSoccerException{
        for (int i = 0; i < stats.size(); i++){
            if (stats.get(i).getName().equals(name)){
                return stats.get(i);
            }
        }
        throw new POOBSoccerException(POOBSoccerException.UNKNOWN_STAT + name);
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
