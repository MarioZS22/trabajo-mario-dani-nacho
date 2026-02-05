import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class ServletLogin extends HttpServlet {
    
    public void doPost(HttpServletRequest req, HttpServletResponse res) 
    throws IOException, ServletException {
        
        String usuario = req.getParameter("usuario");
        String pass = req.getParameter("pass");
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/trabajo", "root", "");
            
            // Comprobamos si existe el usuario
            PreparedStatement ps = con.prepareStatement("SELECT IdJugador FROM jugadores WHERE Nombre = ? AND Password = ?");
            ps.setString(1, usuario);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // ¡LOGIN CORRECTO!
                int idEncontrado = rs.getInt("IdJugador");
                
                // --- MAGIA DE SESIONES ---
                // Guardamos el ID en la "mochila" del servidor. 
                // Mientras no cierre el navegador, el servidor sabrá quién es.
                HttpSession session = req.getSession();
                session.setAttribute("idJugador", idEncontrado);
                session.setAttribute("nombreJugador", usuario);
                
                // Lo mandamos al MENU PRINCIPAL (Lobby)
                res.sendRedirect("menu");
            } else {
                // Login fallido
                res.sendRedirect("login.html");
            }
            con.close();
            
        } catch (Exception e) { e.printStackTrace(); }
    }
}