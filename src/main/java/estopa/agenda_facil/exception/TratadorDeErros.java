package estopa.agenda_facil.exception;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<String> tratarErroDeRegraDeNegocio(RegraNegocioException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErroValidacaoDto>> tratarErroDeValidacaoDosDtos(MethodArgumentNotValidException ex) {
        List<FieldError> erros = ex.getFieldErrors();
        List<ErroValidacaoDto> errosMapeados = erros.stream()
                .map(erro -> new ErroValidacaoDto(erro.getField(), erro.getDefaultMessage()))
                .toList();

        return ResponseEntity.badRequest().body(errosMapeados);
    }
}
