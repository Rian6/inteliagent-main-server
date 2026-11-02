package com.inteliagent.main_server.configuration;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.dao.EmptyResultDataAccessException;
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    ResponseEntity<ProblemDetail> status(org.springframework.web.server.ResponseStatusException e) { return problem(e.getStatusCode().value(), "Registro não encontrado"); }
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ProblemDetail> invalid(IllegalArgumentException e) { return problem(400, "Dados inválidos"); }
    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ProblemDetail> forbidden(AccessDeniedException e) { return problem(403, "Acesso negado"); }
    @ExceptionHandler(EmptyResultDataAccessException.class)
    ResponseEntity<ProblemDetail> missing(EmptyResultDataAccessException e) { return problem(404, "Registro não encontrado"); }
    @ExceptionHandler({org.springframework.dao.DataIntegrityViolationException.class, org.springframework.http.converter.HttpMessageNotReadableException.class})
    ResponseEntity<ProblemDetail> invalidPayload(Exception e) { return problem(400, "Dados inválidos"); }
    @ExceptionHandler(com.inteliagent.main_server.service.RoutingBusyException.class)
    ResponseEntity<ProblemDetail> busy(com.inteliagent.main_server.service.RoutingBusyException e) {
        return ResponseEntity.status(503).header("Retry-After", "1").body(ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, "Otimização ocupada. Tente novamente."));
    }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ProblemDetail> failure(Exception e) {
        org.slf4j.LoggerFactory.getLogger(getClass()).error("Request failed", e);
        return problem(500, "Não foi possível concluir a operação");
    }
    private ResponseEntity<ProblemDetail> problem(int status, String message) { return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(HttpStatus.valueOf(status), message)); }
}
