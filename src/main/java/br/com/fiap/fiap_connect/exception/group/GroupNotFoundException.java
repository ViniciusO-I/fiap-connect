package br.com.fiap.fiap_connect.exception.group;
import br.com.fiap.fiap_connect.exception.BusinessException;
public class GroupNotFoundException extends BusinessException {
    public GroupNotFoundException(String message) { super(message); }
}
