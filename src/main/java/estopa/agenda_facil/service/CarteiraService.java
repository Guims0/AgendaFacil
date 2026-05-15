package estopa.agenda_facil.service;

import estopa.agenda_facil.exception.RegraNegocioException;
import estopa.agenda_facil.model.entity.Carteira;
import estopa.agenda_facil.model.repository.CarteiraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarteiraService {

    private final CarteiraRepository carteiraRepository;

    public Double consultarSaldo(Long usuarioId) {
        Carteira carteira = buscarCarteiraPorUsuario(usuarioId);
        return carteira.getSaldo();
    }

    @Transactional
    public void depositar(Long usuarioId, Double valor) {
        if (valor <= 0) {
            throw new RegraNegocioException("O valor do depósito deve ser maior que zero.");
        }

        Carteira carteira = buscarCarteiraPorUsuario(usuarioId);

        carteiraRepository.adicionarSaldo(carteira.getId(), valor);
    }

    private Carteira buscarCarteiraPorUsuario(Long usuarioId) {
        return carteiraRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RegraNegocioException("Carteira não encontrada para este usuário."));
    }
}