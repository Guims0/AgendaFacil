package estopa.agenda_facil.infra.security;

import estopa.agenda_facil.exception.RegraNegocioException;
import estopa.agenda_facil.model.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FiltroDeSeguranca extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var tokenJWT = recuperarToken(request);


        if (tokenJWT != null) {

            var emailUsuario = tokenService.getSubject(tokenJWT);

            var usuario = usuarioRepository.findByEmail(emailUsuario)
                    .orElseThrow(() -> new RegraNegocioException("Token válido, mas o usuário não foi encontrado no banco de dados."));

            var autenticacao = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(autenticacao);
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {

        var cabecalhoAutorizacao = request.getHeader("Authorization");

        if (cabecalhoAutorizacao != null) {

            return cabecalhoAutorizacao.replace("Bearer ", "");
        }
        return null;
    }
}