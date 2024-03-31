package connection;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.util.Map;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Connection {

    private static java.sql.Connection connection;

    public static java.sql.Connection getConnection() throws Exception {
        DatabaseConfiguration config = new DatabaseConfiguration();
        
        if (connection == null) {
            Class.forName(config.driver);

            connection = DriverManager.getConnection(config.databaseLink, config.username, config.password);
            connection.setAutoCommit(false);
        }

        return connection;
    }

    private static class DatabaseConfiguration {

        private static String FILE = "config.json";
        private static String relativePath = "res" + File.separator + "conn" + File.separator + FILE;

        private static String PostgreSQL = "org.postgresql.Driver";
        private static String PostgreSQLLink = "jdbc:postgresql://";
        
        public String   database,
                        databaseLink,
                        serverName,
                        host,
                        username,
                        password,
                        driver;

        public int port;

        public DatabaseConfiguration() throws StreamReadException, DatabindException, IOException {
            setConfig();
        }

        public void setServerName(String serverName) {
            this.serverName = serverName;
        }

        public void setServerName(Object serverName) {
            this.setServerName(serverName.toString());
        }

        public void setDatabaseLink(String databaseLink) {
            this.databaseLink = databaseLink;
        }

        public void setDatabaseLink() {
            if (database.equals("PostgreSQL")) {
                this.setDatabaseLink(PostgreSQLLink + this.host + ":" + this.port + "/" + this.serverName);
            }
        }

        public void setDatabase(String database) {
            this.database = database;
        }

        public void setDatabase(Object database) {
            this.setDatabase(database.toString());
        }

        public void setHost(String host) {
            this.host = host;
        }

        public void setHost(Object host) {
            this.setHost(host.toString());
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setUsername(Object username) {
            this.setUsername(username.toString());
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setPassword(Object password) {
            this.setPassword(password.toString());
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }

        public void setDriver() {
            if (database.equals("PostgreSQL")) {
                this.setDriver(PostgreSQL);
            }
        }

        public void setPort(int port) {
            this.port = port;
        }

        public void setPort(Object port) {
            this.setPort(Integer.parseInt(port.toString()));
        }


        private void setConfig() throws StreamReadException, DatabindException, IOException {
            Map<String,Object> parameters = getParameters();
            this.setDatabase(parameters.get("Database"));
            this.setServerName(parameters.get("Server Name"));
            this.setHost(parameters.get("Host"));
            this.setPort(parameters.get("Port"));
            this.setUsername(parameters.get("Username"));
            this.setPassword(parameters.get("Password"));
            this.setDriver();
            this.setDatabaseLink();
        }

        public static Map<String,Object> getParameters() throws StreamReadException, DatabindException, IOException {
            
            ObjectMapper objectMapper = new ObjectMapper();
            
            @SuppressWarnings("unchecked")
            Map<String,Object> parameters = objectMapper.readValue(new File(relativePath), Map.class);

            return parameters;
        }

    }
}
