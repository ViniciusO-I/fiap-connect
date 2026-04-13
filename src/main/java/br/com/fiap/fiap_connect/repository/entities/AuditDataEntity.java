package br.com.fiap.fiap_connect.repository.entities;

import jakarta.persistence.*;
import java.util.Date;

@MappedSuperclass
public abstract class AuditDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date dtCreated;
    private Date dtUpdated;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    public AuditDataEntity() {
        this.status = StatusEnum.ACTIVE;
        this.dtCreated = new Date();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Date getDtCreated() { return dtCreated; }
    public void setDtCreated(Date dtCreated) { this.dtCreated = dtCreated; }
    public Date getDtUpdated() { return dtUpdated; }
    public void setDtUpdated(Date dtUpdated) { this.dtUpdated = dtUpdated; }
    public StatusEnum getStatus() { return status; }
    public void setStatus(StatusEnum status) { this.status = status; }
}
