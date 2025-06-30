package com.mintit.incentive.configure;

import io.r2dbc.proxy.ProxyConnectionFactory;
import io.r2dbc.proxy.core.Binding;
import io.r2dbc.proxy.core.Bindings;
import io.r2dbc.proxy.core.QueryInfo;
import io.r2dbc.proxy.support.QueryExecutionInfoFormatter;
import io.r2dbc.spi.ConnectionFactory;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.mariadb.r2dbc.MariadbConnectionConfiguration;
import org.mariadb.r2dbc.MariadbConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/***
 * R2DBC 연결 정보 정의
 */
@Configuration
@EnableR2dbcRepositories
@EnableTransactionManagement
@Slf4j
public class R2dbcConfig extends AbstractR2dbcConfiguration {

    @Value("${spring.r2dbc.port}")
    private int port;

    @Value("${spring.r2dbc.username}")
    private String username;

    @Value("${spring.r2dbc.password}")
    private String password;

    @Value("${spring.r2dbc.database}")
    private String database;

    @Value("${spring.r2dbc.host}")
    private String host;

    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactory original = new MariadbConnectionFactory(MariadbConnectionConfiguration.builder()
                                                                                                .host(host)
                                                                                                .port(port)
                                                                                                .username(username)
                                                                                                .password(password)
                                                                                                .database(database)
                                                                                                .build());
        QueryExecutionInfoFormatter formatter = new QueryExecutionInfoFormatter();
        formatter.addConsumer((executionInfo, sb) -> {
            List<QueryInfo> queries = executionInfo.getQueries();
            if (!queries.isEmpty()) {
                String sqlQuery = queries.stream()
                                         .map(QueryInfo::getQuery)
                                         .collect(Collectors.joining("\",\"", "\"", "\""));
                sqlQuery = sqlQuery.replace("\"", "");
                for (QueryInfo queryInfo : queries) {
                    for (Bindings binds : queryInfo.getBindingsList()) {
                        SortedSet<Binding> indexBindings = binds.getIndexBindings();
                        if (!indexBindings.isEmpty()) {
                            for (Binding b : indexBindings) {
                                StringBuilder sbForBindings = new StringBuilder();
                                formatter.onBoundValue.accept(b.getBoundValue(), sbForBindings);
                                if (b.getBoundValue().getValue() instanceof String) {
                                    sqlQuery = sqlQuery.replaceFirst("\\?", "'" + sbForBindings.toString() + "'");
                                } else {
                                    sqlQuery = sqlQuery.replaceFirst("\\?", sbForBindings.toString());
                                }
                            }
                        }
                    }
                }
                sb.append("SQL Time [");
                sb.append(executionInfo.getExecuteDuration().toMillis());
                sb.append("]");
                sb.append(" Executing SQL statement [");
                sb.append(sqlQuery);
                sb.append("]");

            }

        });
        return ProxyConnectionFactory.builder(original).onAfterQuery(queryInfo -> {
            log.info(formatter.format(queryInfo));
        }).build();
    }

    @Bean
    public ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }

    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }
}
