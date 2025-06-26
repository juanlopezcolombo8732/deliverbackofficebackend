package ar.com.deliverar.deliver.controller;
import com.unboundid.ldap.sdk.*;
import com.unboundid.ldap.sdk.extensions.StartTLSExtendedRequest;
import com.unboundid.util.ssl.*;

import javax.net.ssl.SSLSocketFactory;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class LoginController {



    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String,String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null) {
            return ResponseEntity
                    .badRequest()
                    .body("❌ Parámetros 'username' y 'password' son requeridos");
        }

        String domain    = "DELIVERAR";
        String fqdnUser  = domain + "\\" + username;
        String ldapHost  = "ad.deliver.ar";
        int    ldapPort  = 389;

        LDAPConnection connection = null;
        try {
            // 1. Crear contexto StartTLS
            SSLUtil sslUtil = new SSLUtil(new TrustAllTrustManager());  // producción: TrustStore real
            SSLSocketFactory sslSocketFactory = sslUtil.createSSLSocketFactory();

            // 2. Conectar y elevar a TLS
            connection = new LDAPConnection(ldapHost, ldapPort);
            connection.processExtendedOperation(
                    new StartTLSExtendedRequest(sslSocketFactory)
            );

            // 3. Intentar bind con las credenciales del usuario
            BindResult bindResult = connection.bind(fqdnUser, password);

            if (bindResult.getResultCode() == ResultCode.SUCCESS) {
                return ResponseEntity.ok("✅ User authenticated successfully");
            } else {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("❌ Invalid credentials: " + bindResult.getDiagnosticMessage());
            }

        } catch (LDAPException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ LDAP error: " + e.getDiagnosticMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ General error: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

}
