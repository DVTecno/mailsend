package com.mail.exceptions;

import javax.mail.MessagingException;

public class MiExcepcion extends Exception {
    public MiExcepcion(String mensaje) {
        super(mensaje);
    }
}
