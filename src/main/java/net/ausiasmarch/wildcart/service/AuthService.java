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
package net.ausiasmarch.wildcart.service;

import java.time.LocalDateTime;
import static java.time.LocalDateTime.now;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import net.ausiasmarch.wildcart.bean.CaptchaBean;
import net.ausiasmarch.wildcart.bean.CaptchaResponseBean;
import net.ausiasmarch.wildcart.bean.EmailBean;
import net.ausiasmarch.wildcart.bean.RecoverBean;
import net.ausiasmarch.wildcart.bean.UsuarioBean;
import net.ausiasmarch.wildcart.entity.PendentEntity;
import net.ausiasmarch.wildcart.entity.QuestionEntity;
import net.ausiasmarch.wildcart.exception.UnauthorizedException;
import net.ausiasmarch.wildcart.entity.UsuarioEntity;
import net.ausiasmarch.wildcart.exception.CannotPerformOperationException;
import net.ausiasmarch.wildcart.exception.ResourceNotFoundException;
import net.ausiasmarch.wildcart.helper.EmailHelper;
import net.ausiasmarch.wildcart.helper.JwtHelper;
import net.ausiasmarch.wildcart.helper.RandomHelper;
import net.ausiasmarch.wildcart.helper.TipoUsuarioHelper;
import net.ausiasmarch.wildcart.repository.PendentRepository;
import net.ausiasmarch.wildcart.repository.QuestionRepository;
import net.ausiasmarch.wildcart.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class AuthService {

    @Autowired
    HttpSession oHttpSession;

    @Autowired
    UsuarioRepository oUsuarioRepository;

    @Autowired
    QuestionRepository oQuestionRepository;

    @Autowired
    PendentRepository oPendentRepository;

    @Autowired
    private HttpServletRequest oRequest;

    @Autowired
    private MailService oMailService;

    @Value("${captcha.timeout}")
    private long captchaTimeout;

    @Value("${recover.frontend.url}")
    private String recoverFrontendURL;

    @Value("${recover.recipient.email}")
    private String recoverRecipientEmail;

    public String signupPhase1(UsuarioEntity oUsuarioEntity) {
        sendRecoveryEmail(oUsuarioEntity,
               "Aviso de seguridad: activación de nueva cuenta",
               EmailHelper.getEmailTemplateValidate(oUsuarioEntity.getNombre() + " " + oUsuarioEntity.getApellido1(), recoverFrontendURL));
        return "El usuario se ha registrado correctamente. Por favor revise su email para validar la cuenta.";
    }

    public String signupPhase2(String token) {
        UsuarioEntity oUsuarioEntity = oUsuarioRepository.findByToken(token).orElseThrow(() -> new ResourceNotFoundException("token not found"));
        oUsuarioEntity.setToken("validado " + now());
        oUsuarioEntity.setValidado(true);
        return "Usuario validado correctamente en el sistema";
    }

    private void sendRecoveryEmail(UsuarioEntity oUsuarioEntity, String subject, String body) {
        try {
            oUsuarioEntity.setToken(RandomHelper.getSHA256(RandomHelper.getToken(256)));
            oUsuarioEntity.setValidado(false);
            oUsuarioRepository.save(oUsuarioEntity);
            EmailBean oEmailBean = new EmailBean();
            oEmailBean.setSender(recoverRecipientEmail + "/" + oUsuarioEntity.getToken());
            oEmailBean.setRecipient(oUsuarioEntity.getEmail());
            oEmailBean.setSubject(subject);
            oEmailBean.setBody(body);
            oMailService.sendMail(oEmailBean);
        } catch (Exception ex) {
            throw new CannotPerformOperationException("Error sending email");
        }
    }

    public String recoverByUsernamePhase1(String username) {
        UsuarioEntity oUsuarioEntity = oUsuarioRepository.findByLogin(username).orElseThrow(() -> new ResourceNotFoundException("username not found"));
        sendRecoveryEmail(oUsuarioEntity,
               "Aviso de seguridad: solicitud para restablecer la contraseña en wildcart",
               EmailHelper.getEmailTemplateRecover(oUsuarioEntity.getNombre() + " " + oUsuarioEntity.getApellido1(), recoverFrontendURL));
        return "Por favor revise su email para validar la cuenta.";
    }

    public String recoverByEmailPhase1(String email) {
        UsuarioEntity oUsuarioEntity = oUsuarioRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("username not found"));
        sendRecoveryEmail(oUsuarioEntity,
               "Aviso de seguridad: solicitud para restablecer la contraseña en wildcart",
               EmailHelper.getEmailTemplateRecover(oUsuarioEntity.getNombre() + " " + oUsuarioEntity.getApellido1(), recoverFrontendURL));
        return "Por favor revise su email para validar la cuenta.";
    }

    public String recoverPhase2(RecoverBean oRecoverBean) {
        UsuarioEntity oUsuarioEntity = oUsuarioRepository.findByToken(oRecoverBean.getToken()).orElseThrow(() -> new ResourceNotFoundException("token not found"));
        oUsuarioEntity.setPassword(oRecoverBean.getPassword());
        oUsuarioEntity.setToken("validado " + now());
        oUsuarioEntity.setValidado(true);
        return "Se ha actualizado la contraseña en el sistema";
    }

    public String login(@RequestBody UsuarioBean oUsuarioBean) {
        if (oUsuarioBean.getPassword() != null) {
            UsuarioEntity oUsuarioEntity = oUsuarioRepository.findByLoginAndPassword(oUsuarioBean.getUsername(), oUsuarioBean.getPassword());
            if (oUsuarioEntity != null) {
                return JwtHelper.generateJWT(oUsuarioBean.getUsername());
            } else {
                throw new UnauthorizedException("login or password incorrect");
            }
        } else {
            throw new UnauthorizedException("wrong password");
        }
    }

    public UsuarioEntity check() {
        String strUsuarioName = (String) oRequest.getAttribute("usuario");
        if (strUsuarioName != null) {
            return oUsuarioRepository.findByLogin(strUsuarioName).orElseThrow(() -> new ResourceNotFoundException("username " + strUsuarioName + " not found"));
        } else {
            throw new UnauthorizedException("No active session");
        }
    }

    public boolean isLoggedIn() {
        return oUsuarioRepository.findByLogin((String) oRequest.getAttribute("usuario")).isPresent();
    }

    public UsuarioEntity getUser() {
        return oUsuarioRepository.findByLogin((String) oRequest.getAttribute("usuario")).orElseThrow(() -> new UnauthorizedException("this request is only allowed to auth users"));
    }

    public Long getUserID() {
        Optional<UsuarioEntity> oUsuarioSessionEntity = oUsuarioRepository.findByLogin((String) oRequest.getAttribute("usuario"));
        if (oUsuarioSessionEntity.isPresent()) {
            return oUsuarioSessionEntity.get().getId();
        } else {
            throw new UnauthorizedException("this request is only allowed to auth users");
        }
    }

    public boolean isAdmin() {
        Optional<UsuarioEntity> oUsuarioSessionEntity = oUsuarioRepository.findByLogin((String) oRequest.getAttribute("usuario"));
        if (oUsuarioSessionEntity.isPresent()) {
            if (oUsuarioSessionEntity.get().getTipousuario().getId().equals(TipoUsuarioHelper.ADMIN)) {
                return true;
            }
        }
        return false;
    }

    public boolean isUser() {
        Optional<UsuarioEntity> oUsuarioSessionEntity = oUsuarioRepository.findByLogin((String) oRequest.getAttribute("usuario"));
        if (oUsuarioSessionEntity.isPresent()) {
            if (oUsuarioSessionEntity.get().getTipousuario().getId().equals(TipoUsuarioHelper.USER)) {
                return true;
            }
        }
        return false;
    }

    public void OnlyAdmins() {
        Optional<UsuarioEntity> oUsuarioSessionEntity = oUsuarioRepository.findByLogin((String) oRequest.getAttribute("usuario"));
        if (oUsuarioSessionEntity.isEmpty()) {
            throw new UnauthorizedException("this request is only allowed to admin role");
        } else {
            if (!oUsuarioSessionEntity.get().getTipousuario().getId().equals(TipoUsuarioHelper.ADMIN)) {
                throw new UnauthorizedException("this request is only allowed to admin role");
            }
        }
    }

    public void OnlyUsers() {
        Optional<UsuarioEntity> oUsuarioSessionEntity = oUsuarioRepository.findByLogin((String) oRequest.getAttribute("usuario"));
        if (oUsuarioSessionEntity.isEmpty()) {
            throw new UnauthorizedException("this request is only allowed to user role");
        } else {
            if (!oUsuarioSessionEntity.get().getTipousuario().getId().equals(TipoUsuarioHelper.USER)) {
                throw new UnauthorizedException("this request is only allowed to user role");
            }
        }
    }

    public void OnlyAdminsOrUsers() {
        Optional<UsuarioEntity> oUsuarioSessionEntity = oUsuarioRepository.findByLogin((String) oRequest.getAttribute("usuario"));
        if (oUsuarioSessionEntity.isEmpty()) {
            throw new UnauthorizedException("this request is only allowed to logged users");
        } else {
            if (!oUsuarioSessionEntity.get().getTipousuario().getId().equals(TipoUsuarioHelper.ADMIN)
                   && !oUsuarioSessionEntity.get().getTipousuario().getId().equals(TipoUsuarioHelper.USER)) {
                throw new UnauthorizedException("this request is only allowed to user or admin role");
            }
        }
    }

    public void OnlyAdminsOrOwnUsersData(Long id) {
        Optional<UsuarioEntity> oUsuarioSessionEntity = oUsuarioRepository.findByLogin((String) oRequest.getAttribute("usuario"));
        if (oUsuarioSessionEntity.isPresent()) {
            if (oUsuarioSessionEntity.get().getTipousuario().getId().equals(TipoUsuarioHelper.USER)) {
                if (!oUsuarioSessionEntity.get().getId().equals(id)) {
                    throw new UnauthorizedException("this request is only allowed for your own data");
                }
            }
        } else {
            throw new UnauthorizedException("this request is only allowed to user or admin role");
        }
    }

    @Transactional
    public CaptchaResponseBean prelogin() {

        //long lQuestionId = Long.valueOf(RandomHelper.getRandomInt(1, (int) oQuestionRepository.count()));
        List<QuestionEntity> allQuestions = oQuestionRepository.findAll();
        Long lQuestionId = allQuestions.get(RandomHelper.getRandomInt(0, allQuestions.size() - 1)).getId();

        QuestionEntity oQuestionEntity = oQuestionRepository.findById(lQuestionId)
               .orElseThrow(() -> new ResourceNotFoundException("Question not found (id = " + lQuestionId + ")"));

        PendentEntity oPendentEntity = new PendentEntity();
        oPendentEntity.setQuestion(oQuestionEntity);
        oPendentEntity.setTimecode(LocalDateTime.now());
        PendentEntity oNewPendentEntity = oPendentRepository.save(oPendentEntity); //new       

        oNewPendentEntity.setToken(RandomHelper.getSHA256(
               String.valueOf(oNewPendentEntity.getId())
               + String.valueOf(lQuestionId)
               + String.valueOf(RandomHelper.getRandomInt(1, 9999))));

        oPendentRepository.save(oNewPendentEntity); //update

        CaptchaResponseBean oCaptchaResponseBean = new CaptchaResponseBean();
        oCaptchaResponseBean.setQuestion(oNewPendentEntity.getQuestion().getStatement());
        oCaptchaResponseBean.setToken(oNewPendentEntity.getToken());

        return oCaptchaResponseBean;
    }

    public String loginC(@RequestBody CaptchaBean oCaptchaBean) {
        if (oCaptchaBean.getUsername() != null && oCaptchaBean.getPassword() != null) {
            UsuarioEntity oUsuarioEntity = oUsuarioRepository.findByLoginAndPassword(oCaptchaBean.getUsername(), oCaptchaBean.getPassword());
            if (oUsuarioEntity != null) {

                PendentEntity oPendentEntity = oPendentRepository.findByToken(oCaptchaBean.getToken())
                       .orElseThrow(() -> new ResourceNotFoundException("Pendent not found (token = " + oCaptchaBean.getToken() + ")"));

                LocalDateTime timecode = oPendentEntity.getTimecode();

                if (LocalDateTime.now().isAfter(oPendentEntity.getTimecode().plusSeconds(captchaTimeout))) {
                    throw new UnauthorizedException("Captcha expired");
                }

                if (oUsuarioEntity.isActivo()) {
                    throw new UnauthorizedException("User is not active");
                }

                if (oUsuarioEntity.isValidado()) {
                    throw new UnauthorizedException("User is not validated");
                }

                if (oPendentEntity.getQuestion().getResponse().contains("|")) {
                    String[] answersArray = oPendentEntity.getQuestion().getResponse().split("\\|");
                    for (String strAnswer : answersArray) {
                        if (strAnswer.equalsIgnoreCase(oCaptchaBean.getAnswer())) {
                            oPendentRepository.delete(oPendentEntity);
                            return JwtHelper.generateJWT(oCaptchaBean.getUsername());
                        }
                    }
                    throw new UnauthorizedException("Wrong captcha response");
                } else {
                    if (oPendentEntity.getQuestion().getResponse().toLowerCase().equals(oCaptchaBean.getAnswer().toLowerCase())) {
                        oPendentRepository.delete(oPendentEntity);
                        return JwtHelper.generateJWT(oCaptchaBean.getUsername());
                    } else {
                        throw new UnauthorizedException("Captcha error");
                    }
                }
            } else {
                throw new UnauthorizedException("Login or password error");
            }
        } else {
            throw new UnauthorizedException("Login or password not found");
        }
    }

    private void deleteExpiredPendents() {
        List<PendentEntity> lPendents = oPendentRepository.findAll();

        lPendents.forEach((oPendentEntity) -> {
            if (oPendentEntity.getTimecode().plusSeconds(captchaTimeout).isBefore(LocalDateTime.now())) {
                oPendentRepository.delete(oPendentEntity);
            }
        });
    }

}
