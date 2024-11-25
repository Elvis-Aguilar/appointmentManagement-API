package com.appointment.management.domain.service.auth;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class TemplateRendererServiceTest {

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private TemplateRendererService templateRendererService;

    //variables globales para el Given global
    private String template;
    private Map<String, Object> variables;
    private String renderedContent;

    @BeforeEach
    void setUp() {
        // Given global
        template = "testTemplate";
        variables = new HashMap<>();
        variables.put("key", "value");

        renderedContent = "Rendered content based on template and variables";

        // When: Global
        when(templateEngine.process(eq(template), any(Context.class))).thenReturn(renderedContent);
    }

    @Test
    void renderTemplate_ShouldReturnRenderedContent_WhenTemplateAndVariablesAreProvided() {
        // when:
        String result = templateRendererService.renderTemplate(template, variables);

        // then:
        assertEquals(renderedContent, result);
        verify(templateEngine).process(eq(template), any(Context.class));
    }
}
