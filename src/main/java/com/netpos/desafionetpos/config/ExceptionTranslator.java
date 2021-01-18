package com.netpos.desafionetpos.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;
import org.zalando.problem.violations.ConstraintViolationProblem;

import javax.annotation.Nullable;


/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 */
@ControllerAdvice
public class ExceptionTranslator implements ProblemHandling, SecurityAdviceTrait {

    private static final String CODE_KEY = "code";
    private static final String MESSAGE_KEY = "message";
    private static final String VIOLATIONS_KEY = "violations";
    private static final String ERR_VALIDATION = "validation errors";

    /**
     * Post-process the Problem payload to add the message key for the front-end if needed.
     */
    @Override
    public ResponseEntity<Problem> process(@Nullable ResponseEntity<Problem> entity, NativeWebRequest request) {
        if (entity == null) {
            return entity;
        }

        ProblemBuilder problemBuilder = Problem.builder()
                .with(CODE_KEY, entity.getStatusCodeValue());

        if (entity.getBody() instanceof ConstraintViolationProblem) {
            problemBuilder
                    .with(VIOLATIONS_KEY, ((ConstraintViolationProblem) entity.getBody()).getViolations())
                    .with(MESSAGE_KEY, ERR_VALIDATION);
        } else {
            problemBuilder
                    .with(MESSAGE_KEY, entity.getBody().getDetail());
        }


        return new ResponseEntity<>(problemBuilder.build(), entity.getHeaders(), entity.getStatusCode());
    }
}
