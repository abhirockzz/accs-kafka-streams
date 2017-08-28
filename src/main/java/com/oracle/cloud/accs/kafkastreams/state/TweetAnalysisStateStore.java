package com.oracle.cloud.accs.kafkastreams.state;

import com.oracle.cloud.cache.basic.Cache;
import com.oracle.cloud.cache.basic.LocalSessionProvider;
import com.oracle.cloud.cache.basic.RemoteSessionProvider;
import com.oracle.cloud.cache.basic.SessionProvider;
import com.oracle.cloud.cache.basic.options.Transport;

import java.util.Optional;

public class TweetAnalysisStateStore {

    private TweetAnalysisStateStore() {
    }
    
    
    private static final String CACHE_NAME = "tweet-analysis-store";
    private final static Cache<Long> TWEETS_ANALYSIS_STORE;

    static {
        String protocolName = Optional.ofNullable(System.getenv("CACHING_PROTOCOL")).orElse("REST");
        System.out.println("Protocol - " + protocolName);

        String port = protocolName.equals("REST") ? "8080" : "1444";
        String cacheUrlSuffix = protocolName.equals("REST") ? "ccs" : "";

        Transport transport = protocolName.equals("REST") ? Transport.rest() : Transport.grpc();
        System.out.println("Transport - " + transport.getType().name());

        String cacheHost = System.getenv().getOrDefault("CACHING_INTERNAL_CACHE_URL","tweet-analysis-store-acc");
        String cacheUrl = "http://" + cacheHost + ":" + port + "/" + cacheUrlSuffix;
        System.out.println("Cache URL - " + cacheUrl);

        SessionProvider sessionProvider = new RemoteSessionProvider(cacheUrl);
        TWEETS_ANALYSIS_STORE = sessionProvider.createSession(transport)
                .getCache(CACHE_NAME, new TweetCountSerializer());

//        SessionProvider sessionProvider = new LocalSessionProvider();
//        TWEETS_ANALYSIS_STORE = sessionProvider.createSession()
//                .getCache(CACHE_NAME);
        

    }

    public static void put(String term, Long count) {
        if (term != null && count != null) {
            TWEETS_ANALYSIS_STORE.put(term, count);
        }

    }

    public static Long get(String term) {
        Long count = TWEETS_ANALYSIS_STORE.get(term);
        return count;
    }
}
