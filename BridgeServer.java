package repo;

import java.util.Random;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
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
    	  req.exceptionHandler(new Handler<Throwable>() {
			@Override
			public void handle(Throwable err) {
				logger.fatal("OPAAAAAAAAAAAAAAAA deu merda!", err);
			}
		});
        if (req.path().equals("/")) req.response().sendFile("webroot/index.html", "OPA! cadê isso que vocÊ pediu? tava aqui agorinha ... sumiu!", 
        		new Handler<AsyncResult<Void>>() {
					@Override
					public void handle(AsyncResult<Void> paramE) {
						logger.info("@@@@@@@@ handle sendFile index"+ paramE);
					}
        		}); // Serve the index.html
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
    logger.info("@@@@@@@@@ env.host: "+ host);
    
    String port = System.getenv("OPENSHIFT_VERTX_PORT");
    logger.info("@@@@@@@@@ env.port: "+ port);
    
    server.listen(port!= null ? Integer.valueOf(port) : 8080, host != null ? host : "127.0.0.1", new Handler<AsyncResult<HttpServer>>() {
		@Override
		public void handle(AsyncResult<HttpServer> paramE) {
			logger.info("handler do listen do server: "+paramE);
		}
	});
    
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
