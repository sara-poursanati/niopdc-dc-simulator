package ir.niopdc.common;

@FunctionalInterface
public interface CsvConvertable {
    String convertToCsv(Object value);
}
