package br.com.fiap.fiap_connect.exception.group;
import br.com.fiap.fiap_connect.exception.BusinessException;
public class UserAlreadyGroupMemberException extends BusinessException {
    public UserAlreadyGroupMemberException(String message) { super(message); }
}
