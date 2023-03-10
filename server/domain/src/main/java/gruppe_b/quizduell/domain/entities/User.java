package gruppe_b.quizduell.domain.entities;

import java.util.UUID;

/**
 * User Objekt
 * 
 * @author Christopher Burmeister
 */
public class User {
    UUID id;
    String name;
    String mail;
    String passwordHash;
    String salt;

    public User() {
    }

    public User(String name, String mail, String passwordHash, String salt) {
        this.name = name;
        this.mail = mail;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    public User(UUID id, String name, String mail, String passwordHash, String salt) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
