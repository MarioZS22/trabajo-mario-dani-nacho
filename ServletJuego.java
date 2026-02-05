import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.HashMap;

public class ServletJuego extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res) 
    throws IOException, ServletException {
        
        // SESIÓN
        HttpSession session = req.getSession();
        Integer miId = (Integer) session.getAttribute("idJugador");
        Integer idPartida = (Integer) session.getAttribute("idPartidaActual");
        String miNombre = (String) session.getAttribute("nombreJugador");

        if (miId == null || idPartida == null) {
            res.sendRedirect("login.html");
            return;
        }

        res.setContentType("text/html;charset=UTF-8");
        PrintWriter out = res.getWriter();

        LogicaOca miJuego = new LogicaOca();
        HashMap<Integer, Integer> posiciones = miJuego.obtenerPosiciones(idPartida);
        boolean esMiTurno = miJuego.esMiTurno(miId, idPartida);
        
        Object dadoObj = req.getAttribute("valorDado");
        String mensajeDado = (dadoObj == null) ? "-" : dadoObj.toString();

        // --- DETECTAR SI HAY GANADOR ---
        boolean juegoTerminado = false;
        boolean heGanadoYo = false;
        
        for (Integer idJug : posiciones.keySet()) {
            if (posiciones.get(idJug) == 63) {
                juegoTerminado = true;
                if (idJug.equals(miId)) heGanadoYo = true;
            }
        }
        // -------------------------------

        out.println("<html><head><title>Partida " + idPartida + "</title>");

        // SOLO RECARGAMOS SI EL JUEGO NO HA TERMINADO
        // Y si no es mi turno (para esperar al rival)
        if (!esMiTurno && !juegoTerminado) {
            out.println("<meta http-equiv='refresh' content='2'>");
        }

        out.println("<style>");
        out.println("body { font-family: 'Verdana', sans-serif; text-align: center; background-color: #263238; color: white; margin:0; padding-top:10px;}");
        out.println(".tablero { display: flex; flex-wrap: wrap; width: 920px; margin: 0 auto; border: 8px solid #455a64; background: #eceff1; color: black; }");
        out.println(".casilla { width: 100px; height: 100px; border: 1px solid #b0bec5; position: relative; box-sizing: border-box; display: flex; flex-direction: column; justify-content: space-between; padding: 5px; font-weight: bold; font-size: 13px; }");

        // Fichas
        out.println(".ficha-yo { width: 30px; height: 30px; background: #00e676; border-radius: 50%; border: 3px solid white; position: absolute; top: 5px; left: 5px; z-index: 20; box-shadow: 2px 2px 5px black; transition: top 0.5s, left 0.5s; }");
        out.println(".ficha-rival { width: 30px; height: 30px; background: #d50000; border-radius: 50%; border: 3px solid white; position: absolute; bottom: 5px; right: 5px; z-index: 10; box-shadow: 2px 2px 5px black; }");

        // Estilos especiales
        out.println(".oca { background-color: #fff59d; border: 2px solid #fbc02d; }");
        out.println(".posada { background-color: #4a148c; color: #e040fb; border: 3px solid #ea80fc; text-shadow: 0 0 5px #e040fb; }");
        out.println(".carcel { background: repeating-linear-gradient(90deg, #bdbdbd, #bdbdbd 10px, #424242 10px, #424242 15px); color: white; }");
        out.println(".calavera { background-color: #212121; color: white; border: 2px solid black; }");
        out.println(".meta { background-color: #66bb6a; color: white; }");

        // Botones
        out.println(".boton { padding: 15px 40px; font-size: 20px; background-color: #ffeb3b; color: #f57f17; font-weight:bold; border: none; cursor: pointer; border-radius: 50px; margin-top: 20px; box-shadow: 0 4px 0 #f9a825; }");
        out.println(".boton:active { box-shadow: 0 2px 0 #f9a825; transform: translateY(2px); }");
        out.println(".boton-disabled { padding: 15px 40px; font-size: 20px; background-color: #78909c; color: #cfd8dc; font-weight:bold; border: none; cursor: not-allowed; border-radius: 50px; margin-top: 20px; }");
        out.println(".btn-salir { position: fixed; top: 10px; right: 10px; background: #d32f2f; color: white; padding: 10px; text-decoration: none; border-radius: 5px; font-size: 12px; }");

        // ESTILO GANADOR
        out.println(".mensaje-fin { position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); padding: 40px; border-radius: 20px; z-index: 100; box-shadow: 0 0 50px black; text-align: center; border: 5px solid white; }");
        out.println(".gana { background: linear-gradient(45deg, #00c853, #64dd17); color: white; font-size: 40px; font-weight: bold; text-shadow: 2px 2px 0 #005005; }");
        out.println(".pierde { background: linear-gradient(45deg, #b71c1c, #f44336); color: white; font-size: 40px; font-weight: bold; text-shadow: 2px 2px 0 #5f0000; }");

        out.println("</style></head><body>");
        
        out.println("<a href='menu' class='btn-salir'>SALIR AL MENÚ</a>");
        
        // CABECERA
        String estado;
        if (juegoTerminado) {
            estado = "🏁 PARTIDA TERMINADA";
        } else {
            estado = esMiTurno ? "✅ ¡TE TOCA TIRAR!" : "⏳ TURNO DEL RIVAL...";
        }
        
        out.println("<h1>Partida #" + idPartida + " - " + miNombre + "</h1>");
        out.println("<h2>" + estado + " | Dado: " + mensajeDado + "</h2>");

        // --- MENSAJE DE VICTORIA/DERROTA ---
        if (juegoTerminado) {
            if (heGanadoYo) {
                out.println("<div class='mensaje-fin gana'>");
                out.println("🏆 ¡HAS GANADO! 🏆<br>");
                out.println("<span style='font-size:20px'>¡Felicidades, eres el Rey de la Oca!</span><br><br>");
                out.println("<a href='menu' class='boton' style='text-decoration:none; color:black; font-size:16px;'>VOLVER AL MENÚ</a>");
                out.println("</div>");
            } else {
                out.println("<div class='mensaje-fin pierde'>");
                out.println("💀 HAS PERDIDO 💀<br>");
                out.println("<span style='font-size:20px'>El rival llegó a la meta antes...</span><br><br>");
                out.println("<a href='menu' class='boton' style='text-decoration:none; color:black; font-size:16px;'>VOLVER AL MENÚ</a>");
                out.println("</div>");
            }
        }
        // -----------------------------------

        out.println("<div class='tablero'>");
        for (int i = 1; i <= 63; i++) {
            String clase = "";
            String texto = "";
            
            if (i == 19) { clase = " posada"; texto = "🏩 CLUB"; }
            else if (i == 52) { clase = " carcel"; texto = "CÁRCEL"; }
            else if (i == 58) { clase = " calavera"; texto = "☠️"; }
            else if (i == 63) { clase = " meta"; texto = "FIN"; }
            else if (i % 9 == 0) { clase = " oca"; texto = "OCA"; }

            out.println("<div class='casilla" + clase + "'>");
            out.println("<span>" + i + "</span>"); 
            out.println("<span style='font-size:10px; text-align:center;'>" + texto + "</span>");
            
            for (Integer idJug : posiciones.keySet()) {
                if (posiciones.get(idJug) == i) {
                    if (idJug.equals(miId)) {
                        out.println("<div class='ficha-yo' title='YO'></div>");
                    } else {
                        out.println("<div class='ficha-rival' title='Rival'></div>");
                    }
                }
            }
            
            out.println("</div>");
        }
        out.println("</div>");
        
        // CONTROLES
        if (!juegoTerminado) {
            out.println("<form action='jugar' method='POST'>");
            if (esMiTurno) {
                out.println("<input type='submit' value='🎲 TIRAR DADO' class='boton'>");
            } else {
                out.println("<input type='submit' value='ESPERANDO...' class='boton-disabled' disabled>");
            }
            out.println("</form>");
        }

        out.println("</body></html>");
    }
    
    public void doPost(HttpServletRequest req, HttpServletResponse res) 
    throws IOException, ServletException {
        
        HttpSession session = req.getSession();
        Integer miId = (Integer) session.getAttribute("idJugador");
        Integer idPartida = (Integer) session.getAttribute("idPartidaActual");

        if (miId == null || idPartida == null) {
            res.sendRedirect("login.html");
            return;
        }

        LogicaOca miJuego = new LogicaOca();
        int dadoSacado = miJuego.jugarTurno(miId, idPartida); 
        
        req.setAttribute("valorDado", dadoSacado);
        doGet(req, res);
    }
}