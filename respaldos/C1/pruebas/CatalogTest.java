package src.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas del catálogo de todo lo que se puede escoger.
 */
public class CatalogTest
{
    private Catalog catalog;

    /** Catálogo con lo registrado. */
    @BeforeEach
    public void setUp()
    {
        catalog = new Catalog();
    }

    /** Encuentra un personaje por su nombre. */
    @Test
    public void shouldFindACountryById() throws POOBSoccerException
    {
        assertEquals("Nigeria", catalog.getCountry("Nigeria").getId());
    }

    /** Un personaje que no existe lanza la excepción del juego. */
    @Test
    public void shouldRejectAnUnknownCountry()
    {
        try{
            catalog.getCountry("Brasil");
            fail("Debió rechazar un personaje que no existe");
        }
        catch (POOBSoccerException e){
            assertTrue(e.getMessage().contains("Brasil"));
        }
    }

    /** Una estadística que no existe lanza la excepción del juego. */
    @Test
    public void shouldRejectAnUnknownStat()
    {
        try{
            catalog.getStat("Suerte");
            fail("Debió rechazar una estadística que no existe");
        }
        catch (POOBSoccerException e){
            assertTrue(e.getMessage().contains("Suerte"));
        }
    }

    /** Hay cuatro personajes, cuatro perfiles, dos tipos de partido, tres resoluciones y cuatro estadísticas. */
    @Test
    public void shouldHaveEverythingTheStatementAsksFor()
    {
        assertEquals(4, catalog.getCountryIds().size());
        assertEquals(4, catalog.getProfileIds().size());
        assertEquals(2, catalog.getRuleIds().size());
        assertEquals(3, catalog.getFieldIds().size());
        assertEquals(4, catalog.getStatNames().size());
    }

    /** Las listas salen en el orden de registro. */
    @Test
    public void shouldKeepRegistrationOrder()
    {
        assertEquals("Corea del Sur", catalog.getCountryIds().get(0));
        assertEquals("500x400", catalog.getFieldIds().get(0));
    }
}
