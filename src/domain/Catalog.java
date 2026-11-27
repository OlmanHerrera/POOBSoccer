package src.domain;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Registro de personajes, perfiles, tipos de partido, canchas, estadísticas y elementos de la cancha.
 */
public class Catalog{
    private static final String FILE = "/catalogo.txt";
    private static final String CLASS_PREFIX = "src.domain.";
    private static final String SEPARATOR = ";";
    private static final String COMMENT = "#";
    private static final String SIZE_SEPARATOR = "x";
    private static final String COUNTRY = "pais";
    private static final String PROFILE = "perfil";
    private static final String RULES = "reglas";
    private static final String FIELD = "cancha";
    private static final String STAT = "estadistica";
    private static final String ELEMENT = "elemento";

    private LinkedHashMap<String, CountryDefinition> countries;
    private LinkedHashMap<String, PlayerStrategy> profiles;
    private LinkedHashMap<String, MatchRules> rules;
    private LinkedHashMap<String, Field> fields;
    private ArrayList<Stat> stats;
    private ArrayList<FieldElement> elements;

    /**
     * Crea el catálogo con lo que dice catalogo.txt.
     * @throws POOBSoccerException si el archivo no existe o alguna línea está mal escrita
     */
    public Catalog() throws POOBSoccerException{
        countries = new LinkedHashMap<String, CountryDefinition>();
        profiles = new LinkedHashMap<String, PlayerStrategy>();
        rules = new LinkedHashMap<String, MatchRules>();
        fields = new LinkedHashMap<String, Field>();
        stats = new ArrayList<Stat>();
        elements = new ArrayList<FieldElement>();
        load();
    }

    /**
     * Lee catalogo.txt línea por línea; se saltan las vacías y las que empiezan con #.
     * @throws POOBSoccerException si el archivo no existe, no se puede leer o tiene una línea mal escrita
     */
    private void load() throws POOBSoccerException{
        InputStream in = Catalog.class.getResourceAsStream(FILE);
        if (in == null){
            throw new POOBSoccerException(POOBSoccerException.NO_CATALOG + FILE);
        }
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line = reader.readLine();
            int number = 1;
            while (line != null){
                String text = line.trim();
                if (!text.isEmpty() && !text.startsWith(COMMENT)){
                    register(text.split(SEPARATOR), number);
                }
                line = reader.readLine();
                number++;
            }
            reader.close();
        }
        catch (IOException e){
            throw new POOBSoccerException(POOBSoccerException.CANNOT_READ + FILE, e);
        }
    }

    /**
     * Registra una línea del catálogo.
     * @param parts partes de la línea, separadas por ;
     * @param number número de la línea, para el mensaje de error
     * @throws POOBSoccerException si el tipo no existe o faltan datos
     */
    private void register(String[] parts, int number) throws POOBSoccerException{
        String kind = parts[0].trim();
        if (kind.equals(COUNTRY)){
            checkParts(parts, 4, number);
            AbilityStrategy power = (AbilityStrategy) create(parts[2], AbilityStrategy.class, number);
            addCountry(new CountryDefinition(parts[1].trim(), power, parts[3].trim()));
        }
        else if (kind.equals(PROFILE)){
            checkParts(parts, 2, number);
            addProfile((PlayerStrategy) create(parts[1], PlayerStrategy.class, number));
        }
        else if (kind.equals(RULES)){
            checkParts(parts, 2, number);
            addRules((MatchRules) create(parts[1], MatchRules.class, number));
        }
        else if (kind.equals(STAT)){
            checkParts(parts, 2, number);
            addStat((Stat) create(parts[1], Stat.class, number));
        }
        else if (kind.equals(ELEMENT)){
            checkParts(parts, 2, number);
            addElement((FieldElement) create(parts[1], FieldElement.class, number));
        }
        else if (kind.equals(FIELD)){
            checkParts(parts, 3, number);
            int width = toInt(parts[1], number);
            int height = toInt(parts[2], number);
            addField(new Field(width + SIZE_SEPARATOR + height, width, height));
        }
        else{
            throw lineError(number, POOBSoccerException.UNKNOWN_KIND + kind);
        }
    }

    /**
     * Crea con reflexión un objeto de src.domain a partir del nombre de su clase.
     * @param className nombre de la clase, por ejemplo BlueAuraShot
     * @param kind tipo que debe tener el objeto, por ejemplo AbilityStrategy
     * @param number número de la línea, para el mensaje de error
     * @return el objeto creado con el constructor sin parámetros de la clase
     * @throws POOBSoccerException si la clase no existe, no se puede crear o no es del tipo pedido
     */
    private Object create(String className, Class<?> kind, int number) throws POOBSoccerException{
        String name = className.trim();
        Object created;
        try{
            created = Class.forName(CLASS_PREFIX + name).getDeclaredConstructor().newInstance();
        }
        catch (ClassNotFoundException e){
            throw lineError(number, POOBSoccerException.UNKNOWN_CLASS + name);
        }
        catch (ReflectiveOperationException e){
            throw lineError(number, POOBSoccerException.CANNOT_CREATE + name);
        }
        if (!kind.isInstance(created)){
            throw lineError(number, name + POOBSoccerException.WRONG_KIND + kind.getSimpleName());
        }
        return created;
    }

    private void checkParts(String[] parts, int needed, int number) throws POOBSoccerException{
        if (parts.length < needed){
            throw lineError(number, POOBSoccerException.MISSING_DATA);
        }
    }

    private int toInt(String text, int number) throws POOBSoccerException{
        try{
            return Integer.parseInt(text.trim());
        }
        catch (NumberFormatException e){
            throw lineError(number, POOBSoccerException.NOT_A_NUMBER + text.trim());
        }
    }

    private POOBSoccerException lineError(int number, String detail){
        return new POOBSoccerException(POOBSoccerException.CATALOG_LINE + number + ": " + detail);
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
     * @return un perfil nuevo de la clase registrada con ese nombre
     * @throws POOBSoccerException si no existe o no se puede crear
     */
    public PlayerStrategy getProfile(String id) throws POOBSoccerException{
        PlayerStrategy p = profiles.get(id);
        if (p == null){
            throw new POOBSoccerException(POOBSoccerException.UNKNOWN_PROFILE + id);
        }
        try{
            return p.getClass().getDeclaredConstructor().newInstance();
        }
        catch (ReflectiveOperationException e){
            throw new POOBSoccerException(POOBSoccerException.CANNOT_CREATE + p.getClass().getSimpleName(), e);
        }
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
