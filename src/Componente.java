import java.math.BigDecimal;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Componente {
    private String cservidor;
    private String cbase;
    private String cusuario;
    private String cpassword;

    public Componente() {
    }

    public Componente(String cservidor, String cbase) {
        this.cservidor = cservidor;
        this.cbase = cbase;
    }

    public Componente(String cservidor, String cbase, String cusuario, String cpassword) {
        this.cservidor = cservidor;
        this.cbase = cbase;
        this.cusuario = cusuario;
        this.cpassword = cpassword;
    }

    public String getCservidor() {
        return cservidor;
    }

    public void setCservidor(String cservidor) {
        this.cservidor = cservidor;
    }

    public String getCbase() {
        return cbase;
    }

    public void setCbase(String cbase) {
        this.cbase = cbase;
    }

    public String getCusuario() {
        return cusuario;
    }

    public void setCusuario(String cusuario) {
        this.cusuario = cusuario;
    }

    public void setCpassword(String cpassword) {
        this.cpassword = cpassword;
    }

    private String generarURL(){
        String url = null;
        if(this.cservidor != null && this.cbase != null){
            if(this.cusuario != null && this.cpassword != null){
                url = "jdbc:sqlserver://" + this.cservidor + ";databaseName=" + this.cbase + ";user=" + this.cusuario + ";password=" + this.cpassword + ";";
            }else{
                url = "jdbc:sqlserver://" + this.cservidor + ";databaseName=" + this.cbase + ";integratedSecurity=true";
            }
        }
        return url;
    }

    public int ejecutar(String sentencia) {
        int filas = ejecutar(sentencia, null, false);
        return filas;
    }

    public int ejecutar(String sentencia, List<Object> parametros) {
        int filas = ejecutar(sentencia, parametros, false);
        return filas;
    }

    public int ejecutar(String sentencia, List<Object> parametros, boolean sw){
        int filas = 0;

        try(Connection cnx = DriverManager.getConnection(generarURL())){
            if(! cnx.isClosed()){
                ParameterMetaData pmd = null;
                if(parametros == null && ! sw){
                    Statement stmt = cnx.createStatement();
                    filas = stmt.executeUpdate(sentencia);
                }else if(parametros != null && ! sw){
                    PreparedStatement pstmt = cnx.prepareStatement(sentencia);
                    pmd = pstmt.getParameterMetaData();
                    if(pmd.getParameterCount() == parametros.size()){
                        for(int i = 0; i < parametros.size(); i++){
                            pstmt.setObject((i + 1), parametros.get(i));
                        }
                        filas = pstmt.executeUpdate();
                    }else{
                        throw new Exception("El número de parámetros no coincide con la sentencia");
                    }
                }else if(parametros == null && sw){
                    CallableStatement cstm = cnx.prepareCall("{call " + sentencia + "}");
                    filas = cstm.executeUpdate();
                }else if(parametros != null && sw){
                    CallableStatement cstm = cnx.prepareCall("{call " + sentencia + "}");
                    pmd = cstm.getParameterMetaData();
                    if(pmd.getParameterCount() == parametros.size()){
                        for(int i = 0; i < parametros.size(); i++){
                            cstm.setObject((i + 1), parametros.get(i));
                        }
                        filas = cstm.executeUpdate();
                    }else{
                        throw new Exception("El número de parámetros no coincide");
                    }
                }else{
                    throw new Exception("No ha introducido unos valores correctos de ejecución");
                }
            }else{
                throw new Exception("Conexión cerrada");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filas;
    }

    public Object consultaEscalar(String sentencia) {
        Object item = consultaEscalar(sentencia, null, false);
        return item;
    }

    public Object consultaEscalar(String sentencia, List parametros) {
        Object item = consultaEscalar(sentencia, parametros, false);
        return item;
    }

    public Object consultaEscalar(String sentencia, List parametros, boolean sw){
        Object item = null;

        try(Connection cnx = DriverManager.getConnection(generarURL())){
            if(! cnx.isClosed()){
                ResultSet rs;
                ParameterMetaData pmd;
                if(parametros == null && ! sw){
                    Statement stmt = cnx.createStatement();
                    rs = stmt.executeQuery(sentencia);
                    rs.next();
                    item = rs.getObject(1);
                }else if(parametros != null && ! sw){
                    PreparedStatement pstmt = cnx.prepareStatement(sentencia);
                    pmd = pstmt.getParameterMetaData();
                    if(pmd.getParameterCount() == parametros.size()){
                        for(int i = 0; i < parametros.size(); i++){
                            pstmt.setObject((i + 1), parametros.get(i));
                        }
                        rs = pstmt.executeQuery();
                        rs.next();
                        item = rs.getObject(1);
                    }else{
                        throw new Exception("El número de parámetros no coincide");
                    }
                }else if(parametros == null && sw){
                    CallableStatement cstm = cnx.prepareCall("{call " + sentencia + "}");
                    rs = cstm.executeQuery();
                    rs.next();
                    item = rs.getObject(1);
                }else if(parametros != null && sw){
                    CallableStatement cstm = cnx.prepareCall("{call " + sentencia + "}");
                    pmd = cstm.getParameterMetaData();
                    if(pmd.getParameterCount() == parametros.size()){
                        for(int i = 0; i < parametros.size(); i++){
                            cstm.setObject((i + 1), parametros.get(i));
                        }
                        rs = cstm.executeQuery();
                        rs.next();
                        item = rs.getObject(1);
                    }else{
                        throw new Exception("El número de parámetros no coincide con la sentencia");
                    }
                }else{
                    throw new Exception("No ha introducido unos valores correctos de ejecución");
                }
            }else{
                throw new Exception("La conexión a la base de datos se ha cerrado");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return item;
    }

    public List consultar(String sentencia) {
        List<Map> resultado = consultar(sentencia, null, false);
        return resultado;
    }

    public List consultar(String sentencia, List parametros) {
        List<Map> resultado = consultar(sentencia, parametros, false);
        return resultado;
    }

    public List consultar(String sentencia, List parametros, boolean sw){
        List<Map> resultado = new ArrayList<>();

        try(Connection cnx = DriverManager.getConnection(generarURL())){
            ResultSet rs;
            ParameterMetaData pmd;
            if(! cnx.isClosed()){
                if(parametros == null && ! sw){
                    Statement stmt = cnx.createStatement();
                    rs = stmt.executeQuery(sentencia);
                    ResultSetMetaData rsmd = rs.getMetaData();
                    while(rs.next()){
                        Map<String, Object> fila = new HashMap<>();
                        for(int i = 1; i <= rsmd.getColumnCount(); i++){
                            fila.put(rsmd.getColumnName(i), rs.getObject(i));
                        }
                        resultado.add(fila);
                    }
                }else if(parametros != null && ! sw){
                    PreparedStatement pstmt = cnx.prepareStatement(sentencia);
                    pmd = pstmt.getParameterMetaData();
                    if(pmd.getParameterCount() == parametros.size()){
                        for(int i = 0; i < parametros.size(); i++){
                            pstmt.setObject((i + 1), parametros.get(i));
                        }
                        rs = pstmt.executeQuery();
                        ResultSetMetaData rsmd = rs.getMetaData();
                        while(rs.next()){
                            Map<String, Object> fila = new HashMap<>();
                            for(int i = 1; i <= rsmd.getColumnCount(); i++){
                                fila.put(rsmd.getColumnName(i), rs.getObject(i));
                            }
                            resultado.add(fila);
                        }
                    }else{
                        throw new Exception("El número de parámetros no coincide con la sentencia");
                    }
                }else if(parametros == null && sw){
                    CallableStatement cstm = cnx.prepareCall("{call " + sentencia + "}");
                    rs = cstm.executeQuery();
                    ResultSetMetaData rsmd = rs.getMetaData();
                    while(rs.next()){
                        Map<String, Object> fila = new HashMap<>();
                        for(int i = 1; i <= rsmd.getColumnCount(); i++){
                            fila.put(rsmd.getColumnName(i), rs.getObject(i));
                        }
                        resultado.add(fila);
                    }
                }else if(parametros != null && sw){
                    CallableStatement cstm = cnx.prepareCall("{call " + sentencia + "}");
                    pmd = cstm.getParameterMetaData();
                    if(pmd.getParameterCount() == parametros.size()){
                        for(int i = 0; i < parametros.size(); i++){
                            cstm.setObject((i + 1), parametros.get(i));
                        }
                        rs = cstm.executeQuery();
                        ResultSetMetaData rsmd = rs.getMetaData();
                        while(rs.next()){
                            Map<String, Object> fila = new HashMap<>();
                            for(int i = 1; i <= rsmd.getColumnCount(); i++){
                                fila.put(rsmd.getColumnName(i), rs.getObject(i));
                            }
                            resultado.add(fila);
                        }
                    }else{
                        throw new Exception("El número de parámetros no coincide con la sentencia");
                    }
                }else{
                    throw new Exception("No ha introducido unos valores correctos de ejecución");
                }
            }else{
                throw new Exception("Conexión cerrada");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultado;
    }

    // Transaccion sin parámetros y con parámetros
    public boolean transaccion(List<Comando> comandos){
        boolean sw = true;

        try{
            Connection cnx = DriverManager.getConnection(generarURL());
            try{
                if(! cnx.isClosed()){
                    // Desactivar el autocommit
                    cnx.setAutoCommit(false);
                    //Recorrer el array de comandos
                    for(Comando c : comandos){
                        ejecutar(c.getSentencia(), c.getParametros(), c.getTipo());
                    }
                }else{
                    throw new Exception("Conexión con la base de datos cerrada");
                }
            } catch (Exception e) {
                cnx.rollback();
                sw = false;
                throw new Exception("Uno de los comandos no se ha ejecutado correctamente");
            }
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }

        return sw;
    }

    /*
        Acepta un Arraylist<Comando> con las sentencias o llamadas simples a procedimientos
        un ArrayList<Object> con los parámetros de esas funciones
        Hay que hacer las transacciones con y sin parámetros
        Devuelve True o False en función de si funciona el Commit

        La clase comando tiene 3 variables
        String sentencia
        ArrayList<Object> parámetros
        SQLComponente.comandoTipo tipo (Un enumerado para especificar si es sentencia o procedimiento)
     */


    /*
        metodo generarURL

        ejecutar sentencias que no devuelven filas - normal, parametros, procedimiento

        Sentencias que devuelven un único valor

        Sentencias que devuelven un conjunto de valores

        Procedimiento acepta una lista de comandos, los ejecuta como una transacción(sentencia, parametros, procedimiento)

        Vamos a crear 2 eventos. ANTES DE ACTUALIZAR y DESPUÉS DE ACTUALIZAR. Se les va a asociar a los INSERT, UPDATE y DELETE. Ponerle Oyentes.

        El de ANTES DE ACTUALIZAR se ejecuta antes del execute. Al disparar el oyente me permite anular la operación

        El de DESPUÉS DE ACTUALIZAR, facilita al oyente información sobre la operación, si se ha ejecutado bien, número de filas afectadas, etc.

        Para las transacciones utilizar el procedimiento ALTAMOV_SP de Bankline
     */
}
