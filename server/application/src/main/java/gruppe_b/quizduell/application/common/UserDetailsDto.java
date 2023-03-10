package gruppe_b.quizduell.application.common;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import gruppe_b.quizduell.domain.entities.User;

public class UserDetailsDto implements Serializable {

    private UserDetailsDto() {

    }

    public UserDetailsDto(User user) {
        this.name = user.getName();
        this.mail = user.getMail();
        this.userId = user.getId().toString();
    }

    public String name;

    public String userId;

    public String mail;
}
