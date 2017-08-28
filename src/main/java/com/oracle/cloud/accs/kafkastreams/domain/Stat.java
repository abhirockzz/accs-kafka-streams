package com.oracle.cloud.accs.kafkastreams.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Stat {
    private String term;
    private Long count;
    

    public Stat() {
    }

    public Stat(String term,Long count) {
        this.term = term;
        this.count = count;
    }

    public String getTerm() {
        return term;
    }

    public Long getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "Stat{" + "term=" + term + ", count=" + count + '}';
    }

    
    
    
}
