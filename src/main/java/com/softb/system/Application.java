package com.softb.system;

import com.softb.system.cache.config.CacheConfig;
import com.softb.system.config.Constants;
import com.softb.system.config.ServiceConfig;
import com.softb.system.repository.config.RepositoryConfig;
import com.softb.system.security.config.SecurityConfig;
import com.softb.system.web.config.WebMvcConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;

/**
 * Startup spring boot
 * @author marcuslacerda 
 *
 */
@Configuration
@EnableAutoConfiguration
@EnableScheduling
@EnableConfigurationProperties
@Import(value={WebMvcConfig.class, ServiceConfig.class, SecurityConfig.class, CacheConfig.class, RepositoryConfig.class})
public class Application {

    private final Logger log = LoggerFactory.getLogger(Application.class);

    @Inject
    private Environment env;

    /**
     * Main method, used to run the application.
     *
     * To run the application with hot reload enabled, add the following arguments to your JVM:
     * "-javaagent:spring_loaded/springloaded-jhipster.jar -noverify -Dspringloaded=plugins=io.github.jhipster.loaded.instrument.JHipsterLoadtimeInstrumentationPlugin"
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.setShowBanner(false);

        SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);

        // Check if the selected profile has been set as argument.
        // if not the development profile will be added
        addDefaultProfile(app, source);

        app.run(args);

//        System.exit(SpringApplication.exit(SpringApplication.run(
//                BatchConfig.class, args)));
    }

    /**
     * Set a default profile if it has not been set
     */
    private static void addDefaultProfile(SpringApplication app, SimpleCommandLinePropertySource source) {
        if (!source.containsProperty("spring.profiles.active")) {
            app.setAdditionalProfiles(Constants.SPRING_PROFILE_DEVELOPMENT);
        }
    }

    /**
     * Initializes socialtravel.
     * <p>
     * Spring profiles can be configured with a program arguments --spring.profiles.active=your-active-profile
     * <p>
     */
    @PostConstruct
    public void initApplication() throws IOException {
        if (env.getActiveProfiles ().length == 0) {
            log.warn ( "No Spring profile configured, running with default categorization" );
        } else {
            log.info ( "Running with Spring profile(s) : {}", Arrays.toString ( env.getActiveProfiles () ) );
        }
    }
}
