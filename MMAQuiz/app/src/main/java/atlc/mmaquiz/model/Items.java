package atlc.mmaquiz.model;

/**
 * Created by Alvaro on 26/4/17.
 */

public class Items
{
    String nombre;
    String puntuacion;

    public Items()
    { }

    public Items(String nombre, String puntuacion)
    {
        this.nombre = nombre;
        this.puntuacion = puntuacion;
    }

    public String getNombre()
    {
        return nombre;
    }

    public String getPuntuacion()
    {
        return puntuacion;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public void setPuntuacion(String puntuacion)
    {
        this.puntuacion = puntuacion;
    }
}
