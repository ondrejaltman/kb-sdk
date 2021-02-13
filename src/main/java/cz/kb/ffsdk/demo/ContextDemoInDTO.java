package cz.kb.ffsdk.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContextDemoInDTO {
    private String ff;
    private String environment;
    private String username;
    private String ip;
}
