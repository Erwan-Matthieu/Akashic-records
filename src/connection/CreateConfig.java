package connection;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CreateConfig {
    
    private static String FILE = "config.json";
    private static String relativePath = "res" + File.separator + "conn" + File.separator + FILE;

    public static void createConfigFile(String[] parameters) throws StreamWriteException, DatabindException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        ObjectNode jsonObjectNode = objectMapper.createObjectNode();

        jsonObjectNode.put("Database",parameters[0]);
        jsonObjectNode.put("Host",parameters[1]);
        jsonObjectNode.put("Server Name",parameters[2]);
        jsonObjectNode.put("Port",parameters[3]);
        jsonObjectNode.put("Username",parameters[4]);
        jsonObjectNode.put("Password",parameters[5]);

        objectMapper.writeValue(new File(relativePath), jsonObjectNode);
    }

    public static void main(String[] args) throws StreamWriteException, DatabindException, IOException {
        String[] parameters = {"PostgreSQL","localhost","church","5243","admin_church","admin"};

        createConfigFile(parameters);
    }
}
