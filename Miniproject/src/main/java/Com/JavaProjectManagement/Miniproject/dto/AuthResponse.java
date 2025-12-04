package Com.JavaProjectManagement.Miniproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;

    @JsonProperty("token_type")
    private String tokenType = "Bearer";

    private String username;

    @JsonProperty("expires_in")
    private long expiresIn;

}