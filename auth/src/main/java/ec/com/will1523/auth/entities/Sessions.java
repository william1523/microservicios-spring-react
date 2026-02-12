package ec.com.will1523.auth.entities;



import java.sql.Timestamp;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Sessions")
public class Sessions {
    @Id
    private Integer id;
    private User user;
    private Timestamp createDate;
    private Timestamp endDate;
    private boolean alive;
    
    public Sessions() {
    }

    public Sessions(Integer id, User user, Timestamp createDate, Timestamp endDate, boolean alive) {
        this.id = id;
        this.user = user;
        this.createDate = createDate;
        this.endDate = endDate;
        this.alive = alive;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    
}
