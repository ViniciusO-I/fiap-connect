package br.com.fiap.fiap_connect.exception.skill;
import br.com.fiap.fiap_connect.exception.BusinessException;
public class SkillAlreadyRegisteredException extends BusinessException {
    public SkillAlreadyRegisteredException(String message) { super(message); }
}
