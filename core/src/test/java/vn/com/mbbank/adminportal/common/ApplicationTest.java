package vn.com.mbbank.adminportal.common;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import vn.com.mbbank.adminportal.core.util.EnvironmentInitializer;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public abstract class ApplicationTest {
    static {
        EnvironmentInitializer.initialize();
    }
}
