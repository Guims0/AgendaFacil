package estopa.agenda_facil.controller;

import estopa.agenda_facil.model.dto.EstabelecimentoAtualizacaoDto;
import estopa.agenda_facil.model.dto.EstabelecimentoCadastroDto;
import estopa.agenda_facil.model.entity.Usuario;
import estopa.agenda_facil.service.EstabelecimentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estabelecimentos")
@RequiredArgsConstructor
public class EstabelecimentoController {

    private final EstabelecimentoService estabelecimentoService;

    @PostMapping
    public ResponseEntity<Void> cadastrar(@RequestBody @Valid EstabelecimentoCadastroDto dto) {
        estabelecimentoService.cadastrarEstabelecimento(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Void> atualizar(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody @Valid EstabelecimentoAtualizacaoDto dto) {

        estabelecimentoService.atualizarEstabelecimento(usuarioLogado.getId(), dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> inativarConta(@AuthenticationPrincipal Usuario usuarioLogado) {
        estabelecimentoService.inativarConta(usuarioLogado.getId());
        return ResponseEntity.noContent().build();
    }
}