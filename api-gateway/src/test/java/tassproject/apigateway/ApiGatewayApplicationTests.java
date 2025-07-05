package tassproject.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = ApiGatewayApplication.class,
        properties = {
                "spring.cloud.compatibility-verifier.enabled=false"
        }
)
class ApiGatewayApplicationTests {

    @Test
    void contextLoads() {
    }
}
