/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources.smarthouse;

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
@Table(name = "DatedAlarm")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DatedAlarm.findAll", query = "SELECT d FROM DatedAlarm d"),
    @NamedQuery(name = "DatedAlarm.findByIdAlarmPlaner", query = "SELECT d FROM DatedAlarm d WHERE d.idAlarmPlaner = :idAlarmPlaner"),
    @NamedQuery(name = "DatedAlarm.findByDatetime", query = "SELECT d FROM DatedAlarm d WHERE d.datetime = :datetime"),
    @NamedQuery(name = "DatedAlarm.findByIdUsers", query = "SELECT d FROM DatedAlarm d WHERE d.idUsers = :idUsers")})
public class DatedAlarm implements Serializable {

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

    public DatedAlarm() {
    }

    public DatedAlarm(Integer idAlarmPlaner) {
        this.idAlarmPlaner = idAlarmPlaner;
    }

    public DatedAlarm(Integer idAlarmPlaner, Date datetime, int idUsers) {
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
        if (!(object instanceof DatedAlarm)) {
            return false;
        }
        DatedAlarm other = (DatedAlarm) object;
        if ((this.idAlarmPlaner == null && other.idAlarmPlaner != null) || (this.idAlarmPlaner != null && !this.idAlarmPlaner.equals(other.idAlarmPlaner))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SmartHouse.DatedAlarm[ idAlarmPlaner=" + idAlarmPlaner + " ]";
    }
    
}
