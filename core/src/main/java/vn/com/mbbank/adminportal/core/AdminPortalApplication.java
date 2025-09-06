package vn.com.mbbank.adminportal.core;

import io.hypersistence.utils.spring.repository.BaseJpaRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import vn.com.mbbank.adminportal.core.util.EnvironmentInitializer;

@SpringBootApplication
@EnableScheduling
@ComponentScan({"vn.com.mbbank.adminportal.common", "vn.com.mbbank.adminportal.core"})
@EnableJpaRepositories(value = {"vn.com.mbbank.adminportal.common", "vn.com.mbbank.adminportal.core"}, repositoryBaseClass = BaseJpaRepositoryImpl.class)
@EntityScan({"vn.com.mbbank.adminportal.common", "vn.com.mbbank.adminportal.core"})
public class AdminPortalApplication {
  public static void main(String[] args) {
    EnvironmentInitializer.initialize();
    SpringApplication.run(AdminPortalApplication.class, args);
  }
}