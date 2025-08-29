package pcshop.pcshop.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

public class TokenHash {
    private static final HexFormat HEX = HexFormat.of();

    public static String sha256(String raw){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HEX.formatHex(dig);
        }catch (Exception e){
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
    private TokenHash(){}
}
