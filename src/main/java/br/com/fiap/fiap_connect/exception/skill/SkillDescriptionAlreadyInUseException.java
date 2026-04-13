package br.com.fiap.fiap_connect.exception.skill;
import br.com.fiap.fiap_connect.exception.BusinessException;
public class SkillDescriptionAlreadyInUseException extends BusinessException {
    public SkillDescriptionAlreadyInUseException(String message) { super(message); }
}
