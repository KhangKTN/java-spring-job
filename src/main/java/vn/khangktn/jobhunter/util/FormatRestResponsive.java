package vn.khangktn.jobhunter.util;

import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import jakarta.servlet.http.HttpServletResponse;
import vn.khangktn.jobhunter.domain.response.RestResponse;
import vn.khangktn.jobhunter.util.annotation.ApiMessage;

@ControllerAdvice
public class FormatRestResponsive implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(status);

        if(body instanceof String || body instanceof Resource) return body;

        // Error case
        if(status >= 400){
            return body;
        }
        // Success case
        else{
            // Custom message for each response
            ApiMessage message = returnType.getMethodAnnotation(ApiMessage.class);
            res.setMessage(message == null ? "Successfully!" : message.value());
            res.setData(body);
        }
        return res;
    }
}
