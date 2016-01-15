package com.collatrice.frontend;

import com.github.aesteve.vertx.nubes.VertxNubes;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;

public class FrontEnd extends AbstractVerticle {

//    protected HttpServer server;
//	protected HttpServerOptions options;
	protected VertxNubes nubes;
    @Override
	public void init(Vertx vertx, Context context) {
		super.init(vertx, context);
		JsonObject config = context.config();
                System.out.println("com.collattice.FrontEnd.init()" + config.getString("src-package"));
//		options = new HttpServerOptions();
//		options.setHost(config.getString("host", "localhost"));
//		options.setPort(config.getInteger("port", 9000));
		nubes = new VertxNubes(vertx, config);
	}
    @Override
    public void start() {

//        JsonArray array = new JsonArray();
//        array.add("com.collatrice.frontend");
//        JsonObject obj = new JsonObject();
//        obj.put("src-package", array.toString());
//        VertxNubes nubes = new VertxNubes(vertx, obj);
        Router router = Router.router(vertx);
        BridgeOptions opts = new BridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddress("chat.to.server"))
                .addOutboundPermitted(new PermittedOptions().setAddress("chat.to.client"));
        SockJSHandler ebHandler = SockJSHandler.create(vertx).bridge(opts);
        router.route("/eventbus/*").handler(ebHandler);
        router.route().handler(StaticHandler.create().setCachingEnabled(false));
        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
        EventBus eb = vertx.eventBus();
        eb.consumer("chat.to.server").handler(message -> {
            // Create a timestamp string
            String timestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date.from(Instant.now()));
            // Send the message back out to all clients with the timestamp prepended.
            eb.publish("chat.to.client", timestamp + ": " + message.body());
        });
        nubes.bootstrap(res -> {
            if (res.succeeded()) {
                Router rouuter = res.result();
                for(Route rout:rouuter.getRoutes()){
                    System.out.println("routes" + rout.getPath());
                }
                System.out.println("Everything's readyy");
            } else {
                System.err.println("Something went wrong");
                res.cause().printStackTrace();
            }
        } , router);

    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        JsonObject object = new JsonObject().put("src-package", "com.collatrice.frontend");
        DeploymentOptions options = new DeploymentOptions();
        options.setConfig(object);
        vertx.deployVerticle(new FrontEnd() , options);
    }

}
