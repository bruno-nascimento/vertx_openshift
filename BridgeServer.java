package repo;

import java.util.Random;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.sockjs.SockJSServer;
import org.vertx.java.platform.Verticle;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class BridgeServer extends Verticle {
  Logger logger;

  public void start() {
    logger = container.logger();
    
    HttpServer server = vertx.createHttpServer();

    // Also serve the static resources. In real life this would probably be done by a CDN
    server.requestHandler(new Handler<HttpServerRequest>() {
      public void handle(HttpServerRequest req) {
        if (req.path().equals("/")) req.response().sendFile("webroot/index.html"); // Serve the index.html
      }
    });

    JsonArray permitted = new JsonArray();
    permitted.add(new JsonObject()); // Let everything through

    ServerHook hook = new ServerHook(logger);
    hook.setVertx(vertx);

    SockJSServer sockJSServer = vertx.createSockJSServer(server);
    sockJSServer.setHook(hook);
    sockJSServer.bridge(new JsonObject().putString("prefix", "/eventbus"), permitted, permitted);
    
    
    String host = System.getenv("OPENSHIFT_VERTX_IP");
    String port = System.getenv("OPENSHIFT_VERTX_PORT");
    
    server.listen(port, host);
    
    final EventBus eb = vertx.eventBus();
    final Random gerador = new Random();
    
    long timerID = vertx.setPeriodic(10000, new Handler<Long>() {
        public void handle(Long timerID) {
        	float numero = gerador.nextFloat() * 100;
            logger.info("%!%!%!%!%!%!%!%! ### mensagem enviada pelo timer "+ numero);
            eb.publish("someaddress", "msg automatica > data : "+ numero);
        }
    });
    
  }
}
