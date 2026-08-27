package com.LetMeDoWith.LetMeDoWith.common;

import com.LetMeDoWith.LetMeDoWith.application.auth.factory.SocialProviderAuthFactory;
import com.LetMeDoWith.LetMeDoWith.application.auth.service.CreateTokenService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
public class BeanTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    private SocialProviderAuthFactory socialProviderAuthFactory;

    @Test
    @DisplayName("application context bean Test")
    void applicationContextBeanTest() {

        // when
        CreateTokenService createTokenService1 = applicationContext.getBean(CreateTokenService.class);
        CreateTokenService createTokenService2 = (CreateTokenService) applicationContext.getBean("createTokenService");

        // then
        Assertions.assertThat(createTokenService1).isInstanceOf(CreateTokenService.class);
        Assertions.assertThat(createTokenService2).isInstanceOf(CreateTokenService.class);
    }

    @Test
    @DisplayName("Bean Factory Test")
    void beanFactoryTest() {

        // when
        CreateTokenService createTokenService1 = beanFactory.getBean(CreateTokenService.class);
        CreateTokenService createTokenService2 = (CreateTokenService) beanFactory.getBean("createTokenService");

        // then
        Assertions.assertThat(createTokenService1).isInstanceOf(CreateTokenService.class);
        Assertions.assertThat(createTokenService2).isInstanceOf(CreateTokenService.class);
    }
}
