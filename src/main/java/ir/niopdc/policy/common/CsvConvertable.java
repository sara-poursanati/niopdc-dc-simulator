package ir.niopdc.policy.common;

@FunctionalInterface
public interface CsvConvertable {
    String convertToCsv(Object value);
}
