package net.ausiasmarch.wildcart.bean;

public class CaptchaResponse {

    private String token;
    private String question;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

}
