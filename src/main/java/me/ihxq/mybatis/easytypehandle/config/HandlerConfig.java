package me.ihxq.mybatis.easytypehandle.config;

import lombok.Getter;
import lombok.Setter;
import me.ihxq.mybatis.easytypehandle.handler.MybatisHandleable;
import me.ihxq.mybatis.easytypehandle.handler.PersistableHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = "mybatis.easy-type-handlers")
public class HandlerConfig {

    @Getter
    @Setter
    private Set<String> basePackages = new HashSet<>();

    @Bean
    ConfigurationCustomizer mybatisConfigurationCustomizer() {
        basePackages.add(MybatisHandleable.class.getPackage().getName());

        return configuration -> {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            ConfigurationBuilder reflectConfiguration;
            reflectConfiguration = new ConfigurationBuilder();
            for (String basePackage : basePackages) {
                reflectConfiguration = reflectConfiguration.addUrls(ClasspathHelper.forPackage(basePackage));
            }
            Reflections reflections = new Reflections(reflectConfiguration);
            Set<Class<? extends MybatisHandleable>> subTypes = reflections.getSubTypesOf(MybatisHandleable.class);
            System.out.println("find sub type : " + subTypes.size());
            subTypes.stream()
                    .filter(type -> type.getEnclosingClass() == null)
                    .filter(type -> !type.isInterface())
                    .forEach(type -> typeHandlerRegistry.register(type, PersistableHandler.class));
        };
    }
}
