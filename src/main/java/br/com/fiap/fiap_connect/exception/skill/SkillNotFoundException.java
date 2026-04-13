package br.com.fiap.fiap_connect.exception.skill;
import br.com.fiap.fiap_connect.exception.BusinessException;
public class SkillNotFoundException extends BusinessException {
    public SkillNotFoundException(String message) { super(message); }
}
