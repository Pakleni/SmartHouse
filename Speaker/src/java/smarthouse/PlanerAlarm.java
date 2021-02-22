/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthouse;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author pakleni
 */
@Entity
@Table(name = "PlanerAlarm")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PlanerAlarm.findAll", query = "SELECT p FROM PlanerAlarm p"),
    @NamedQuery(name = "PlanerAlarm.findByIdAlarmPlaner", query = "SELECT p FROM PlanerAlarm p WHERE p.idAlarmPlaner = :idAlarmPlaner"),
    @NamedQuery(name = "PlanerAlarm.findByDatetime", query = "SELECT p FROM PlanerAlarm p WHERE p.datetime = :datetime"),
    @NamedQuery(name = "PlanerAlarm.findByIdUsers", query = "SELECT p FROM PlanerAlarm p WHERE p.idUsers = :idUsers")})
public class PlanerAlarm implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idAlarmPlaner")
    private Integer idAlarmPlaner;
    @Basic(optional = false)
    @Column(name = "datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetime;
    @Basic(optional = false)
    @Column(name = "idUsers")
    private int idUsers;

    public PlanerAlarm() {
    }

    public PlanerAlarm(Integer idAlarmPlaner) {
        this.idAlarmPlaner = idAlarmPlaner;
    }

    public PlanerAlarm(Integer idAlarmPlaner, Date datetime, int idUsers) {
        this.idAlarmPlaner = idAlarmPlaner;
        this.datetime = datetime;
        this.idUsers = idUsers;
    }

    public Integer getIdAlarmPlaner() {
        return idAlarmPlaner;
    }

    public void setIdAlarmPlaner(Integer idAlarmPlaner) {
        this.idAlarmPlaner = idAlarmPlaner;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public int getIdUsers() {
        return idUsers;
    }

    public void setIdUsers(int idUsers) {
        this.idUsers = idUsers;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idAlarmPlaner != null ? idAlarmPlaner.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PlanerAlarm)) {
            return false;
        }
        PlanerAlarm other = (PlanerAlarm) object;
        if ((this.idAlarmPlaner == null && other.idAlarmPlaner != null) || (this.idAlarmPlaner != null && !this.idAlarmPlaner.equals(other.idAlarmPlaner))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SmartHouse.PlanerAlarm[ idAlarmPlaner=" + idAlarmPlaner + " ]";
    }
    
}
