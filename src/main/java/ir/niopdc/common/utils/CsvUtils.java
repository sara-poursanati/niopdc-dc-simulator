package ir.niopdc.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CsvUtils {

    @Value("${app.csv.delimiter}")
    private char delimiter;
    @Value("${app.csv.quote}")
    private char quote;
    @Value("${app.csv.lineSeparator}")
    private String lineSeparator;

    public String convertToCsv(Object value) {
        final CsvMapper mapper = new CsvMapper();
        final CsvSchema schema = mapper.schemaFor(value.getClass())
                .withColumnSeparator(delimiter)
                .withQuoteChar(quote)
                .withLineSeparator(lineSeparator);
        try {
            return mapper.writer(schema).writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
