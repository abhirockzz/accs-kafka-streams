package com.oracle.cloud.accs.kafkastreams;

import static com.oracle.cloud.accs.kafkastreams.KafkaStreamsTweetAnalysisJob.TRACKED_TERMS_STRING;
import com.oracle.cloud.accs.kafkastreams.state.TweetAnalysisStateStore;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;


public class TweetStreamProcessor implements Processor<String, String> {

    final static List<String> TRACKED_TERMS = Arrays.asList(TRACKED_TERMS_STRING.split(","));

    public TweetStreamProcessor() {
    }

    @Override
    public void init(ProcessorContext pc) {

        System.out.println("Processor initialized in thread " + Thread.currentThread().getName());
        
    }

    @Override
    public void process(String tweeter, String tweet) {
        //System.out.println("Processor OPERATING in thread " + Thread.currentThread().getName());
        
        System.out.println("Tweeter - " + tweeter);
        System.out.println("Tweet - " + tweet);
        if (tweet != null) {
            TRACKED_TERMS.stream().parallel()
                    .forEach((term) -> {
                        //System.out.println("tracked word " + term);
                        if (tweet.toLowerCase().contains(term)) {
                            Long currentCount = TweetAnalysisStateStore.get(term) == null ? 0L : TweetAnalysisStateStore.get(term);
                            currentCount ++;
                            System.out.println("Count for word " + term + " is " + currentCount);
                            TweetAnalysisStateStore.put(Optional.of(term).get(),
                                                        Optional.of(currentCount).get());

                        }
                    });
        }

    }

    @Override
    public void punctuate(long l) {
        //no-op in this case
    }

    @Override
    public void close() {
        System.out.println("Closing processor...");
    }

}
