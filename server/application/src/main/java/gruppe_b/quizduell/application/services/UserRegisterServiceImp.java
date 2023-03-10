package gruppe_b.quizduell.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import gruppe_b.quizduell.application.user.commands.create_user.CreateUserCommand;
import gruppe_b.quizduell.application.user.commands.create_user.CreateUserCommandHandler;
import gruppe_b.quizduell.application.common.UserCredentialsDto;

/**
 * Service zum Erstellen eines neuen User's in der Datenbank.
 * 
 * @author Christopher Burmeister
 */
@Service
public class UserRegisterServiceImp implements UserRegisterService {

    @Autowired
    CreateUserCommandHandler createUserHandler;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void saveUser(UserCredentialsDto userCredentialsDto) {
        if (userCredentialsDto.mail != null && userCredentialsDto.mail.equals("")) {
            userCredentialsDto.mail = null;
        }
        CreateUserCommand command = new CreateUserCommand(
                userCredentialsDto.name,
                userCredentialsDto.mail,
                passwordEncoder.encode(userCredentialsDto.password),
                "salt");

        createUserHandler.handle(command);
    }
}
