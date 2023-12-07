package com.mail.service;

import com.mail.exceptions.MiExcepcion;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.springframework.core.io.Resource;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender javaMailSender;
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * Dirección de correo electrónico del remitente utilizado para enviar correos electrónicos.
     * Esta propiedad se configura a través de la anotación @Value y toma su valor de la propiedad 'spring.mail.username'.
     * Se utiliza en los métodos que envían correos electrónicos para especificar la dirección del remitente.
     */
    @Value("spring.mail.username")
    private String emailSender;

    /**
     * Envía un correo electrónico simple con los parámetros especificados.
     *
     * @param to      La dirección de correo electrónico del destinatario.
     * @param subject El asunto del correo electrónico.
     * @param text    El cuerpo del correo electrónico.
     * @throws MiExcepcion Si hay un error al intentar enviar el correo electrónico.
     */
    public void sendEmail(String to, String subject, String text) throws MiExcepcion {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailSender);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            javaMailSender.send(message);
            log.info("Correo enviado de {} a {} con asunto '{}'", emailSender, to, subject);
        } catch (MailException e) {
            log.error("Error al enviar el correo", e);
            throw new MiExcepcion("Error al enviar el correo");
        }
    }

    /**
     * Envía un correo electrónico de confirmación después de cambiar la contraseña.
     *
     * @param email La dirección de correo electrónico del destinatario.
     * @throws RuntimeException Si hay un error al intentar enviar el correo electrónico.
     */
    public void enviarEmailConfirmacionCambioPassword(String email) {
        String subject = "Contraseña Actualizada";
        String text = "Su contraseña ha sido actualizada con éxito.";
        try {
            sendEmail(email, subject, text);
        } catch (MiExcepcion e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Envía un correo electrónico con un enlace de restablecimiento de contraseña.
     *
     * @param email La dirección de correo electrónico del destinatario.
     * @param resetPasswordLink El enlace de restablecimiento de contraseña.
     * @throws MiExcepcion Si hay un error al intentar enviar el correo electrónico.
     */
    public void sendEmail(String email, String resetPasswordLink) throws MiExcepcion {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setFrom(emailSender, "Soporte de Spring Boot");
            helper.setTo(email);
            String subject = "Aquí está el enlace para restablecer su contraseña: ";
            String content = "<div style=\"background:#ffffff;background-color:#ffffff;margin:0px auto;max-width:600px\"><p>Hola,</p>"
                    + "<p>Usted ha solicitado restablecer su contraseña. " +
                    "</p>"
                    + "<p>Haga clic en el enlace a continuación para cambiar su contraseña:</p>"
                    + "<p><b><a href=\"" + resetPasswordLink + "\">Cambiar mi contraseña</a></b></p>"
                    + "<p>Ignore este correo electrónico si no desea cambiar su contraseña, "
                    + "o si ya ha cambiado su contraseña.</p></div> <div style=\"background:#ffffff;background-color:#ffffff;margin:0px auto;max-width:600px\">\n" +
                    "\n" +
                    "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\"\n" +
                    "               style=\"background:#ffffff;background-color:#ffffff;width:100%\">\n" +
                    "            <tbody>\n" +
                    "            <tr>\n" +
                    "                <td style=\"direction:ltr;font-size:0px;padding:0 0 40px;text-align:center\">\n" +
                    "\n" +
                    "\n" +
                    "                    <div class=\"m_4154174028324774528mj-column-per-100\"\n" +
                    "                         style=\"font-size:0px;text-align:left;direction:ltr;display:inline-block;vertical-align:top;width:100%\">\n" +
                    "\n" +
                    "                        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\">\n" +
                    "                            <tbody>\n" +
                    "                            <tr>\n" +
                    "                                <td style=\"vertical-align:top;padding:0 24px\">\n" +
                    "\n" +
                    "                                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\">\n" +
                    "                                        <tbody>\n" +
                    "\n" +
                    "                                        <tr>\n" +
                    "                                            <td align=\"center\" style=\"font-size:0px;padding:0px;word-break:break-word\">\n" +
                    "\n" +
                    "                                                <div style=\"font-family:Graphik,Helvetica,Arial,sans-serif;font-size:16px;line-height:1;text-align:center;color:#000000\">\n" +
                    "                                                    <a href=\"https://www.facebook.com/\"\n" +
                    "                                                       style=\"text-decoration:none;color:#467ef5\" target=\"_blank\"\n" +
                    "                                                       data-saferedirecturl=\"https://www.google.com/url?q=https://www.facebook.com/grabrfi&amp;source=gmail&amp;ust=1701998951446000&amp;usg=AOvVaw1UG1UXdAWq7bExzd-PHaiq\">\n" +
                    "                                                        <img src=\"https://ci3.googleusercontent.com/meips/ADKq_NZ0J9S6lw-FwOU8Jfx6pqmY59vlzSlnyWS0nuZGsNBjylfGEbAT_q43J4hrs9de6PTN5rFdromRwCClMUJsFmsRh9H8iSHgnZIMLjuDIEQ6sm9r1oVFueZ7r4hPEHT6nUdPrI1LJ8NtNzpB8Lfg22zuKNq635odWdZ_D83HdJ7F7C1NOyIFSllxhA=s0-d-e1-ft#https://uploads-ssl.webflow.com/60eedd479e16db8952bc3b1b/619d36fd72bd657c6068ccad_yOez532kvNaVPgPqAgvfMWhO4qvDLQ.png\"\n" +
                    "                                                             alt=\"Facebook Link\" width=\"36px\" class=\"CToWUd\"\n" +
                    "                                                             data-bit=\"iit\">\n" +
                    "                                                    </a>\n" +
                    "                                                    <a href=\"https://www.instagram.com/\"\n" +
                    "                                                       style=\"text-decoration:none;color:#467ef5\" target=\"_blank\"\n" +
                    "                                                       data-saferedirecturl=\"https://www.google.com/url?q=https://www.instagram.com/grabrfi&amp;source=gmail&amp;ust=1701998951446000&amp;usg=AOvVaw1a2JgpOqNpF8bFp3kAGlzD\">\n" +
                    "                                                        <img src=\"https://ci3.googleusercontent.com/meips/ADKq_Nai8fpkXmnU9z3TOfZoteGY1VcgkOsMQtoN9m8sevFqMeGiX2rkePAyWEcXtQC4bfoRbSc6UZOJ478x3CsUHOjyE_sKjaAV9A9wIZpcMUUYf_ur6CzK0IiiS6IxCWFtUHjOWbLkPaPKNMEhwhfIkl38Q7K_NDdxYKEn-qXZ_KxrXCNmeGKOeGoecQ=s0-d-e1-ft#https://uploads-ssl.webflow.com/60eedd479e16db8952bc3b1b/619d36fde2a53a5062a6c5ea_FN0y1g23p2pL2A0Nwjx3n5ybMiPlSd.png\"\n" +
                    "                                                             alt=\"Instagram Link\" width=\"36px\" class=\"CToWUd\"\n" +
                    "                                                             data-bit=\"iit\">\n" +
                    "                                                    </a></div>\n" +
                    "\n" +
                    "                                            </td>\n" +
                    "                                        </tr>\n" +
                    "\n" +
                    "                                        </tbody>\n" +
                    "                                    </table>\n" +
                    "\n" +
                    "                                </td>\n" +
                    "                            </tr>\n" +
                    "                            </tbody>\n" +
                    "                        </table>\n" +
                    "\n" +
                    "                    </div>\n" +
                    "\n" +
                    "\n" +
                    "                </td>\n" +
                    "            </tr>\n" +
                    "            </tbody>\n" +
                    "        </table>\n" +
                    "        <div class=\"yj6qo\"></div>\n" +
                    "        <div class=\"adL\">\n" +
                    "\n" +
                    "        </div>\n" +
                    "    </div>";
            helper.setSubject(subject);
            helper.setText(content, true);
            javaMailSender.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new MiExcepcion("Error al enviar el correo electrónico");
        }
    }

    /**
     * Envía un correo electrónico con un archivo PDF adjunto.
     *
     * @param to La dirección de correo electrónico del destinatario.
     * @param subject El asunto del correo electrónico.
     * @param text El contenido del correo electrónico.
     * @param pdfBytes Los bytes del archivo PDF que se adjuntará.
     * @param pdfFileName El nombre del archivo PDF adjunto.
     * @throws MiExcepcion Si hay un error al intentar enviar el correo electrónico con el archivo adjunto.
     */
    public void sendEmailWithAttachment(String to, String subject, String text, byte[] pdfBytes, String pdfFileName) throws MiExcepcion {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(emailSender);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            // Adjuntar el archivo PDF
            Resource pdfAttachment = new ByteArrayResource(pdfBytes);
            helper.addAttachment(pdfFileName, pdfAttachment);

            javaMailSender.send(message);
            log.info("Correo enviado de {} a {} con asunto '{}' y PDF adjunto", emailSender, to, subject);
        } catch (MessagingException e) {
            log.error("Error al enviar el correo con archivo adjunto", e);
            throw new MiExcepcion("Error al enviar el correo con archivo adjunto");
        }
    }
}
