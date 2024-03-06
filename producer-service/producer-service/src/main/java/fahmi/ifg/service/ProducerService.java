package fahmi.ifg.service;

import fahmi.ifg.common.HelperKafka;
import fahmi.ifg.response.ProducerResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.Response;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@ApplicationScoped
public class ProducerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerService.class.getName());
    /*@Channel("data-csv-producer")
    Emitter<String> emitterSendCsvData;

    public Response sendData(InputStream csvStream) {
        try {
            if (csvStream != null) {
                if (isCSVFile(csvStream) || isExcelFile(csvStream)) {
                    // Read CSV content from InputStream
                    byte[] fileBytes = csvStream.readAllBytes();
                    String csvContent = new String(fileBytes, StandardCharsets.UTF_8);
                    HelperKafka.publishKafka(csvContent, emitterSendCsvData);
                    return Response.status(Response.Status.ACCEPTED).entity("Sukses")
                            .build();
                }


                // If the file is neither CSV nor Excel, return error response
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Unsupported file format. Only CSV and Excel files are allowed.")
                        .build();
            }else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Uploaded file is null")
                        .build();
            }


        }catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed when process data.")
                    .build();
        }
    }*/

    public boolean isCSVFile(InputStream fileStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream))) {
            // Read the content of the file until the first line that doesn't contain the boundary
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("--")) {
                    // Check if the line contains a comma (,) or any other CSV-specific delimiter
                    return line.contains(",") || line.contains(";") || line.contains("\t");
                }
            }
            return false; // No valid content found
        } catch (IOException e) {
            LOGGER.error("Error occurred while checking CSV file format: {}", e.getMessage());
            return false;
        }
    }

    public boolean isExcelFile(InputStream fileStream) {
        try {
            new XSSFWorkbook(fileStream); // Attempt to create an XSSFWorkbook
            return true; // If successful, it's an Excel file
        } catch (Exception e) {
            try {
                new HSSFWorkbook(fileStream); // Attempt to create an HSSFWorkbook
                return true; // If successful, it's an Excel file
            } catch (Exception ex) {
                return false; // If both attempts fail, it's not an Excel file
            }
        }
    }
}
