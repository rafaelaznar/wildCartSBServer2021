/*
 * Copyright (c) 2021
 *
 * by Rafael Angel Aznar Aparici (rafaaznar at gmail dot com) & 2021 DAW students
 *
 * WILDCART: Free Open Source Shopping Site
 *
 * Sources at:                https://github.com/rafaelaznar/wildCartSBServer2021
 * Database at:               https://github.com/rafaelaznar/wildCartSBServer2021
 * POSTMAN API at:            https://github.com/rafaelaznar/wildCartSBServer2021
 * Client at:                 https://github.com/rafaelaznar/wildCartAngularClient2021
 *
 * WILDCART is distributed under the MIT License (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.ausiasmarch.wildcart.exception;

import java.util.Date;
import net.ausiasmarch.wildcart.bean.ErrorResponseBean;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> UnauthorizedException(UnauthorizedException ex, WebRequest request) {
        ErrorResponseBean errorDetails
               = new ErrorResponseBean(new Date(), HttpStatus.UNAUTHORIZED.name(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> ResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorResponseBean errorDetails
               = new ErrorResponseBean(new Date(), HttpStatus.NOT_FOUND.name(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceNotModifiedException.class)
    public ResponseEntity<?> ResourceNotModifiedException(ResourceNotModifiedException ex, WebRequest request) {
        ErrorResponseBean errorDetails
               = new ErrorResponseBean(new Date(), HttpStatus.NOT_MODIFIED.name(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_MODIFIED);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> ValidationException(ValidationException ex, WebRequest request) {
        ErrorResponseBean errorDetails
               = new ErrorResponseBean(new Date(), HttpStatus.NOT_ACCEPTABLE.name(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> AppExceptionHandler(Exception ex, WebRequest request) {
        ErrorResponseBean errorDetails
               = new ErrorResponseBean(new Date(), HttpStatus.INTERNAL_SERVER_ERROR.name(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CannotPerformOperationException.class)
    public ResponseEntity<?> CannotPerformOperationExceptionException(UnauthorizedException ex, WebRequest request) {
        ErrorResponseBean errorDetails
               = new ErrorResponseBean(new Date(), HttpStatus.FORBIDDEN.name(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(JWTException.class)
    public ResponseEntity<?> JWTException(UnauthorizedException ex, WebRequest request) {
        ErrorResponseBean errorDetails
               = new ErrorResponseBean(new Date(), HttpStatus.UNAUTHORIZED.name(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CaptchaException.class)
    public ResponseEntity<?> CaptchaException(CaptchaException ex, WebRequest request) {
        ErrorResponseBean errorDetails
               = new ErrorResponseBean(new Date(), HttpStatus.NOT_ACCEPTABLE.name(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_ACCEPTABLE);
    }

}
