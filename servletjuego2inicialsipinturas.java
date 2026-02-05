import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*; 

public class ServletJuego extends HttpServlet {

    // --- PARTE 1: MOSTRAR EL TABLERO (GET) ---
    public void doGet(HttpServletRequest req, HttpServletResponse res) 
    throws IOException, ServletException {
        
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        // 1. PREGUNTAMOS A LA BASE DE DATOS DÓNDE ESTÁ EL JUGADOR
        LogicaOca miJuego = new LogicaOca();
        int casillaActual = miJuego.obtenerPosicion(1, 1); // Jugador 1, Partida 1

        // 2. EMPEZAMOS EL HTML
        out.println("<html>");
        out.println("<head><title>Juego de la Oca</title>");
        
        // ESTILOS CSS (Para que se vea bonito)
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; text-align: center; background-color: #f4f4f4; }");
        out.println("h1 { color: #333; }");
        // El contenedor del tablero
        out.println(".tablero { display: flex; flex-wrap: wrap; width: 920px; margin: 20px auto; border: 5px solid #333; background: white; }");
        // Cada casilla
        out.println(".casilla { width: 100px; height: 100px; border: 1px solid #ccc; position: relative; box-sizing: border-box; display: flex; justify-content: flex-start; align-items: flex-start; padding: 5px; font-weight: bold; }");
        // Colores especiales
        out.println(".oca { background-color: #fff176; }"); // Amarillo
        out.println(".meta { background-color: #81c784; }"); // Verde
        // La ficha del jugador (Bola roja)
        out.println(".ficha { width: 50px; height: 50px; background-color: red; border-radius: 50%; border: 3px solid white; position: absolute; top: 25px; left: 25px; box-shadow: 2px 2px 5px rgba(0,0,0,0.5); }");
        // Botón
        out.println(".boton { padding: 15px 30px; font-size: 20px; background-color: #2196F3; color: white; border: none; cursor: pointer; border-radius: 5px; margin-top: 20px; }");
        out.println(".boton:hover { background-color: #0b7dda; }");
        out.println("</style>");
        out.println("</head>");
        
        out.println("<body>");
        out.println("<h1>Tablero de la Oca</h1>");
        out.println("<h3>Estás en la casilla: " + casillaActual + "</h3>");
        
        // 3. GENERAMOS EL TABLERO (BUCLE DE 63 CASILLAS)
        out.println("<div class='tablero'>");
        
        for (int i = 1; i <= 63; i++) {
            String claseColor = "";
            
            // Pintamos de amarillo las Ocas (múltiplos de 9 habituales) y verde la meta
            if (i % 9 == 0 || i == 5 || i == 14 || i == 23 || i == 27 || i == 32 || i == 36 || i == 41 || i == 45 || i == 50 || i == 54 || i == 59) {
                claseColor = " oca";
            }
            if (i == 63) claseColor = " meta";

            out.println("<div class='casilla" + claseColor + "'>");
            out.println(i); // Número de la casilla
            
            // SI EL JUGADOR ESTÁ EN ESTA CASILLA, PINTAMOS LA FICHA
            if (i == casillaActual) {
                out.println("<div class='ficha'></div>");
            }
            
            out.println("</div>"); // Cerramos div casilla
        }
        
        out.println("</div>"); // Cerramos div tablero
        
        // 4. EL BOTÓN PARA TIRAR
        out.println("<form action='jugar' method='POST'>");
        out.println("<input type='submit' value='¡LANZAR DADO!' class='boton'>");
        out.println("</form>");

        out.println("</body></html>");
    }
    
    // --- PARTE 2: MOVER FICHA (POST) ---
    public void doPost(HttpServletRequest req, HttpServletResponse res) 
    throws IOException, ServletException {
    
        // 1. Instanciamos tu lógica
        LogicaOca miJuego = new LogicaOca();
        
        // 2. Ejecutamos el turno (Jugador 1, Partida 1)
        // Esto conectará a la BD, tirará el dado y hará el UPDATE
        miJuego.jugarTurno(1, 1); 
        
        // 3. Volvemos a cargar la página para ver el cambio
        doGet(req, res);
    }
}
