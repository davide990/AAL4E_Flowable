package aal4e;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This class starts Flowable. All workflow deployment and execution requests are handled through http requests
 */
@SpringBootApplication
public class ApplicationLauncher {

    private static Logger logger = LoggerFactory.getLogger(ApplicationLauncher.class);

    public static void main(String[] args) {
        SpringApplication.run(ApplicationLauncher.class, args);
    }

}