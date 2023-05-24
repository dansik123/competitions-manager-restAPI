package aber.dcs.uk.shootingCompetitionsBackend.helpers;

import aber.dcs.uk.shootingCompetitionsBackend.dtos.LoginDto;
import aber.dcs.uk.shootingCompetitionsBackend.dtos.RegisterUserDto;
import aber.dcs.uk.shootingCompetitionsBackend.entities.UserEntity;
import aber.dcs.uk.shootingCompetitionsBackend.models.Role;

import java.util.List;

public class CredentialsHelper {
    private static final String correctEncodedPassword = "$2a$10$4fx1SdUacGu.VA9V32eJeuWQwk5v4zlyGIt/D8hcLqffdV.BmaAGy";
    private static final String registerLastname = "fakeLastname";
    private static final String registerFirstname = "fakeFirstname";

    public static String getCorrectEncodedPassword(){
        return correctEncodedPassword;
    }

    public static RegisterUserDto getCorrectUserRegistrationDetails(){
        LoginDto loginDto = new LoginCredBuilder().useCorrectCred().build();
        return new RegisterUserDto(
                registerFirstname, registerLastname,
                loginDto.getEmail(), loginDto.getPassword());
    }

    public static UserEntity getDatabaseRegisteredUserEntity(){
        LoginDto loginDto = new LoginCredBuilder().useCorrectCred().build();
        return new UserEntity(1L, registerFirstname,
                registerLastname, loginDto.getEmail(), correctEncodedPassword,
                true, Role.USER);
    }

    public static UserEntity getUserEntityById(Long id){
        return getUsersEntitiesList().stream().
                filter(entity -> entity.getId().equals(id)).
                findFirst().orElse(null);
    }

    private static List<UserEntity> getUsersEntitiesList(){
        return List.of(
                getDatabaseAdminUserEntity(),
                getDatabaseSpectatorUserEntity(),
                getDatabaseUser1Entity(),
                getDatabaseUser2Entity(),
                getDatabaseUser3Entity(),
                getDatabaseUser4Entity()
        );
    }
    private static UserEntity getDatabaseAdminUserEntity(){
        return new UserEntity(1L, "AdminFirstname",
                "AdminLastname", "admin@nano.com",
                "$2a$10$uoAEWbRSqOzROcODSXHyF.r8/F6qq/7Se0k64MkYDM6AMaT4GeOXi",
                true, Role.ADMIN);
    }

    private static UserEntity getDatabaseSpectatorUserEntity(){
        return new UserEntity(2L, "spectatorFirstname",
                "spectatorLastname", "spectator@nano.com",
                "$2a$10$HvRitwmj3r4bsvfyGowSQeNVZkTBIe2veNispwpBU/42xI/88DoYm",
                true, Role.SPECTATOR);
    }

    private static UserEntity getDatabaseUser1Entity(){
        return new UserEntity(3L, "userOneFirstname",
                "userOneLastname", "userOne@nano.com",
                "$2a$10$51b0C7M3Ebn0XkK4EIRoIO/6/StKzOF/50qCmaO6GwUkXxtVHrnty",
                true, Role.USER);
    }

    private static UserEntity getDatabaseUser2Entity(){
        return new UserEntity(4L, "userTwoFirstname",
                "userTwoLastname", "userTwo@nano.com",
                "$2a$10$3wNPi8cKypwMX/hVCWW/W.AVTQPuspPAVxWLpIeUdZss/Bgtbusdq",
                true, Role.USER);
    }

    private static UserEntity getDatabaseUser3Entity(){
        return new UserEntity(5L, "userThreeFirstname",
                "userThreeLastname", "userThree@nano.com",
                "$2a$10$Y1tMxAg0kxiN7ZIJRywxdujEKDeNZsG8y3hQxSwTBlPVPrekwa90a",
                true, Role.USER);
    }

    private static UserEntity getDatabaseUser4Entity(){
        return new UserEntity(6L, "userFourFirstname",
                "userFourLastname", "userFour@nano.com",
                "$2a$10$d7Lx4HKTsM0aA0j2YM9Cau9a5gSHPF2pIUPkigCgo8kSOF4zWVmn.",
                true, Role.USER);
    }

    public static UserEntity getDatabaseRegisteredUserEntityWithChangedPassword(String password){
        UserEntity user = getDatabaseRegisteredUserEntity();
        user.setPassword(password);
        return user;
    }
    public static class JwtTokenBuilder{
        private static final String issuedFakeRefreshToken = "fake_generated_refreshToken_jwt";
        private static final String issuedFakeAccessToken = "fake_generated_accessToken_jwt";

        private static  final String tokenNotInDbSuffix = "_not_saved_in_db";

        private final StringBuilder stringBuilder;

        public JwtTokenBuilder(){
            stringBuilder = new StringBuilder();
        }

        public JwtTokenBuilder addRefreshToken(){
            stringBuilder.append(issuedFakeRefreshToken);
            return this;
        }

        public JwtTokenBuilder addAccessToken(){
            stringBuilder.append(issuedFakeAccessToken);
            return this;
        }

        public JwtTokenBuilder addTokenPrefix(){
            stringBuilder.insert(0, "Bearer ");
            return this;
        }

        public JwtTokenBuilder addNotInDBSuffix(){
            stringBuilder.append(tokenNotInDbSuffix);
            return this;
        }

        public String buildToken(){
            return stringBuilder.toString();
        }
    }
    public static class LoginCredBuilder{
        private static final String adminUsername = "admin@nano.com";
        private static final String adminPassword = "adminPassword";
        private static final String spectatorUsername = "spectator@nano.com";
        private static final String spectatorPassword = "spectatorPassword";
        private static final String user1Username = "userOne@nano.com";
        private static final String user1Password = "userOnePassword";
        private static final String user2Username = "userTwo@nano.com";
        private static final String user2Password = "userTwoPassword";
        private static final String user3Username = "userThree@nano.com";
        private static final String user3Password = "userThreePassword";
        private static final String user4Username = "userFour@nano.com";
        private static final String user4Password = "userFourPassword";

        private static final String correctUsername = "fakeEmail@nano.com";
        private static final String correctPassword = "simplePassword123";
        private LoginDto loginDto;

        public LoginCredBuilder useCorrectCred()
        {
            this.loginDto = new LoginDto(correctUsername, correctPassword);
            return this;
        }

        public LoginCredBuilder useIncorrectCred(){
            this.loginDto = new LoginDto(correctUsername, correctPassword.substring(0,5));
            return this;
        }

        public LoginCredBuilder useAdminUsername(){
            this.loginDto.setEmail(adminUsername);
            return this;
        }

        public LoginCredBuilder useAdminPassword(){
            this.loginDto.setPassword(adminPassword);
            return this;
        }

        public LoginCredBuilder useSpectatorUsername(){
            this.loginDto.setEmail(spectatorUsername);
            return this;
        }

        public LoginCredBuilder useSpectatorPassword(){
            this.loginDto.setPassword(spectatorPassword);
            return this;
        }

        public LoginCredBuilder useUser1Username(){
            this.loginDto.setEmail(user1Username);
            return this;
        }

        public LoginCredBuilder useUser1Password(){
            this.loginDto.setPassword(user1Password);
            return this;
        }

        public LoginCredBuilder useUser2Username(){
            this.loginDto.setEmail(user2Username);
            return this;
        }

        public LoginCredBuilder useUser2Password(){
            this.loginDto.setPassword(user2Password);
            return this;
        }

        public LoginCredBuilder useUser3Username(){
            this.loginDto.setEmail(user3Username);
            return this;
        }

        public LoginCredBuilder useUser3Password(){
            this.loginDto.setPassword(user3Password);
            return this;
        }

        public LoginCredBuilder useUser4Username(){
            this.loginDto.setEmail(user4Username);
            return this;
        }

        public LoginCredBuilder useUser4Password(){
            this.loginDto.setPassword(user4Password);
            return this;
        }

        public LoginDto build(){
            return loginDto;
        }
    }
}
