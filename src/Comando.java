import java.util.ArrayList;
import java.util.List;

public class Comando {
    private String sentencia;
    private List<Object> parametros;
    private boolean tipo;

    public Comando(){}

    public Comando(String sentencia, List parametros, boolean tipo){
        this.sentencia = sentencia;
        this.parametros = parametros;
        this.tipo = tipo;
    }

    public String getSentencia() {
        return sentencia;
    }

    public void setSentencia(String sentencia) {
        this.sentencia = sentencia;
    }

    public List<Object> getParametros() {
        return parametros;
    }

    public void setParametros(ArrayList<Object> parametros) {
        this.parametros = parametros;
    }

    public boolean getTipo() {
        return tipo;
    }

    public void setTipo(boolean tipo) {
        this.tipo = tipo;
    }
}
