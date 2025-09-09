package com.sap.refactoring;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class JavaRefactoringTestApplication {

  public static void main(String[] args) {
    SpringApplication.run(JavaRefactoringTestApplication.class, args);
  }

}
