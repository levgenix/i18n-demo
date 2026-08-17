package com.example.i18n;

import java.util.Locale;

import java.lang.reflect.Type;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import com.example.model.LocalizedRequest;

@ControllerAdvice
public class LanguageRequestAdvice extends RequestBodyAdviceAdapter {

    @Override
    public boolean supports(
            MethodParameter methodParameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType) {
        return LocalizedRequest.class
                .isAssignableFrom(
                        methodParameter.getParameterType());
    }

    /**
     * Устанавливает локаль, получаемую из метода LocalizedRequest#requestLang.
     * Перед этим ресетит текущую пользовательскую локаль,
     * чтобы не была установлена системная, если локаль не найдена в
     * scenarioDto.serviceInfoDto.queryParams.lang
     * 
     * @return тело запроса
     */
    @Override
    public Object afterBodyRead(
            Object body,
            HttpInputMessage inputMessage,
            MethodParameter parameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType) {
        // Очищаем локаль от предыдущего запроса.
        // Потоки в Tomcat используются повторно (pool), и если не очистить,
        // то локаль от прошлого пользователя может случайно достаться новому запросу.
        LocaleContextHolder.resetLocaleContext();

        LocalizedRequest request = (LocalizedRequest) body;
        String requestLang = request.requestLang();
        if (!StringUtils.isEmpty(requestLang)) {
            LocaleContextHolder.setLocale(Locale.forLanguageTag(requestLang));
        }

        return body;
    }
}
