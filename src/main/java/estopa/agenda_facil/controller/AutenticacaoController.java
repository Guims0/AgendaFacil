package estopa.agenda_facil.controller;

import estopa.agenda_facil.infra.security.TokenService;
import estopa.agenda_facil.model.dto.AutenticacaoDto;
import estopa.agenda_facil.model.dto.TokenRespostaDto;
import estopa.agenda_facil.model.entity.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AuthenticationManager manager;
    private final TokenService tokenService;

    @PostMapping
    public ResponseEntity<TokenRespostaDto> efetuarLogin(@RequestBody @Valid AutenticacaoDto dto) {

        var tokenDeAutenticacao = new UsernamePasswordAuthenticationToken(dto.email(), dto.senha());
        var autenticacao = manager.authenticate(tokenDeAutenticacao);

        if (autenticacao.getPrincipal() instanceof Usuario usuario) {
            var tokenJWT = tokenService.gerarToken(usuario);
            return ResponseEntity.ok(new TokenRespostaDto(tokenJWT));
        }

        throw new IllegalStateException("Erro de infraestrutura: O objeto autenticado não é do tipo Usuario.");
    }
}