package edu.sabanciuniv.cs308ecommercebackend.handlers;

import edu.sabanciuniv.cs308ecommercebackend.models.payloads.TeknocsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionIntercept
{

    @ExceptionHandler(Exception.class)
    public TeknocsResponse<Exception> handleGeneralError(Exception e)
    {
        return new TeknocsResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unknown error has occured on the server. Please take note of the error details to report this.",
                e
        );
    }

}
