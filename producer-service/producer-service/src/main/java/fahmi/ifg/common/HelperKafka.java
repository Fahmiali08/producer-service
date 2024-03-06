package fahmi.ifg.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Map;

public class HelperKafka {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelperKafka.class.getName());

    public static void publishKafka(String csvStream, Emitter<String> emitter) {
        emitter.send(csvStream);
    }

}
