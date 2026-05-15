package estopa.agenda_facil.controller;

import estopa.agenda_facil.model.dto.DepositoDto;
import estopa.agenda_facil.model.entity.Usuario;
import estopa.agenda_facil.service.CarteiraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carteiras")
@RequiredArgsConstructor
public class CarteiraController {

    private final CarteiraService carteiraService;

    @GetMapping("/saldo")
    public ResponseEntity<Double> consultarSaldo(@AuthenticationPrincipal Usuario usuarioLogado) {
        Double saldo = carteiraService.consultarSaldo(usuarioLogado.getId());
        return ResponseEntity.ok(saldo);
    }

    @PostMapping("/deposito")
    public ResponseEntity<String> depositar(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody @Valid DepositoDto dto) {

        carteiraService.depositar(usuarioLogado.getId(), dto.valor());
        return ResponseEntity.ok("Depósito de R$ " + dto.valor() + " realizado com sucesso!");
    }
}