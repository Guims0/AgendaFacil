package estopa.agenda_facil.controller;

import estopa.agenda_facil.model.dto.ClienteAtualizacaoDto;
import estopa.agenda_facil.model.dto.ClienteCadastroDto;
import estopa.agenda_facil.model.entity.Usuario;
import estopa.agenda_facil.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<Void> cadastrar(@RequestBody @Valid ClienteCadastroDto dto) {
        clienteService.cadastrarCliente(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Void> atualizar(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody @Valid ClienteAtualizacaoDto dto) {

        clienteService.atualizarCliente(usuarioLogado.getId(), dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> inativarConta(@AuthenticationPrincipal Usuario usuarioLogado) {
        clienteService.inativarConta(usuarioLogado.getId());
        return ResponseEntity.noContent().build();
    }
}