package ir.niopdc.policy.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.FileInputStream;
import java.io.IOException;


@UtilityClass
public class SecurityUtils {

  public String createSHA512Hash(String filePath) throws IOException {
    try (FileInputStream fis = new FileInputStream(filePath)) {
      return DigestUtils.sha512Hex(fis);
    }
  }

  public boolean verifyFileSHA512Hash(String filePath, String expectedHash) throws IOException {
      String calculatedHash = createSHA512Hash(filePath);
      return calculatedHash.equals(expectedHash);
  }
}
