// src/main/java/cz/demo/todo/TodoApplication.java
// tohle je vlastne Program.cs ekvivalent

package cz.stvr.azul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

// tohle je analog k Builder procesu v .NET
@SpringBootApplication
@ConfigurationPropertiesScan // injection of properties (res/applicaton.properties) <-- pripojeni DB

// entry point
public class AzulApplication {
    public static void main(String[] args) {
        SpringApplication.run(AzulApplication.class, args);
    }
}
