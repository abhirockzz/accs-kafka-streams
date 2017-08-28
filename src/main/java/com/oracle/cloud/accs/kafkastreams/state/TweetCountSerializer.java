package com.oracle.cloud.accs.kafkastreams.state;

import java.io.IOException;
import com.oracle.cloud.cache.basic.io.Serializer;

public class TweetCountSerializer implements Serializer {

    public TweetCountSerializer() {
    }

    @Override
    public byte[] serialize(Object o) throws IOException {
        byte[] serializedForm = null;
        try {
            Long count = (Long) o;
            serializedForm = String.valueOf(count).getBytes();
            //System.out.println("Serialized successfully");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return serializedForm;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clzType) throws IOException {
        Long count = null;
        if (data == null){
            System.out.println("Data null/empty");
            return null;
        }
        try {
            count = Long.valueOf(new String(data));
            //System.out.println("De-serialized form " + count);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return (T) count;
    }
}
