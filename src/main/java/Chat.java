import io.javalin.Javalin;
import io.javalin.websocket.WsContext;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static j2html.TagCreator.*;

public class Chat {

    private static Map<WsContext, String> userUsernameMap = new ConcurrentHashMap<>();
    private static Map<String, WsContext> userToSession = new ConcurrentHashMap<>();
    private static Map<String, String> matchMap = new HashMap<>();

    public static void main(String[] args) {
        List<String> usernameStore = new ArrayList<>();
        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/public");
        }).start(7070);
        app.get("/match/:users", ctx -> {
            String users[] = ctx.pathParam("users").split(",");
            addUserMapping(users);
            String matchDetails = printMatch();
            ctx.result("User1 : " + users[0] + " User 2 " + users[1] + " map size: " + matchMap.size() + " " + matchDetails);
//            ctx.cookieStore("username", users[0]);
            usernameStore.add(users[0]);
            ctx.redirect("http://localhost:7070/onechat.html");
        });

        app.ws("/chat", ws -> {
            ws.onConnect(context -> {
                String username = usernameStore.get(usernameStore.size() - 1);
                userUsernameMap.put(context, username);
                userToSession.put(username, context);
                context.send(new JSONObject()
                        .put("userMessage", username + " joined the chat")
                        .toString()
                );
            });
            ws.onClose(context -> {
                String username = userUsernameMap.get(context);
                userUsernameMap.remove(context);
                userToSession.remove(username);
                context.send(new JSONObject()
                        .put("userMessage", username + " left the chat")
                        .toString()
                );
            });
            ws.onMessage(context -> {
                sendMessageTo(userUsernameMap.get(context), context.message(), userToSession.get(matchMap.get(userUsernameMap.get(context))));
            });
        });
    }

    private static String printMatch() {
        StringBuilder res = new StringBuilder();
        for (Map.Entry<String, String> match : matchMap.entrySet()) {
            res.append(match.getKey() + " matches to " + match.getValue() + "<br>");
        }
        return res.toString();
    }

    public static void addUserMapping(String[] users) {
        if (matchMap.containsKey(users[0]) || matchMap.containsKey(users[1])) {
            matchMap.remove(users[0]);
            matchMap.remove(users[1]);
        }
        matchMap.put(users[0], users[1]);
        matchMap.put(users[1], users[0]);
    }

    private static void sendMessageTo(String sender, String message, WsContext receiver) {
        if (receiver.session.isOpen() && userToSession.get(sender).session.isOpen()) {
            receiver.send(new JSONObject()
                    .put("userMessage", createHtmlMessage(sender, message))
                    .toString()
            );
            userToSession.get(sender).send(new JSONObject()
                    .put("userMessage", createHtmlMessage(sender, message))
                    .toString()
            );
        }
    }

    private static String createHtmlMessage(String sender, String message) {
        return article(
                b(sender + ":"),
                span(attrs(".timestamp"), new SimpleDateFormat("HH:mm:ss").format(new Date())),
                p(message)
        ).render();
    }
}