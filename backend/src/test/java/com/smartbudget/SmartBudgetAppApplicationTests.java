package com.smartbudget;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.smartbudget.repository.CategoryRepository;
import com.smartbudget.repository.TransactionRepository;
import com.smartbudget.repository.UserRepository;

import javax.sql.DataSource;

@SpringBootTest(properties = {
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
				"org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration," +
				"org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration," +
				"org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration," +
				"org.springframework.boot.autoconfigure.data.jpa.JpaAuditingAutoConfiguration",
		"spring.data.jpa.repositories.enabled=false"
})
class SmartBudgetAppApplicationTests {

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private CategoryRepository categoryRepository;

	@MockBean
	private TransactionRepository transactionRepository;

	@MockBean
	private DataSource dataSource;

	@Test
	void contextLoads() {
	}
}
