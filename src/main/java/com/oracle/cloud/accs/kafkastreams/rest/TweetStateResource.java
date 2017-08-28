package com.oracle.cloud.accs.kafkastreams.rest;

import com.oracle.cloud.accs.kafkastreams.domain.Stat;
import com.oracle.cloud.accs.kafkastreams.state.TweetAnalysisStateStore;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("stats")
public class TweetStateResource {
    
    
    final static String TRACKED_TERMS_STRING = System.getenv().getOrDefault("TRACKED_TERMS", "java,kafka,cloud,oracle,paas");
    
    @Path("{term}")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Stat forTerm(@PathParam("term") String term){
        Long count = TweetAnalysisStateStore.get(term);
        return new Stat(term, count);
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<Stat> all(){
        List<String> terms = Arrays.asList(TRACKED_TERMS_STRING.split(","));
        List<Stat> stats = terms.stream()
                .map((term) -> new Stat(term, TweetAnalysisStateStore.get(term)))
                .collect(Collectors.toList());
        
        //System.out.println("all stats size = "+ stats.size());
        return stats;
    }
}
