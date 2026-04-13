package br.com.fiap.fiap_connect.exception.group;
import br.com.fiap.fiap_connect.exception.BusinessException;
public class UserLacksRequiredSkillsException extends BusinessException {
    public UserLacksRequiredSkillsException(String message) { super(message); }
}
