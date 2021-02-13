package cz.kb.ffsdk.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContextInDTO {
    private String environment;
    private String username;
    private String clientIP;
}
