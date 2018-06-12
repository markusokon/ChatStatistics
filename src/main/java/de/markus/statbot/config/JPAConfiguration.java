package de.markus.statbot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;

/**
 * Gibt den Ort der Repositories an
 *
 * @author Nils Bauer
 *
 */
@Configuration
@EnableJpaRepositories(basePackages = { "de.markus.statbot.repositories" })
public class JPAConfiguration extends JpaRepositoryConfigExtension {

}
