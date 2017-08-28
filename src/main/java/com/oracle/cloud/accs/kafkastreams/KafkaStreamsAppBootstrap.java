package com.oracle.cloud.accs.kafkastreams;

import com.oracle.cloud.accs.kafkastreams.rest.TweetStateResource;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.UriBuilder;
import org.apache.kafka.streams.KafkaStreams;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public final class KafkaStreamsAppBootstrap {

    private static final Logger LOGGER = Logger.getLogger(KafkaStreamsAppBootstrap.class.getName());

    private static void bootstrap() throws IOException {
        Random rnd = new Random();
        String port = Optional.ofNullable(System.getenv("PORT")).orElse("808" + String.valueOf(rnd.nextInt(10)));
        String host = System.getenv().getOrDefault("HOSTNAME", "localhost");
        System.out.println("host " + host);

        URI baseUri = UriBuilder.fromUri("http://" + host + "/").port(Integer.parseInt(port)).build();
        ResourceConfig config = new ResourceConfig(TweetStateResource.class)
                .register(MoxyJsonFeature.class);
        HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);
        Logger.getLogger(KafkaStreamsAppBootstrap.class.getName()).log(Level.INFO, "Application accessible at {0}", baseUri.toString());

        KafkaStreams theStream = new KafkaStreamsTweetAnalysisJob().startPipeline();

        //gracefully exit
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                LOGGER.log(Level.INFO, "Exiting......");
                try {
                    theStream.close();
                    LOGGER.log(Level.INFO, "Kafka Stream services stopped");

                    server.stop(0);
                    LOGGER.log(Level.INFO, "Jersey REST services stopped");

                } catch (Exception ex) {
                    //log & continue....
                    LOGGER.log(Level.SEVERE, ex, ex::getMessage);
                }

            }
        }));
    }

    public static void main(String[] args) throws Exception {

        bootstrap();

    }
}
