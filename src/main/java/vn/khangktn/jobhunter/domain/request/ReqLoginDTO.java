package vn.khangktn.jobhunter.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReqLoginDTO {
    @NotBlank(message = "Username is not empty!")
    String username;
    @NotBlank(message = "Password is not empty!")
    String password;
}
