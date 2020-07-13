package com.showcup.etl.properties;

import java.io.IOException;
import java.util.List;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

/**
 * 
 * @Description 自定义读取yaml配置文件的工厂类，支持通过PropertySource读取yaml配置，默认情况下spring不支持PropertySource注解读取yaml配置文件
 * @author 封厂长
 * @date 2020-7-6
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

	@Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        List<PropertySource<?>> sources = new YamlPropertySourceLoader().load(resource.getResource().getFilename(), resource.getResource());
        return sources.get(0);
    }
}
