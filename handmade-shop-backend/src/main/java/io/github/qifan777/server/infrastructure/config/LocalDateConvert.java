package io.github.qifan777.server.infrastructure.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@JsonComponent
@Slf4j
public class LocalDateConvert {

    public static class Serializer extends JsonSerializer<LocalDate> {


        @Override
        public void serialize(LocalDate localDateTime, JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider) throws IOException {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd");
            String format = dateTimeFormatter.format(localDateTime);
            jsonGenerator.writeString(format);
        }
    }

    public static class Deserializer extends JsonDeserializer<LocalDate> {


        @Override
        public LocalDate deserialize(JsonParser jsonParser,
                                     DeserializationContext deserializationContext) throws IOException {
            String text = jsonParser.getText();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd");
            return LocalDate.parse(text, dateTimeFormatter);
        }
    }
}
