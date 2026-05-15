package estopa.agenda_facil.controller;

import estopa.agenda_facil.model.dto.AgendamentoRespostaDto;
import estopa.agenda_facil.model.dto.AgendamentoSolicitarDto;
import estopa.agenda_facil.model.entity.Usuario;
import estopa.agenda_facil.service.AgendamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    @PostMapping
    public ResponseEntity<AgendamentoRespostaDto> solicitarAgendamento(

            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody @Valid AgendamentoSolicitarDto dto) {
        
        AgendamentoRespostaDto resposta = agendamentoService.solicitarAgendamento(usuarioLogado.getId(), dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }
}