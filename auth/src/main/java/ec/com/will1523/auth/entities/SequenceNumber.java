package ec.com.will1523.auth.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class SequenceNumber {

    @Id
    @Indexed( unique = true)
    private String document;
    private long current = 1;
    public String getDocument() {
        return document;
    }
    public void setDocument(String document) {
        this.document = document;
    }
    public long getCurrent() {
        return current;
    }
    public void setCurrent(long current) {
        this.current = current;
    }

    


}

