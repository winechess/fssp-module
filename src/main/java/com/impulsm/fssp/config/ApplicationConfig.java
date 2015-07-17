package com.impulsm.fssp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by vinichenkosa on 08/07/15.
 */
public class ApplicationConfig {

    public final ProjectStage PROJECT_STAGE;
    private final static Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    public ApplicationConfig() {
        String projectStage = null;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            Properties p = new Properties();
            p.load(is);
            projectStage = p.getProperty("PROJECT_STAGE");
        } catch (IOException e) {
            logger.error("Не удалось загрузить свойства из фалйа application.properties");
        }
        PROJECT_STAGE = ProjectStage.valueOf(projectStage);
    }


}
