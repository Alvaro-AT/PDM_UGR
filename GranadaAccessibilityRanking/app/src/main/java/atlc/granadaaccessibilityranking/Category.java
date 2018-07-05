package atlc.granadaaccessibilityranking;


public class Category
{
    //Atributos necesarios de las valoraciones
    private String categoryID;
    private String nombre;
    private String comentario;
    private double valoracion;

    public Category() {
        super();
    }

    public Category(String categoryID, String nombre, String comentario, double valoracion) {

        super();
        this.categoryID = categoryID;
        this.nombre = nombre;
        this.comentario = comentario;
        this.valoracion = valoracion;

    }

    public String getCategoryID() {
        return categoryID;
    }

    public String getNombre() {
        return nombre;
    }

    public String getComentario(){ return comentario; }

    public double getValoracion() {
        return valoracion;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public void setValoracion(double valoracion) {
        this.valoracion = valoracion;
    }

}

