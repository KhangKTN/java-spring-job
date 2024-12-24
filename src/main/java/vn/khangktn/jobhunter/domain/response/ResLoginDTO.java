package vn.khangktn.jobhunter.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.khangktn.jobhunter.domain.Role;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ResLoginDTO {
    @JsonProperty("access_token")
    private String accessToken;
    private UserLogin user;

    @Setter @Getter 
    @NoArgsConstructor @AllArgsConstructor
    public static class UserLogin{
        private long id;
        private String email;
        private String name;
        private Role role;
    }

    @Setter @Getter 
    @NoArgsConstructor @AllArgsConstructor
    public static class UserAccount{
        private UserLogin user;
    }

    @Setter @Getter 
    @NoArgsConstructor @AllArgsConstructor
    public static class UserInsideToken{
        private long id;
        private String email;
        private String name;
    }
}
