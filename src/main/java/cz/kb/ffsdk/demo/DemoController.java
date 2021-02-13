package cz.kb.ffsdk.demo;

import cz.kb.ffsdk.integration.service.FeatureFlagService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DemoController {


    @GetMapping("/form")
    public String getForm(Model model) {
        model.addAttribute("contextDTO", new ContextInDTO());
        model.addAttribute("duration", 0L);

        return "index";
    }

    @PostMapping("/form")
    public String postForm(@ModelAttribute ContextInDTO contextInDTO, Model model) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        boolean isEnabled = FeatureFlagService.isFeatureEnabled(contextInDTO.getFf(),
                new cz.kb.ffsdk.integration.dto.ContextInDTO(contextInDTO.getEnvironment(), contextInDTO.getUsername(), contextInDTO.getIp()));

        stopWatch.stop();

        model.addAttribute("contextDTO", contextInDTO);
        model.addAttribute("duration", stopWatch.getLastTaskTimeMillis());
        model.addAttribute("result", isEnabled);

        return "index";
    }


}
