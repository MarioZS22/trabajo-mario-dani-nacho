import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class ServletMenu extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res) 
    throws IOException, ServletException {
        
        HttpSession session = req.getSession();
        Integer miId = (Integer) session.getAttribute("idJugador");
        String miNombre = (String) session.getAttribute("nombreJugador");
        
        if (miId == null) { res.sendRedirect("login.html"); return; } 

        // 1. OBTENER ESTADÍSTICAS
        LogicaOca logica = new LogicaOca();
        int[] stats = logica.obtenerEstadisticas(miId); // ESTO YA DEBERÍA FUNCIONAR SI ARREGLASTE LOGICAOCA
        int totalPartidas = stats[0];
        int totalVictorias = stats[1];

        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();
        
        out.println("<html><head><title>Menú Oca</title>");
        out.println("<style>");
        out.println("body{font-family:'Verdana'; text-align:center; background:#263238; color:white; margin:0;}");
        out.println(".stats-bar { background: #212121; padding: 15px; border-bottom: 2px solid #ffeb3b; display: flex; justify-content: space-around; align-items: center; }");
        out.println(".stat-box { font-size: 18px; font-weight: bold; }");
        out.println(".number { color: #ffeb3b; font-size: 22px; }");
        out.println(".card{background:#37474f; border:1px solid #546e7a; margin:10px; padding:20px; width:220px; border-radius:10px; box-shadow:0 4px 8px rgba(0,0,0,0.3); vertical-align:top;}");
        out.println(".container{display:flex; flex-wrap:wrap; justify-content:center;}");
        out.println(".btn{padding:10px; cursor:pointer; font-weight:bold; border:none; border-radius:5px;}");
        out.println(".btn-join{background:#66bb6a; color:white;}");
        out.println(".btn-del{background:#d32f2f; color:white; margin-left:5px;}");
        out.println(".btn-create{background:#0277bd; color:white; font-size:18px; padding:15px 30px; margin:20px;}");
        out.println("</style>");
        out.println("</head><body>");
        
        // BARRA DE ESTADÍSTICAS
        out.println("<div class='stats-bar'>");
        out.println("<div class='stat-box'>👤 " + miNombre + "</div>");
        out.println("<div class='stat-box'>🎲 Partidas: <span class='number'>" + totalPartidas + "</span></div>");
        out.println("<div class='stat-box'>🏆 Victorias: <span class='number'>" + totalVictorias + "</span></div>");
        out.println("<a href='login.html' style='color:#ef5350; text-decoration:none; font-size:12px;'>Cerrar Sesión</a>");
        out.println("</div>");
        
        // BOTÓN CREAR
        out.println("<form action='menu' method='POST'>");
        out.println("<input type='hidden' name='accion' value='crear'>");
        out.println("<input type='submit' value='➕ CREAR NUEVA PARTIDA' class='btn btn-create'>");
        out.println("</form><hr style='border-color:#546e7a; width:80%;'>");
        
        // LISTAR PARTIDAS
        out.println("<h3>Partidas Disponibles</h3>");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/trabajo", "root", "");
            Statement st = con.createStatement();
            
            ResultSet rs = st.executeQuery("SELECT * FROM partidas ORDER BY IdPartida DESC");
            
            out.println("<div class='container'>");
            while(rs.next()) {
                int idPartida = rs.getInt("IdPartida");
                String nombrePartida = rs.getString("Nombre");
                int estado = rs.getInt("Estado"); 
                int idGanador = rs.getInt("IdGanador");

                String estiloCard = (estado == 1) ? "background:#263238; border:1px solid #37474f; opacity:0.8;" : "background:#37474f; border:1px solid #546e7a;";

                out.println("<div class='card' style='" + estiloCard + "'>");
                out.println("<h3>Partida #" + idPartida + "</h3>");
                out.println("<p style='color:#b0bec5; font-size:12px;'>" + nombrePartida + "</p>");

                if (estado == 1) {
                    if (idGanador == miId) {
                         out.println("<div style='color:#00e676; font-weight:bold; font-size:18px; margin-bottom:10px;'>🏆 ¡GANASTE!</div>");
                    } else if (idGanador == 0) {
                         out.println("<div style='color:grey;'>🏁 FINALIZADA</div>");
                    } else {
                         out.println("<div style='color:#ef5350; font-weight:bold; font-size:16px; margin-bottom:10px;'>❌ PERDISTE</div>");
                    }
                } else {
                    out.println("<form action='menu' method='POST' style='display:inline;'>");
                    out.println("<input type='hidden' name='accion' value='unirse'>");
                    out.println("<input type='hidden' name='idPartida' value='" + idPartida + "'>");
                    out.println("<input type='submit' value='ENTRAR' class='btn btn-join'>");
                    out.println("</form>");
                }

                out.println("<form action='menu' method='POST' style='display:inline;'>");
                out.println("<input type='hidden' name='accion' value='borrar'>");
                out.println("<input type='hidden' name='idPartida' value='" + idPartida + "'>");
                out.println("<input type='submit' value='🗑️' class='btn btn-del' onclick=\"return confirm('¿Borrar?');\">");
                out.println("</form>");
                
                out.println("</div>");
            }
            out.println("</div>");
            con.close();
        } catch(Exception e) { e.printStackTrace(); }
        
        out.println("</body></html>");
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) 
    throws IOException, ServletException {
        
        HttpSession session = req.getSession();
        Integer miId = (Integer) session.getAttribute("idJugador");
        String miNombre = (String) session.getAttribute("nombreJugador");
        String accion = req.getParameter("accion");
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/trabajo", "root", "");
            
            if ("crear".equals(accion)) {
                // 1. Creamos la partida
                PreparedStatement ps = con.prepareStatement("INSERT INTO partidas (Nombre, TurnoDe, Estado) VALUES (?, ?, 0)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, "Partida de " + miNombre);
                ps.setInt(2, miId);
                ps.executeUpdate();
                
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int nuevaPartida = rs.getInt(1);
                    
                    // 2. Metemos al jugador dentro
                    Statement st = con.createStatement();
                    st.executeUpdate("INSERT INTO detallespartida (IdPartida, IdJugador, Casilla) VALUES (" + nuevaPartida + ", " + miId + ", 1)");
                    
                    // 3. --- EL ARREGLO IMPORTANTE ---
                    // Guardamos la partida en sesión y mandamos a JUGAR (no al menú)
                    session.setAttribute("idPartidaActual", nuevaPartida);
                    res.sendRedirect("jugar");
                    con.close();
                    return; // ¡IMPORTANTE! Cortamos aquí para que no siga leyendo
                }
                
            } else if ("unirse".equals(accion)) {
                int idPartida = Integer.parseInt(req.getParameter("idPartida"));
                PreparedStatement ps = con.prepareStatement("INSERT IGNORE INTO detallespartida (IdPartida, IdJugador, Casilla) VALUES (?, ?, 1)");
                ps.setInt(1, idPartida);
                ps.setInt(2, miId);
                ps.executeUpdate();
                
                session.setAttribute("idPartidaActual", idPartida);
                res.sendRedirect("jugar");
                con.close();
                return;

            } else if ("borrar".equals(accion)) {
                int idPartida = Integer.parseInt(req.getParameter("idPartida"));
                PreparedStatement ps1 = con.prepareStatement("DELETE FROM detallespartida WHERE IdPartida = ?");
                ps1.setInt(1, idPartida);
                ps1.executeUpdate();
                PreparedStatement ps2 = con.prepareStatement("DELETE FROM partidas WHERE IdPartida = ?");
                ps2.setInt(1, idPartida);
                ps2.executeUpdate();
            }
            
            con.close();
        } catch(Exception e) { e.printStackTrace(); }
        
        // Si es borrar o hay error, volvemos al menú
        res.sendRedirect("menu");
    }
}