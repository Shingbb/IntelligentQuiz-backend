package com.shing.intelligentquiz.common;

import com.shing.intelligentquiz.MainApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author shing
 */
@SpringBootTest(
        classes = { MainApplication.class, },
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {"spring.profiles.active=local"}
)
@Slf4j
@AutoConfigureMockMvc
public class TestBase {
}
