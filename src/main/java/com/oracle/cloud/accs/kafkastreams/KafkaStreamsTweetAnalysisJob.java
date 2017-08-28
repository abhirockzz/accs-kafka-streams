package com.oracle.cloud.accs.kafkastreams;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.apache.kafka.streams.processor.TopologyBuilder;

public class KafkaStreamsTweetAnalysisJob {

    private static final Logger LOGGER = Logger.getLogger(KafkaStreamsTweetAnalysisJob.class.getSimpleName());

    private static final String SOURCE_NAME = "tweets-source";
    String topicName = System.getenv().getOrDefault("OEHCS_TOPIC", "tweets");
    final static String TRACKED_TERMS_STRING = System.getenv().getOrDefault("TRACKED_TERMS", "java,kafka,cloud,oracle,paas");
    private static final String PROCESSOR_NAME = "tweets-processor";
    
    KafkaStreams streams = null;

    public KafkaStreams startPipeline() {

        Map<String, Object> configurations = new HashMap<>();

        configurations.put(StreamsConfig.APPLICATION_ID_CONFIG, Optional.ofNullable(System.getenv("STREAMS_APP_ID")).orElse("tweet-stream-app"));

        String kafkaBroker = Optional.ofNullable(System.getenv("OEHCS_EXTERNAL_CONNECT_STRING")).orElse("localhost:9092");
        LOGGER.log(Level.INFO, "Kafa broker {0}", kafkaBroker);
        configurations.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker);

        configurations.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        configurations.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        //configurations.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 5);

        StreamsConfig config = new StreamsConfig(configurations);

        TopologyBuilder builder = processingTopologyBuilder();
        
        boolean connected = false;
        int retries = 0;

        do {
            LOGGER.info("Initiating Kafka Streams");
            try {
                streams = new KafkaStreams(builder, config);
                connected = true;
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error during Kafka Stream initialization {0} .. retrying", e.getMessage());
                retries++;
            }

        } while (!connected && retries <= 1); //retry twice

        if (!connected) {
            LOGGER.warning("Unable to initialize Kafka Streams.. exiting");
            System.exit(0);
        }

        streams.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LOGGER.log(Level.SEVERE, "Uncaught exception in Thread {0} - {1}", new Object[]{t, e.getMessage()});
                e.printStackTrace();
            }
        });

        streams.start();
        System.out.println("Kafka Streams started.......");
        return streams;

    }


    private TopologyBuilder processingTopologyBuilder() {

        TopologyBuilder builder = new TopologyBuilder();

        builder.addSource(SOURCE_NAME, topicName)
                .addProcessor(PROCESSOR_NAME, new ProcessorSupplier() {
                    @Override
                    public Processor get() {
                        return new TweetStreamProcessor();
                    }
                }, SOURCE_NAME);

        LOGGER.info("Kafka streams processing topology ready");

        return builder;
    }

}
