package br.com.fiap.fiap_connect.exception.group;
import br.com.fiap.fiap_connect.exception.BusinessException;
public class GroupOwnerNotFoundException extends BusinessException {
    public GroupOwnerNotFoundException(String message) { super(message); }
}
