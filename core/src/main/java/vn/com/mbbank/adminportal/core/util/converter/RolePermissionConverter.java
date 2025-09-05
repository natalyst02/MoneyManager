package vn.com.mbbank.adminportal.core.util.converter;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.runtime.Generics;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import vn.com.mbbank.adminportal.common.util.Json;

import java.nio.charset.StandardCharsets;
import java.util.Map;


@Converter
public class RolePermissionConverter implements AttributeConverter<Map<String, Integer>, String> {
  private static final JsonWriter.WriteObject<Map<String, Integer>> ROLE_PERMISSION_WRITE_OBJECT = Json.findWriter(Generics.makeParameterizedType(Map.class, String.class, Integer.class));
  private static final JsonReader.ReadObject<Map<String, Integer>> ROLE_PERMISSION_READ_OBJECT = Json.findReader(Generics.makeParameterizedType(Map.class, String.class, Integer.class));

  @Override
  public String convertToDatabaseColumn(Map<String, Integer> attribute) {
    return attribute != null ? Json.encodeToString(attribute, ROLE_PERMISSION_WRITE_OBJECT) : null;
  }

  @Override
  public Map<String, Integer> convertToEntityAttribute(String dbData) {
    return dbData != null ? Json.decode(dbData.getBytes(StandardCharsets.UTF_8), ROLE_PERMISSION_READ_OBJECT) : null;
  }
}
