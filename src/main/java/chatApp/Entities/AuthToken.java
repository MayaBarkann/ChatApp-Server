package chatApp.Entities;

import javax.persistence.*;

@Entity
@Table(name = "authToken")
public class AuthToken {

    @Id
    private int id;
    @Column(unique = true, nullable = false)
    private String token;



}
