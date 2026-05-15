package estopa.agenda_facil.controller;

import estopa.agenda_facil.model.dto.EstabelecimentoCadastroDto;
import estopa.agenda_facil.service.EstabelecimentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}