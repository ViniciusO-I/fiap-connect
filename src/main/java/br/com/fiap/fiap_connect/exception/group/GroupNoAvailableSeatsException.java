package br.com.fiap.fiap_connect.exception.group;
import br.com.fiap.fiap_connect.exception.BusinessException;
public class GroupNoAvailableSeatsException extends BusinessException {
    public GroupNoAvailableSeatsException(String message) { super(message); }
}
