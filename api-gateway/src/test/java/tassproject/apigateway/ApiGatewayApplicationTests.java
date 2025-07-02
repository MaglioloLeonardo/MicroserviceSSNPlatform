// api-gateway/src/test/java/tassproject/apigateway/ApiGatewayApplicationTests.java
package tassproject.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = ApiGatewayApplication.class,
        properties = {
                // disabilito il verifier che altrimenti blocca la 3.1.4 + Cloud 2023.0.3
                "spring.cloud.compatibility-verifier.enabled=false"
        }
)
class ApiGatewayApplicationTests {

    @Test
    void contextLoads() {
    }

}
