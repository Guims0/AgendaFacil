package estopa.agenda_facil.controller;

import estopa.agenda_facil.model.dto.AgendamentoRespostaDto;
import estopa.agenda_facil.model.dto.AgendamentoSolicitarDto;
import estopa.agenda_facil.model.entity.Usuario;
import estopa.agenda_facil.service.AgendamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'ESTABELECIMENTO')")
    public ResponseEntity<AgendamentoRespostaDto> solicitarAgendamento(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody @Valid AgendamentoSolicitarDto dto) {
        AgendamentoRespostaDto resposta = agendamentoService.solicitarAgendamento(usuarioLogado, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping("/meus")
    @PreAuthorize("hasRole('CLIENTE') and !hasRole('ESTABELECIMENTO')")
    public ResponseEntity<List<AgendamentoRespostaDto>> listarAgendamentosDoCliente(@AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.ok(agendamentoService.listarPorCliente(usuarioLogado.getId()));
    }

    @GetMapping("/estabelecimento")
    @PreAuthorize("hasRole('ESTABELECIMENTO')")
    public ResponseEntity<List<AgendamentoRespostaDto>> listarAgendamentosDoEstabelecimento(@AuthenticationPrincipal Usuario usuarioLogado) {
        return ResponseEntity.ok(agendamentoService.listarPorEstabelecimento(usuarioLogado.getId()));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        agendamentoService.cancelarAgendamento(id, usuarioLogado.getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/confirmar")
    @PreAuthorize("hasRole('ESTABELECIMENTO')")
    public ResponseEntity<Void> confirmar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        agendamentoService.confirmarAgendamento(id, usuarioLogado.getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/registrar-pagamento")
    @PreAuthorize("hasRole('ESTABELECIMENTO')")
    public ResponseEntity<Void> registrarPagamento(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        agendamentoService.registrarPagamento(id, usuarioLogado.getId());
        return ResponseEntity.noContent().build();
    }
}