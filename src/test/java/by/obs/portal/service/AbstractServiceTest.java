package by.obs.portal.service;

import by.obs.portal.utils.TimingExtension;
import by.obs.portal.spring.TestDataBaseConfig;
import by.obs.portal.spring.TestIntegrationConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.transaction.annotation.Transactional;

import static by.obs.portal.utils.ValidationUtil.getRootCause;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringJUnitWebConfig(AbstractServiceTest.Config.class)
@SpringBootTest(classes = {TestDataBaseConfig.class, TestIntegrationConfig.class})
@ExtendWith(TimingExtension.class)
@Transactional
abstract class AbstractServiceTest {

    @Configuration
    @ComponentScan({"by.obs.portal"})
    static class Config {
    }

    <T extends Throwable> void validateRootCause(Runnable runnable, Class<T> exceptionClass) {
        assertThrows(exceptionClass, () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw getRootCause(e);
            }
        });
    }
}