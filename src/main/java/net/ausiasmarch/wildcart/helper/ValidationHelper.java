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
package net.ausiasmarch.wildcart.helper;

import java.time.Duration;
import java.time.LocalDateTime;
import net.ausiasmarch.wildcart.exception.ValidationException;

public class ValidationHelper {

    public static final String EMAIL_PATTERN = "^.+@.+\\..+$";
    public static final String CODIGO_PATTERN = "^([A-Z0-9]{1,6}-)[A-Za-z0-9]{5,200}$";

    public static void isNotNull(String fieldName, String str) {
        if (str == null) {
            throw new ValidationException("Error en la validación: " + fieldName + " no puede ser nulo");
        }
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void isNumeric(String fieldName, String str) {
        if (!isNumeric(str)) {
            throw new ValidationException("Error en la validación: " + fieldName + " ha de ser numérico");
        }
    }

    public static void isBetween(int iNumber, int Min, int Max, String error) {
        if (iNumber < Min || iNumber > Max) {
            throw new ValidationException("error en la validación: " + error);
        }
    }

    public static void isBetween(double iNumber, double iMin, double iMax, String error) {
        if (iNumber >= iMin && iNumber <= iMax) {
        } else {
            throw new ValidationException("error de validación: " + error);
        }
    }

    public static void isBetween(String strNombre, int minlength, int maxlength, String error) {
        if (strNombre.length() >= minlength && strNombre.length() <= maxlength) {
        } else {
            throw new ValidationException("error en la validación: " + error);
        }
    }

    public static void validateRPP(int iRPP) {
        ValidationHelper.isBetween(iRPP, 1, 10000, "RPP no válido (1-10000)");
    }

    public static void validatePattern(String strInput, String strPattern, String error) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(strPattern);
        java.util.regex.Matcher m = p.matcher(strInput);
        if (!m.matches()) {
            throw new ValidationException("error de validación: " + error);
        }
    }

    public static void validateDNI(String itDNI, String error) {
        String strDNI = itDNI.trim().replaceAll(" ", "");
        if (strDNI.length() == 9) {
            if (isNumeric(strDNI.substring(0, 8))) {
                int intPartDNI = Integer.parseInt(strDNI.substring(0, 8));
                char cLetraDNI = strDNI.charAt(8);
                int valNumDni = intPartDNI % 23;
                if ("TRWAGMYFPDXBNJZSQVHLCKE".charAt(valNumDni) != cLetraDNI) {
                    throw new ValidationException("error de validación: " + error);
                }
            } else {
                throw new ValidationException("error de validación: " + error);
            }
        } else {
            throw new ValidationException("error de validación: " + error);
        }
    }

    public static void validateEmail(String email, String error) {
        isBetween(email, 3, 255, error);
        String ePattern = "^.+@.+\\..+$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        if (!m.matches()) {
            throw new ValidationException("error de validación: " + error);
        }
    }

    public static void validateLogin(String strLogin, String error) {
        isBetween(strLogin, 6, 20, error);
        String ePattern = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){4,18}[a-zA-Z0-9]$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(strLogin);
        if (!m.matches()) {
            throw new ValidationException("error de validación: " + error);
        }
    }

    public static void validateDate(LocalDateTime oDate, LocalDateTime oDateStart, LocalDateTime oDateEnd,
           String error) {
        Long lDur1 = Duration.between(oDateStart, oDate).toMillis();
        Long lDur2 = Duration.between(oDate, oDateEnd).toMillis();
        if (lDur1 > 0L && lDur2 > 0L) {
        } else {
            throw new ValidationException("error de validación: " + error);
        }
    }

}
