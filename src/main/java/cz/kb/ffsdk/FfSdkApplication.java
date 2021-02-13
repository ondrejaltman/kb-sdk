package cz.kb.ffsdk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FfSdkApplication {

    public static void main(String[] args) {
        SpringApplication.run(FfSdkApplication.class, args);
    }

}
