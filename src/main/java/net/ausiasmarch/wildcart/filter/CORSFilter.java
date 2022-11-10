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
package net.ausiasmarch.wildcart.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CORSFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

       if (req instanceof HttpServletRequest && res instanceof HttpServletResponse) {
            HttpServletRequest oRequest = (HttpServletRequest) req;
            HttpServletResponse oResponse = (HttpServletResponse) res;

            if (oRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
                //https://stackoverflow.com/questions/56479150/access-blocked-by-cors-policy-response-to-preflight-request-doesnt-pass-access
                //System.out.println("Pre-flight");
                oResponse.setHeader("Access-Control-Allow-Origin", oRequest.getHeader("origin"));
                oResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,HEAD,PATCH");
                oResponse.setHeader("Access-Control-Max-Age", "3600");
                oResponse.setHeader("Access-Control-Allow-Credentials", "true");
                oResponse.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, "
                       + "Origin, "
                       + "Accept, "
                       + "Authorization, "
                       + "ResponseType, "
                       + "Observe, "
                       + "X-Requested-With, "
                       + "Content-Type, "
                       + "Access-Control-Expose-Headers, "
                       + "Access-Control-Request-Method, "
                       + "Access-Control-Request-Headers");
                oResponse.setStatus(HttpServletResponse.SC_OK);
            } else {
                oResponse.setHeader("Cache-control", "no-cache, no-store");
                oResponse.setHeader("Pragma", "no-cache");
                oResponse.setHeader("Expires", "-1");
                oResponse.setHeader("Access-Control-Allow-Origin", oRequest.getHeader("origin"));
                oResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,HEAD,PATCH");
                oResponse.setHeader("Access-Control-Max-Age", "86400");
                oResponse.setHeader("Access-Control-Allow-Credentials", "true");
                oResponse.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, "
                       + "Origin, "
                       + "Accept, "
                       + "Authorization, "
                       + "ResponseType, "
                       + "Observe, "
                       + "X-Requested-With, "
                       + "Content-Type, "
                       + "Access-Control-Expose-Headers, "
                       + "Access-Control-Request-Method, "
                       + "Access-Control-Request-Headers");
            }
        }

    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}
