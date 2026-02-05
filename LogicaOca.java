import java.sql.*;
import java.util.HashMap;

public class LogicaOca {
    private String url = "jdbc:mysql://localhost:3306/trabajo";
    private String user = "root";
    private String pass = ""; 

    // 👇👇👇 ¡ESTA ES LA PARTE QUE TE FALTA! 👇👇👇
    // ---------------------------------------------------------
    public int[] obtenerEstadisticas(int idJugador) {
        int[] stats = {0, 0};
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);
            
            // 1. Contar Partidas
            PreparedStatement ps1 = con.prepareStatement("SELECT COUNT(*) FROM detallespartida WHERE IdJugador = ?");
            ps1.setInt(1, idJugador);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) stats[0] = rs1.getInt(1);
            
            // 2. Contar Victorias
            PreparedStatement ps2 = con.prepareStatement("SELECT COUNT(*) FROM partidas WHERE IdGanador = ?");
            ps2.setInt(1, idJugador);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) stats[1] = rs2.getInt(1);

            con.close();
        } catch (Exception e) { e.printStackTrace(); }
        return stats;
    }
    // ---------------------------------------------------------

    // Resto de métodos (Posiciones, Turnos, Jugar...)
    public HashMap<Integer, Integer> obtenerPosiciones(int idPartida) {
        HashMap<Integer, Integer> mapa = new HashMap<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);
            PreparedStatement ps = con.prepareStatement("SELECT IdJugador, Casilla FROM detallespartida WHERE IdPartida = ?");
            ps.setInt(1, idPartida);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) mapa.put(rs.getInt("IdJugador"), rs.getInt("Casilla"));
            con.close();
        } catch (Exception e) { e.printStackTrace(); }
        return mapa;
    }

    public boolean esMiTurno(int idJugador, int idPartida) {
        boolean esMio = false;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);
            PreparedStatement ps = con.prepareStatement("SELECT TurnoDe FROM partidas WHERE IdPartida = ?");
            ps.setInt(1, idPartida);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) if (rs.getInt("TurnoDe") == idJugador) esMio = true;
            con.close();
        } catch (Exception e) { e.printStackTrace(); }
        return esMio;
    }

    public int obtenerEstadoPartida(int idPartida) {
        int estado = 0; 
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);
            PreparedStatement ps = con.prepareStatement("SELECT Estado FROM partidas WHERE IdPartida = ?");
            ps.setInt(1, idPartida);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) estado = rs.getInt("Estado");
            con.close();
        } catch (Exception e) { e.printStackTrace(); }
        return estado;
    }

    public int jugarTurno(int idJugador, int idPartida) {
        if (obtenerEstadoPartida(idPartida) == 1) return -1;
        if (!esMiTurno(idJugador, idPartida)) return -1;

        int nuevaPos = 0;
        int dado = 0;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);

            PreparedStatement ps1 = con.prepareStatement("SELECT Casilla FROM detallespartida WHERE IdJugador = ? AND IdPartida = ?");
            ps1.setInt(1, idJugador);
            ps1.setInt(2, idPartida);
            ResultSet rs1 = ps1.executeQuery();
            int posActual = 1;
            if (rs1.next()) posActual = rs1.getInt("Casilla");

            dado = (int)(Math.random() * 6) + 1;
            nuevaPos = posActual + dado;
            
            if (nuevaPos > 63) nuevaPos = 63 - (nuevaPos - 63);

            // Reglas Oca simplificadas para no alargar
            if (nuevaPos == 5) nuevaPos = 9;
            else if (nuevaPos == 9) nuevaPos = 14;
            else if (nuevaPos == 14) nuevaPos = 18;
            else if (nuevaPos == 18) nuevaPos = 23;
            else if (nuevaPos == 26) nuevaPos = 53;
            else if (nuevaPos == 42) nuevaPos = 30;
            else if (nuevaPos == 52) nuevaPos = 52; // Cárcel (visual)
            else if (nuevaPos == 58) nuevaPos = 1;   // Calavera

            PreparedStatement psUp = con.prepareStatement("UPDATE detallespartida SET Casilla = ? WHERE IdJugador = ? AND IdPartida = ?");
            psUp.setInt(1, nuevaPos);
            psUp.setInt(2, idJugador);
            psUp.setInt(3, idPartida);
            psUp.executeUpdate();

            if (nuevaPos == 63) {
                PreparedStatement psFin = con.prepareStatement("UPDATE partidas SET Estado = 1, IdGanador = ? WHERE IdPartida = ?");
                psFin.setInt(1, idJugador);
                psFin.setInt(2, idPartida);
                psFin.executeUpdate();
            } else {
                PreparedStatement psRival = con.prepareStatement("SELECT IdJugador FROM detallespartida WHERE IdPartida = ? AND IdJugador <> ? LIMIT 1");
                psRival.setInt(1, idPartida);
                psRival.setInt(2, idJugador);
                ResultSet rsRival = psRival.executeQuery();
                if (rsRival.next()) {
                    int idRival = rsRival.getInt("IdJugador");
                    PreparedStatement psTurno = con.prepareStatement("UPDATE partidas SET TurnoDe = ? WHERE IdPartida = ?");
                    psTurno.setInt(1, idRival);
                    psTurno.setInt(2, idPartida);
                    psTurno.executeUpdate();
                }
            }
            con.close();
        } catch (Exception e) { e.printStackTrace(); }
        return dado;
    }
}