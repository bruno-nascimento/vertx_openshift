package vertx;

import java.util.Map.Entry;

import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.sockjs.EventBusBridgeHook;
import org.vertx.java.core.sockjs.SockJSSocket;

public class ServerHook implements EventBusBridgeHook {
	
	Logger logger;
	private Vertx vertx;

	public ServerHook(Logger logger) {
		logger.info("-=- ### -=- ### -=- ######## ServerHook");
		this.logger = logger;
	}

	@Override
	public boolean handleSocketCreated(SockJSSocket sock) {
		// You can do things in here like check the Origin of the request
		logger.info("-=- ### -=- ### -=- ######## handleSocketCreated");

		String origin = sock.headers().get("origin");
		sock.write(new Buffer().appendString("lerolerolero"));
		logger.info("-=- ### -=- ### -=- sock is "+ sock);
		logger.info("-=- ### -=- ### -=- Origin is " + origin);
		logger.info("-=- ### -=- ### -=- URI is " + sock.uri());
		logger.info("-=- ### -=- ### -=- sock.writeHandlerID() is "+ sock.writeHandlerID());
		logger.info("-=- ### -=- ### -=- sock.localAddress() is "+ sock.localAddress());
		logger.info("-=- ### -=- ### -=- sock.remoteAddress() is "+ sock.remoteAddress());
		
		for (Entry<String, String> entry : sock.headers().entries()) {
			logger.info("-=- ### -=- ### -=- sock.headers() - $"+entry.getKey()+"$ is: " + entry.getValue());
		}
		
		
		vertx.sharedData().getSet("conns").add(sock.writeHandlerID());

		sock.dataHandler(new Handler<Buffer>() {
			@Override
			public void handle(Buffer buffer) {
				logger.info("################### sock.dataHandler >>>"+buffer.toString());
			}
		});
		
		return true;
	}

	/**
	 * The socket has been closed
	 * 
	 * @param sock
	 *            The socket
	 */
	public void handleSocketClosed(SockJSSocket sock) {
		logger.info("-=- ### -=- ### -=- ######## handleSocketClosed");
		logger.info("handleSocketClosed, sock = " + sock);
	}

	/**
	 * Client is sending or publishing on the socket
	 * 
	 * @param sock
	 *            The sock
	 * @param send
	 *            if true it's a send else it's a publish
	 * @param msg
	 *            The message
	 * @param address
	 *            The address the message is being sent/published to
	 * @return true To allow the send/publish to occur, false otherwise
	 */
	public boolean handleSendOrPub(SockJSSocket sock, boolean send, JsonObject msg, String address) {
		logger.info("-=- ### -=- ### -=- ######## handleSendOrPub");
		
		logger.info("handleSendOrPub, sock = " + sock + ", send = " + send + ", address = " + address);
		logger.info(msg);
		return true;
	}

	/**
	 * Client is registering a handler
	 * 
	 * @param sock
	 *            The socket
	 * @param address
	 *            The address
	 * @return true to let the registration occur, false otherwise
	 */
	public boolean handlePreRegister(SockJSSocket sock, String address) {
		logger.info("-=- ### -=- ### -=- ######## handlePreRegister");
		logger.info("handlePreRegister, sock = " + sock + ", address = " + address);
		return true;
	}

	public void handlePostRegister(SockJSSocket sock, String address) {
		logger.info("-=- ### -=- ### -=- ######## handlePostRegister");
		logger.info("handlePostRegister, sock = " + sock + ", address = "+ address);
	}

	/**
	 * Client is unregistering a handler
	 * 
	 * @param sock
	 *            The socket
	 * @param address
	 *            The address
	 */
	public boolean handleUnregister(SockJSSocket sock, String address) {
		logger.info("-=- ### -=- ### -=- ######## handleUnregister");
		logger.info("handleUnregister, sock = " + sock + ", address = "+ address);
		return true;
	}

	@Override
	public boolean handleAuthorise(JsonObject message, String sessionID, Handler<AsyncResult<Boolean>> handler) {
		logger.info("-=- ### -=- ### -=- ######## handleAuthorise");
		return false;
	}

	public void setVertx(Vertx vertx) {
		this.vertx = vertx;
	}
	
}
