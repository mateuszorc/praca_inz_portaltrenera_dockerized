package com.example.portaltrenera.registration.service;

import com.example.portaltrenera.email.EmailSender;
import com.example.portaltrenera.email.EmailValidator;
import com.example.portaltrenera.model.User;
import com.example.portaltrenera.model.UserRole;
import com.example.portaltrenera.registration.RegistrationRequest;
import com.example.portaltrenera.registration.token.ConfirmationToken;

import java.time.LocalDateTime;
import javax.transaction.Transactional;

import com.example.portaltrenera.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private final UserService userService;
    private final EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;
    private static final Logger LOGGER = LogManager.getLogger(RegistrationService.class);

    @Autowired
    public RegistrationService(UserService userService,
                               EmailValidator emailValidator,
                               ConfirmationTokenService confirmationTokenService,
                               EmailSender emailSender) {
        this.userService = userService;
        this.emailValidator = emailValidator;
        this.confirmationTokenService = confirmationTokenService;
        this.emailSender = emailSender;
    }

    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if (!isValidEmail) {
            LOGGER.info("Email supplied for registration is not valid.");
            throw new IllegalStateException("Email is not valid.");
        } else {
            String token = this.userService
                    .registerUser(new User(
                            request.getFirstName(),
                            request.getLastName(),
                            request.getEmail(),
                            request.getPassword(),
                            UserRole.USER));
            String link = "http://localhost:8080/api/registration/confirm?token=" + token;
            this.emailSender.send(request.getEmail(), this.buildEmail(request.getFirstName(), link));
            LOGGER.info("Sent registration email to: " + request.getEmail());
//            return token;
            return "U??ytkownik zosta?? zarejestrowany";
        }
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token).orElseThrow(() -> {
            LOGGER.info("Token could not be found");
            return new IllegalStateException("Token could not be found");
        });
        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Email has already been confirmed!");
        } else {
            LocalDateTime expiredAt = confirmationToken.getExpiresAt();
            if (expiredAt.isBefore(LocalDateTime.now())) {
                LOGGER.info("Token expiration time has passed.");
                throw new IllegalStateException("Token expiration time has passed.");
            } else {
                confirmationTokenService.setConfirmedAt(token);
                userService.enableUser(confirmationToken.getUser().getEmail());
                LOGGER.info("User's account has been confirmed. \n(token): " + token);
                return "Potwierdzono email, przejd?? na stron?? portaltrenera.pl by si?? zalogowa??!";
            }
        }
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n\n  " +
                "<table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n    " +
                "<tbody><tr>\n      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n        \n        " +
                "<table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n          " +
                "<tbody><tr>\n            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n                " +
                "<table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n                  " +
                "<tbody><tr>\n                    <td style=\"padding-left:10px\">\n                  \n                    </td>\n                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n                      " +
                //potwierd?? sw??j email
                "<span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Potwierd?? sw??j email</span>\n                    " +
                "</td>\n                  </tr>\n                </tbody></table>\n              </a>\n            </td>\n          </tr>\n        </tbody></table>\n        \n      </td>\n    </tr>\n  </tbody></table>\n  " +
                "<table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n    " +
                "<tbody><tr>\n      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n      <td>\n        \n                " +
                "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n                  " +
                "<tbody><tr>\n                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n                  </tr>\n                </tbody></table>\n        \n      </td>\n      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n   " +
                " </tr>\n  </tbody></table>\n\n\n\n  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n    " +
                "<tbody><tr>\n      <td height=\"30\"><br></td>\n    </tr>\n    <tr>\n      <td width=\"10\" valign=\"middle\"><br></td>\n      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n        \n            " +
                //imi?? i link
                "<p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Cze???? " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"" +
                ">Klikaj??c w link \"Aktywuj konto\" potwierdzasz, ??e zapozna??e??/a?? si?? z polityk?? prywatno??ci Portalu Trenera i zgadzasz si?? z ni??. Je??li chcesz si?? z ni?? zapozna?? kliknij w link \"Polityka prywatno??ci\". \nNie mo??na korzysta?? z portalu trenera je??li nie aktywuje si?? konta.</p>" +
                "<blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#FF0000\"> <a href=\"http://localhost:3000/privacy-policy\">Polityka prywatno??ci</a> </p></blockquote>" +
                "<blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Aktywuj konto</a> </p></blockquote>\n Link wyga??nie 24 godziny po otrzymaniu tego emaila. <p>Do zobaczenia na Portalu Trenera!</p>        \n      </td>\n      <td width=\"10\" valign=\"middle\"><br></td>\n    </tr>\n    <tr>\n      <td height=\"30\"><br></td>\n    </tr>\n  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n\n</div></div>";
    }
}