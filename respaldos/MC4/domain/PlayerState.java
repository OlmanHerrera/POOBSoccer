package src.domain;

/**
 * Efecto activo sobre un jugador (normal, empujado, electrocutado...).
 * Cuenta su duración en ciclos del bucle y decide qué acciones se permiten.
 */
public abstract class PlayerState
{
    private int elapsedUpdates;

    /**
     * Crea el efecto con su contador en cero.
     */
    public PlayerState(){
        elapsedUpdates = 0;
    }

    /**
     * Método plantilla: cuenta un ciclo y, si el efecto venció, llama a onExpire.
     * @param p jugador que tiene el efecto
     * @param updatesPerSecond ciclos del bucle por segundo
     */
    public void update(Player p, int updatesPerSecond){
        // pendiente MC4
    }

    /**
     * Qué pasa cuando el efecto vence. Por defecto el jugador vuelve a la normalidad.
     * @param p jugador que tiene el efecto
     */
    protected void onExpire(Player p){
        // pendiente MC4
    }

    /** @return nombre del efecto para el HUD */
    public abstract String getName();

    /** @return duración del efecto en segundos */
    protected abstract double getDurationSeconds();

    /**
     * Se ejecuta cuando el efecto empieza.
     * @param p jugador que recibe el efecto
     */
    protected abstract void enter(Player p);

    /**
     * Se ejecuta cuando el efecto termina o es reemplazado.
     * @param p jugador que tenía el efecto
     */
    protected abstract void exit(Player p);

    /**
     * Decide qué acción se ejecuta realmente mientras dura el efecto.
     * @param a acción pedida
     * @return acción permitida, o null si no puede actuar
     */
    protected abstract Action filter(Action a);
}
