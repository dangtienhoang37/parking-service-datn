package datn.service.parking.utils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.experimental.NonFinal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Component
public class JWToken {
    @NonFinal
    protected static final String SIGNER_KEY = "Ry5OGtcJ+5dTSUV30C1LMaNew2uOotz0zpaBT/F9DJ5fLbcC5EoYtK/Ldh3H8VZo";


    public boolean verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expireTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        return signedJWT.verify(verifier) && expireTime.after(new Date());
    }
    public Jwt jwtDecoder(String token) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNER_KEY.getBytes(),"HS512");
        return NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build().decode(token);
    }
    public  String TokenConcat (String token) {
        return token.substring(7);
    }

    public String getToken(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getCredentials() != null) {
            return authentication.getCredentials().toString();
        }
        else {
            return null;
        }
    }
    public UUID getIdFromToken(String token){
        var targetJWToken = jwtDecoder(TokenConcat(token));
        return UUID.fromString(targetJWToken.getClaim("Id"));
    }
    public String getRoleFromToken(String token){
        var targetJWToken = jwtDecoder(TokenConcat(token));
        return targetJWToken.getClaim("Role");
    }


}
