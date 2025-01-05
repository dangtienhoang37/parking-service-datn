package datn.service.parking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;

@Component
public class JWTDecoder implements JwtDecoder {
    private final String signerKey;

    // Tiêm SIGNER_KEY từ application.properties
    public JWTDecoder(@Value("${jwt.signerKey}") String signerKey) {
        this.signerKey = signerKey;
    }
    @Override
    public Jwt decode(String token) throws JwtException {


            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(),"HS512");
            return NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build().decode(token);

    }
}
