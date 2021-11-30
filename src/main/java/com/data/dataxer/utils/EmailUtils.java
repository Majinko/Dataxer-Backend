package com.data.dataxer.utils;

import com.data.dataxer.models.dto.EmailMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailUtils {
    @Value("${app.frontend.url}")
    private static String frontendUrl;

    public EmailUtils(@Value("${app.frontend.url}") String frontendUrl) {
        EmailUtils.frontendUrl = frontendUrl;
    }

    public static EmailMessage newUser(String email, String password) {
        StringBuilder sb = new StringBuilder();

        sb.append("Dobrý deň gratulujeme, bolo Vám vytvorené konto v dataxeri.<br><br>");
        sb.append("Url: ").append(frontendUrl).append("<br>");
        sb.append("Email: ").append(email).append("<br>");
        sb.append("Heslo: ").append(password).append("<br>");

        return new EmailMessage(email, "Vytvorenie konta v dataxeri", sb.toString());
    }
}
