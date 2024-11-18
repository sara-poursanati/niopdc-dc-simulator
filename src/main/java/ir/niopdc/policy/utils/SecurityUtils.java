package ir.niopdc.policy.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


@UtilityClass
public class SecurityUtils {

  public static String createSHA512Hash(File file) throws IOException {
    try (FileInputStream fis = new FileInputStream(file)) {
      return DigestUtils.sha512Hex(fis);
    }
  }

  public static boolean verifyFileSHA512Hash(File file, String expectedHash) throws IOException {
      String calculatedHash = createSHA512Hash(file);
      return calculatedHash.equals(expectedHash);
  }
}
