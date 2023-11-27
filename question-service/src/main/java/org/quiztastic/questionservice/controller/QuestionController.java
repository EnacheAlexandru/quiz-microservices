package org.quiztastic.questionservice.controller;

import lombok.RequiredArgsConstructor;
import org.quiztastic.questionservice.dto.*;
import org.quiztastic.questionservice.service.JwtService;
import org.quiztastic.questionservice.service.QuestionService;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/main", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class QuestionController {

//    private final ServletWebServerApplicationContext webServerAppContext;

    private final QuestionService questionService;

    private final JwtService jwtService;

    @GetMapping("/question/user")
    public ResponseEntity<List<GetQuestionResponse>> requestGetQuestionsByUser(
            @RequestHeader(value = "Authorization", required = false) String jwtHeader
    ) {
        String username = getAuthorizedUsername(jwtHeader);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<GetQuestionResponse> questionList = questionService.getQuestionsByUser(username);
            return ResponseEntity.ok(questionList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/question/add")
    public ResponseEntity<Void> requestAddQuestion(
            @RequestHeader(value = "Authorization", required = false) String jwtHeader,
            @RequestBody AddQuestionRequest questionRequest
    ) {
        String username = getAuthorizedUsername(jwtHeader);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            questionService.addQuestion(questionRequest, username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

//    @GetMapping("/port")
//    public Integer getRunningPort() {
//        return webServerAppContext.getWebServer().getPort();
//    }

    @PostMapping("/question/update")
    public ResponseEntity<Void> requestUpdateQuestion(
            @RequestHeader(value = "Authorization", required = false) String jwtHeader,
            @RequestBody UpdateQuestionRequest questionRequest
    ) {
        String username = getAuthorizedUsername(jwtHeader);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            questionService.updateQuestion(questionRequest, username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("question/answer")
    public ResponseEntity<GenericResponse> requestAnswerQuestion(
            @RequestHeader(value = "Authorization", required = false) String jwtHeader,
            @RequestBody AnswerQuestionRequest questionRequest
    ) {
        String username = getAuthorizedUsername(jwtHeader);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String correct = questionService.answerQuestion(questionRequest, username);
            return ResponseEntity.ok(GenericResponse.builder().message(correct).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private String getAuthorizedUsername(String jwtHeader) {
        if (!jwtService.isJwtHeaderValid(jwtHeader)) {
            return null;
        }

        return jwtService.extractUsernameHeader(jwtHeader);
    }

}
