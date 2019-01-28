package me.ihxq.mybatis.easytypehandle.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.ihxq.mybatis.easytypehandle.handler.Persistable;
import me.ihxq.mybatis.easytypehandle.handler.PersistableHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnMissingBean(PersistableConfig.class)
@ConfigurationProperties(prefix = "mybatis.easy-type-handlers")
public class PersistableConfig {

    /**
     * basePackages to scan, comma separated or config with index
     */
    @Getter
    @Setter
    private Set<String> basePackages = new HashSet<>();

    @Bean
    ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return configuration -> {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            ConfigurationBuilder reflectConfiguration;
            reflectConfiguration = new ConfigurationBuilder();
            for (String basePackage : basePackages) {
                reflectConfiguration = reflectConfiguration.addUrls(ClasspathHelper.forPackage(basePackage));
            }
            Reflections reflections = new Reflections(reflectConfiguration);
            Set<Class<? extends Persistable>> subTypes = reflections.getSubTypesOf(Persistable.class);
            Set<Class<? extends Persistable>> finds = subTypes.stream()
                    .filter(type -> type.getEnclosingClass() == null)
                    .filter(type -> !type.isInterface()).collect(Collectors.toSet());
            log.info("find {} sub implement of MybatisHandleable: {}", finds.size(), finds.stream().map(Class::getSimpleName).collect(Collectors.joining("\n", "\n", "\n")));
            if (finds.isEmpty()) {
                log.warn("find no sub implement of MybatisHandleable, have you config basePackages correctly? " +
                        "config should be like: mybatis.easy-type-handlers.base-packages=com.example");
            }
            finds.forEach(type -> typeHandlerRegistry.register(type, PersistableHandler.class));
        };
    }
}
