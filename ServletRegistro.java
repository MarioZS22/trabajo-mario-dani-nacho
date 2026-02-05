import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

public class ServletRegistro extends HttpServlet {
    
    public void doPost(HttpServletRequest req, HttpServletResponse res) 
    throws IOException, ServletException {
        
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        String usuario = req.getParameter("usuario");
        String pass = req.getParameter("pass");
        
        // Validación básica
        if(usuario == null || usuario.trim().isEmpty() || pass == null) {
            out.println("<h1>❌ Error: Debes poner usuario y contraseña</h1>");
            return;
        }
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/trabajo", "root", "");
            
            // 1. Intentamos insertar el nuevo jugador
            PreparedStatement ps = con.prepareStatement("INSERT INTO jugadores (Nombre, Password) VALUES (?, ?)");
            ps.setString(1, usuario);
            ps.setString(2, pass);
            
            int filas = ps.executeUpdate();
            
            if (filas > 0) {
                // ÉXITO: Lo mandamos al login para que entre
                res.sendRedirect("login.html");
            }
            
            con.close();
            
        } catch (SQLIntegrityConstraintViolationException e) {
            // ERROR ESPECÍFICO: Si el nombre ya existe (porque pusimos UNIQUE en la BD)
            out.println("<body style='background:#333; color:white; text-align:center;'>");
            out.println("<h1>⚠️ Ese nombre de usuario YA EXISTE</h1>");
            out.println("<h3>Prueba con otro (ej: " + usuario + "123)</h3>");
            out.println("<a href='registro.html' style='color:gold'>Volver a intentar</a>");
            out.println("</body>");
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }
}