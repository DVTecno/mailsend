package com.mail.utilitys;

public class Utility {
    public static String getSiteURL(javax.servlet.http.HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
}
