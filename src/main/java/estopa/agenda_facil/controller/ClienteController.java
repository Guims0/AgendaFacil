package estopa.agenda_facil.controller;

import estopa.agenda_facil.model.dto.ClienteCadastroDto;
import estopa.agenda_facil.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}