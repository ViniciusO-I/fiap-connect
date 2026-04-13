package br.com.fiap.fiap_connect.exception.user;
import br.com.fiap.fiap_connect.exception.BusinessException;
public class EmailAlreadyRegisteredException extends BusinessException {
    public EmailAlreadyRegisteredException(String message) { super(message); }
}
