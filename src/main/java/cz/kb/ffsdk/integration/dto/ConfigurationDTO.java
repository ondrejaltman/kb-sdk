package cz.kb.ffsdk.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationDTO {

    private String name;
    private byte[] checksum;
    private Collection<ConditionDTO> conditions;

}
