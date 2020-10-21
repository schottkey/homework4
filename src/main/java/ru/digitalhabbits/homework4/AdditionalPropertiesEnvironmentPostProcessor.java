package ru.digitalhabbits.homework4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Arrays;

public class AdditionalPropertiesEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String PATH = "config";
    private static final String LOCATION_PATTERN = String.format("classpath:%s/*.properties", PATH);
    private final PropertySourceLoader loader = new PropertiesPropertySourceLoader();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Arrays.stream(getResources())
                .map(this::loadPropertySources)
                .forEach(environment.getPropertySources()::addLast);
    }

    private Resource[] getResources() {
        try {
            return new PathMatchingResourcePatternResolver().getResources(LOCATION_PATTERN);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load properties configuration from " + PATH, e);
        }
    }

    private PropertySource<?> loadPropertySources(Resource path) {
        if (!path.exists()) {
            throw new IllegalArgumentException("Resource " + path + " does not exist");
        }
        try {
            return loader.load(path.getFilename(), path).get(0);
        }
        catch (IOException e) {
            throw new IllegalStateException("Failed to load properties configuration from " + path, e);
        }
    }
}
