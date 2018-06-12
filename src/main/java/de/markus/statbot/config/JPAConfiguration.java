package de.markus.statbot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;

/**
 * declares the location of repositories
 *
 * @author Nils Bauer
 *
 */
@Configuration
@EnableJpaRepositories(basePackages = { "de.markus.statbot.repositories" })
public class JPAConfiguration extends JpaRepositoryConfigExtension {

}
