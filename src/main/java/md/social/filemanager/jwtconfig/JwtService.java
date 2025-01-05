package md.social.filemanager.jwtconfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import md.social.filemanager.exception.FileManagerBaseException;
import md.social.filemanager.model.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class JwtService implements UserDetailsService
{
    private final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private final String JWT_ROLE_CLAIM_KEY = "roles";

    private final String JWT_SECRET_KEY;

    private final String JWT_ISSUER;

    private final String JWT_AUDIENCE;


    @Autowired
    public JwtService(@Value("${jwt.secret.key}") String JWT_SECRET_KEY,
                      @Value("${jwt.iss}") String jwtIssuer,
                      @Value("${jwt.aud}") String jwtAudience)
    {
        this.JWT_SECRET_KEY = JWT_SECRET_KEY;
        JWT_ISSUER = jwtIssuer;
        JWT_AUDIENCE = jwtAudience;
    }

    private SecretKey getSigningKey()
    {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String getUserNameFromJwtToken(String token)
    {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    private List<UserRole> getUserRoleFromJwtToken(String token)
    {
        final ObjectMapper objectMapper = new ObjectMapper();
        
        Jws<Claims> claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);

        String roles = claims.getPayload().get(JWT_ROLE_CLAIM_KEY, String.class);

        List<UserRole> userRoles;
        try {
            userRoles = objectMapper.readValue(roles, new TypeReference<List<UserRole>>() {});
        } catch (JsonProcessingException e) {
            logger.error("Invalid JWT claim [ {} ]", JWT_ROLE_CLAIM_KEY);
            throw new FileManagerBaseException(String.format("Invalid JWT claim [ %s ]", JWT_ROLE_CLAIM_KEY),
                    HttpStatus.FORBIDDEN.getReasonPhrase(), HttpStatus.FORBIDDEN.value());
        }

        if (userRoles.isEmpty()){
            logger.error("User [ {} ] have 0 roles", getUserRoleFromJwtToken(token));
            throw new FileManagerBaseException(String.format("User [ %s ] have 0 roles", getUserRoleFromJwtToken(token)),
                    HttpStatus.FORBIDDEN.getReasonPhrase(), HttpStatus.FORBIDDEN.value());
        }

        return userRoles;
    }

    public boolean validateJwtToken(String authToken)
    {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);

            if (claimsJws.getPayload().getExpiration().before(new Date())){
                throw new UnsupportedJwtException("Jwt Token is expired");
            }

            if (!claimsJws.getPayload().getIssuer().equals(JWT_ISSUER)){
                throw new UnsupportedJwtException("Invalid issuer");
            }

            if (!claimsJws.getPayload().getAudience().contains(JWT_AUDIENCE)){
                throw new UnsupportedJwtException("Invalid audience");
            }

            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String jwt) throws UsernameNotFoundException
    {
        return new User(getUserNameFromJwtToken(jwt), null,
                getUserRoleFromJwtToken(jwt)
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(role.name()))
                        .toList());
    }
}
