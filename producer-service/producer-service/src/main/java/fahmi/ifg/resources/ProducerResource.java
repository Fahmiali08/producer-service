package fahmi.ifg.resources;

import com.opencsv.CSVReader;
import fahmi.ifg.common.HelperKafka;
import fahmi.ifg.response.ProducerResponse;
import fahmi.ifg.service.ProducerService;
import io.vertx.ext.web.multipart.FormDataPart;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Path("/send/csv")
public class ProducerResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerResource.class.getName());

    @Channel("data-csv-producer")
    Emitter<String> emitterSendCsvData;

    @Inject
    ProducerService producerService;

    /*@POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendData(@MultipartForm InputStream csvStream) {
        try {
            if (csvStream != null) {
                // Lanjutkan dengan pemrosesan data
                if (csvStream.available() > 0) {

                    // Proses file CSV
                    byte[] fileBytes = csvStream.readAllBytes();
                    String csvContent = new String(fileBytes, StandardCharsets.UTF_8);
                    HelperKafka.publishKafka(csvContent, emitterSendCsvData);
                    return Response.status(Response.Status.ACCEPTED).entity("Sukses").build();
                } else {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("No data found in the uploaded file")
                            .build();
                }
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Uploaded file is null")
                        .build();
            }
        } catch (Exception e) {
            // Tangani pengecualian
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to process the uploaded file: " + e.getMessage())
                    .build();
        }
    }*/

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendData(@MultipartForm InputStream csvStream
                             ) {
        ProducerResponse producerResponse = new ProducerResponse();
        try {
            // Check if the input stream is not null
//            String fileName = fileDetails.();
//            LOGGER.info("Received file: {}", fileName);

            if (csvStream != null) {
                // Proses file CSV
                byte[] fileBytes = csvStream.readAllBytes();
                String csvContent = new String(fileBytes, StandardCharsets.UTF_8);
                LOGGER.info("csv content {}", csvContent);
                HelperKafka.publishKafka(csvContent, emitterSendCsvData);
                producerResponse.message = "Successfully sent data to consumer";
                producerResponse.status = 200;
            } else {
                producerResponse.message = "Unsupported file format. Only CSV files are allowed.";
                producerResponse.status = 400;
            }
        } catch (IOException e) {
            // Handle IO exceptions
            producerResponse.message = "Failed to process the uploaded file: " + e.getMessage();
            producerResponse.status = 500;
        }

        // Return the response
        return Response.status(producerResponse.status)
                .entity(producerResponse)
                .build();
    }

    // Method to validate if the content is CSV
    private boolean isCSVFileExtension(String fileName) {
        LOGGER.info("FILE NAME {}", fileName);
        return fileName.toLowerCase().endsWith(".csv");
    }

    private String getFileNameFromHeaders(HttpHeaders headers) {
        if (headers != null) {
            MultivaluedMap<String, String> requestHeaders = headers.getRequestHeaders();
            if (requestHeaders != null) {
                List<String> contentDisposition = requestHeaders.get("Content-Disposition");
                if (contentDisposition != null && !contentDisposition.isEmpty()) {
                    for (String header : contentDisposition) {
                        if (header.startsWith("form-data") && header.contains("filename=")) {
                            // Extract the filename from the header
                            String[] parts = header.split(";");
                            for (String part : parts) {
                                if (part.trim().startsWith("filename=")) {
                                    String[] name = part.split("=");
                                    return name[1].trim().replaceAll("\"", "");
                                }
                            }
                        }
                    }
                }
            }
        }
        return null; // Return null if filename is not found in the headers
    }


}
