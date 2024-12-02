package pt.isec.pd.spring_boot.exemplo3.security;

import Utils.Utilizador;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pt.isec.pd.spring_boot.exemplo3.controllers.DatabaseController;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider
{
    DatabaseController dbc = new DatabaseController();

    public UserAuthenticationProvider() throws SQLException {
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        Utilizador u = dbc.getUser(username);

        if (username.equals(u.getEmail()) && password.equals(u.getPassword())) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            if(u.getAdmin() == 1) {
                System.out.println("ADMINNNNNNNNNNNN");
                authorities.add(new SimpleGrantedAuthority("ADMIN"));
            }else{
                authorities.add(new SimpleGrantedAuthority("USER"));
            }
            return new UsernamePasswordAuthenticationToken(username, password, authorities);
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication)
    {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
