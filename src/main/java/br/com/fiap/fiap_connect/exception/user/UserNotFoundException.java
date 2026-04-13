package br.com.fiap.fiap_connect.exception.user;
import br.com.fiap.fiap_connect.exception.BusinessException;
public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(String message) { super(message); }
}
