package vn.com.mbbank.adminportal.core.util;

import java.util.HashMap;
import java.util.Map;

public class StringUtils {
  private StringUtils() {
    throw new UnsupportedOperationException();
  }

  private static final Map<Character, Character> vietnameseSpecialCharacters;

  static {
    var m = new HashMap<Character, Character>();
    m.put('á', 'a');
    m.put('à', 'a');
    m.put('ả', 'a');
    m.put('ã', 'a');
    m.put('ạ', 'a');
    m.put('ă', 'a');
    m.put('ắ', 'a');
    m.put('ằ', 'a');
    m.put('ẳ', 'a');
    m.put('ẵ', 'a');
    m.put('ặ', 'a');
    m.put('â', 'a');
    m.put('ấ', 'a');
    m.put('ầ', 'a');
    m.put('ẩ', 'a');
    m.put('ẫ', 'a');
    m.put('ậ', 'a');
    m.put('đ', 'd');
    m.put('é', 'e');
    m.put('è', 'e');
    m.put('ẻ', 'e');
    m.put('ẽ', 'e');
    m.put('ẹ', 'e');
    m.put('ê', 'e');
    m.put('ế', 'e');
    m.put('ề', 'e');
    m.put('ể', 'e');
    m.put('ễ', 'e');
    m.put('ệ', 'e');
    m.put('í', 'i');
    m.put('ì', 'i');
    m.put('ỉ', 'i');
    m.put('ĩ', 'i');
    m.put('ị', 'i');
    m.put('ó', 'o');
    m.put('ò', 'o');
    m.put('ỏ', 'o');
    m.put('õ', 'o');
    m.put('ọ', 'o');
    m.put('ô', 'o');
    m.put('ố', 'o');
    m.put('ồ', 'o');
    m.put('ổ', 'o');
    m.put('ỗ', 'o');
    m.put('ộ', 'o');
    m.put('ơ', 'o');
    m.put('ớ', 'o');
    m.put('ờ', 'o');
    m.put('ở', 'o');
    m.put('ỡ', 'o');
    m.put('ợ', 'o');
    m.put('ú', 'u');
    m.put('ù', 'u');
    m.put('ủ', 'u');
    m.put('ũ', 'u');
    m.put('ụ', 'u');
    m.put('ư', 'u');
    m.put('ứ', 'u');
    m.put('ừ', 'u');
    m.put('ử', 'u');
    m.put('ữ', 'u');
    m.put('ự', 'u');
    m.put('ý', 'y');
    m.put('ỳ', 'y');
    m.put('ỷ', 'y');
    m.put('ỹ', 'y');
    m.put('ỵ', 'y');
    m.put('Á', 'A');
    m.put('À', 'A');
    m.put('Ả', 'A');
    m.put('Ã', 'A');
    m.put('Ạ', 'A');
    m.put('Ă', 'A');
    m.put('Ắ', 'A');
    m.put('Ằ', 'A');
    m.put('Ẳ', 'A');
    m.put('Ẵ', 'A');
    m.put('Ặ', 'A');
    m.put('Â', 'A');
    m.put('Ấ', 'A');
    m.put('Ầ', 'A');
    m.put('Ẩ', 'A');
    m.put('Ẫ', 'A');
    m.put('Ậ', 'A');
    m.put('Đ', 'D');
    m.put('É', 'E');
    m.put('È', 'E');
    m.put('Ẻ', 'E');
    m.put('Ẽ', 'E');
    m.put('Ẹ', 'E');
    m.put('Ê', 'E');
    m.put('Ế', 'E');
    m.put('Ề', 'E');
    m.put('Ể', 'E');
    m.put('Ễ', 'E');
    m.put('Ệ', 'E');
    m.put('Í', 'I');
    m.put('Ì', 'I');
    m.put('Ỉ', 'I');
    m.put('Ĩ', 'I');
    m.put('Ị', 'I');
    m.put('Ó', 'O');
    m.put('Ò', 'O');
    m.put('Ỏ', 'O');
    m.put('Õ', 'O');
    m.put('Ọ', 'O');
    m.put('Ô', 'O');
    m.put('Ố', 'O');
    m.put('Ồ', 'O');
    m.put('Ổ', 'O');
    m.put('Ỗ', 'O');
    m.put('Ộ', 'O');
    m.put('Ơ', 'O');
    m.put('Ớ', 'O');
    m.put('Ờ', 'O');
    m.put('Ở', 'O');
    m.put('Ỡ', 'O');
    m.put('Ợ', 'O');
    m.put('Ú', 'U');
    m.put('Ù', 'U');
    m.put('Ủ', 'U');
    m.put('Ũ', 'U');
    m.put('Ụ', 'U');
    m.put('Ư', 'U');
    m.put('Ứ', 'U');
    m.put('Ừ', 'U');
    m.put('Ử', 'U');
    m.put('Ữ', 'U');
    m.put('Ự', 'U');
    m.put('Ý', 'Y');
    m.put('Ỳ', 'Y');
    m.put('Ỷ', 'Y');
    m.put('Ỹ', 'Y');
    m.put('Ỵ', 'Y');
    vietnameseSpecialCharacters = m;
  }

  /**
   * Convert kí tự có dấu thành không dấu
   *
   * @param input
   * @return
   */
  public static String deAccent(String input) {
    if (input == null) {
      return null;
    }
    var inputLen = input.length();
    var stringBuilder = new StringBuilder(inputLen);
    for (int i = 0; i < inputLen; ++i) {
      var orgChar = input.charAt(i);
      stringBuilder.append(vietnameseSpecialCharacters.getOrDefault(orgChar, orgChar));
    }
    return stringBuilder.toString();
  }

  public static String subStringByLength(String input, int length) {
    if (input == null) {
      return null;
    }
    return input.length() > length ? input.substring(0, length) : input;
  }

  public static String getOnlyCharAndNumber(String input) {
    if (org.apache.commons.lang3.StringUtils.isEmpty(input)) {
      return "";
    }
    var inputLength = input.length();
    var stringBuilder = new StringBuilder(inputLength);
    for (int i = 0; i < inputLength; ++i) {
      var c = input.charAt(i);
      if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
        stringBuilder.append(c);
      }
    }
    return stringBuilder.toString();
  }

  public static boolean isEmpty(String str) {
    return str == null || str.isBlank();
  }

  public static String replaceAt(String source, char replacementChar, int index) {
    if (source == null) {
      throw new NullPointerException("Source is null");
    }
    if (index < 0 || index >= source.length()) {
      throw new IndexOutOfBoundsException("Index is out of source length");
    }
    var builder = new StringBuilder(source);
    builder.setCharAt(index, replacementChar);
    return builder.toString();
  }
}
