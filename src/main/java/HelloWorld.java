import spark.Session;

import static spark.Spark.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HelloWorld {
    static Map<String, Session> usertoSession = new ConcurrentHashMap<>();
    public static void main(String[] args) {
        String UI = "<html>\n" +
                "<head>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "    <title>WebsSockets</title>\n" +
                "    <link rel=\"stylesheet\" href=\"style.css\">\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div align=\"bottom\" id=\"chatControls\">\n" +
                "        <input id=\"message\" placeholder=\"Type your message\">\n" +
                "        <button id=\"send\">Send</button>\n" +
                "    </div>\n" +
                "    <ul id=\"userlist\"> <!-- Built by JS --> </ul>\n" +
                "    <div id=\"chat\">    <!-- Built by JS --> </div>\n" +
                "    <script src=\"websocketDemo.js\"></script>\n" +
                "</body>\n" +
                "</html>";
        get("/hello", (req, res) -> UI);
        get("/hello/:user1", (request, response) -> {
//            response.type("application/json");
//            return request.params(":user1");
            String users[] = request.params(":user1").split(",");
            adduser(users);
            return "Response:"+request.params(":user1")+"User1 : "+users[0] + " User 2 "+users[1];
        });


        get("/match/:user1", (request, response) -> {
            String users[] = request.params(":user1").split(",");
            Chat.addUserMapping(users);
            return "Response:"+request.params(":user1")+"User1 : "+users[0] + " User 2 "+users[1];
        });
    }
    public static void adduser(String users[]) {

    }
    public static void communicate(String from, String to) {

    }
}