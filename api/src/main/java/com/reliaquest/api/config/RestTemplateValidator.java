package com.reliaquest.api.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateValidator implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RestTemplate restTemplate) {
            validateRestTemplate(restTemplate, beanName);
        }
        return bean;
    }

    private void validateRestTemplate(RestTemplate restTemplate, String beanName) {
        if (!(restTemplate.getErrorHandler() instanceof CustomRestTemplateErrorHandler)) {
            throw new IllegalStateException(String.format(
                    "RestTemplate bean '%s' must be configured with CustomRestTemplateErrorHandler", beanName));
        }
    }
}
