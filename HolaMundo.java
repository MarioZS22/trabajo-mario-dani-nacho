
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class HolaMundo extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws IOException, ServletException {

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Hola Mundo!</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Hola Mundo!</h1>");
        out.println("</body>");
        out.println("</html>");
    }
}
