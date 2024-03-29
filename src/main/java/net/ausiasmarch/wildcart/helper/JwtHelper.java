package net.ausiasmarch.wildcart.helper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import net.ausiasmarch.wildcart.exception.JWTException;

public class JwtHelper {

    private static final String SECRET = "HOLAHOLAasdfghjklHOLAHOLA";
    private static final String ISSUER = "WILDCART AUSIAS";

    private static SecretKey secretKey() {
        return Keys.hmacShaKeyFor((SECRET + ISSUER + SECRET).getBytes());
    }

    public static String generateJWT(String name) {

        Date currentTime = Date.from(Instant.now());
        Date expiryTime = Date.from(Instant.now().plus(Duration.ofSeconds(9600)));

        return Jwts.builder()
               .setId(UUID.randomUUID().toString())
               .setIssuer(ISSUER)
               .setIssuedAt(currentTime)
               .setExpiration(expiryTime)
               .claim("name", name)
               .signWith(secretKey())
               .compact();
    }

    public static String validateJWT(String strJWT) {
        Jws<Claims> headerClaimsJwt = Jwts.parserBuilder()
               .setSigningKey(secretKey())
               .build()
               .parseClaimsJws(strJWT);

        Claims claims = headerClaimsJwt.getBody();

        if (claims.getExpiration().before(new Date())) {
            throw new JWTException("Error validating JWT: token expired");
        }

        if (!claims.getIssuer().equals(ISSUER)) {
            throw new JWTException("Error validating JWT: wrong issuer");
        }
        
        return claims.get("name", String.class);
    }
}
