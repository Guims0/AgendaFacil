package estopa.agenda_facil.controller;

import estopa.agenda_facil.model.dto.ServicoCadastroDto;
import estopa.agenda_facil.model.dto.ServicoAtualizacaoDto;
import estopa.agenda_facil.model.dto.ServicoRespostaDto;
import estopa.agenda_facil.model.entity.Usuario;
import estopa.agenda_facil.service.ServicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicos")
@RequiredArgsConstructor
public class ServicoController {

    private final ServicoService servicoService;

    @PostMapping
    @PreAuthorize("hasRole('ESTABELECIMENTO')")
    public ResponseEntity<Void> cadastrar(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody @Valid ServicoCadastroDto dto) {
        servicoService.cadastrarServico(usuarioLogado.getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/estabelecimento/{idEstabelecimento}")
    public ResponseEntity<List<ServicoRespostaDto>> listarPorEstabelecimento(
            @PathVariable Long idEstabelecimento) {
        var lista = servicoService.listarServicosDoEstabelecimento(idEstabelecimento);
        return ResponseEntity.ok(lista);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ESTABELECIMENTO')")
    public ResponseEntity<Void> atualizar(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @PathVariable Long id,
            @RequestBody @Valid ServicoAtualizacaoDto dto) {
        servicoService.atualizarServico(usuarioLogado.getId(), id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ESTABELECIMENTO')")
    public ResponseEntity<Void> deletar(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @PathVariable Long id) {
        servicoService.deletarServico(usuarioLogado.getId(), id);
        return ResponseEntity.noContent().build();
    }
}