package com.softb.system.repository.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@EnableJpaRepositories(basePackages = { 
		"com.softb.system.security.repository"
})
@EntityScan(basePackages = { 
		"com.softb.system.security.model"
})
public class RepositoryConfig  {

    private final Logger logger = LoggerFactory.getLogger(RepositoryConfig.class);
    @Autowired
    private Environment environment;
    
//    @Bean
//    public SpringLiquibase liquibase(DataSource datasource) {
//    	SpringLiquibase liquibase = null;
//
////    	if (!environment.acceptsProfiles(Constants.SPRING_PROFILE_PRODUCTION)) {
////	        logger.debug("Configuring Liquibase");
////	        liquibase = new SpringLiquibase();
////	        liquibase.setDataSource(datasource);
////	        liquibase.setChangeLog("classpath:config/liquibase/master.xml");
////	        liquibase.setContexts("development");
////    	}
//
//        return liquibase;
//    }
    
    
    @Bean
    public BasicDataSource dataSource() throws URISyntaxException {
    	URI dbUri = new URI(environment.getProperty("DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = (dbUri.getUserInfo().split(":").length > 1 ? dbUri.getUserInfo().split(":")[1] : "");
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(dbUrl);
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);
        basicDataSource.setDriverClassName("org.postgresql.Driver");

        return basicDataSource;
    }
}

