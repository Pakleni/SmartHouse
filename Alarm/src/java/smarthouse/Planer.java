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
@Table(name = "Planer")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Planer.findAll", query = "SELECT p FROM Planer p"),
    @NamedQuery(name = "Planer.findByIdPlanner", query = "SELECT p FROM Planer p WHERE p.idPlanner = :idPlanner"),
    @NamedQuery(name = "Planer.findByIdUsers", query = "SELECT p FROM Planer p WHERE p.idUsers = :idUsers"),
    @NamedQuery(name = "Planer.findByName", query = "SELECT p FROM Planer p WHERE p.name = :name"),
    @NamedQuery(name = "Planer.findByStart", query = "SELECT p FROM Planer p WHERE p.start = :start"),
    @NamedQuery(name = "Planer.findByDuration", query = "SELECT p FROM Planer p WHERE p.duration = :duration"),
    @NamedQuery(name = "Planer.findByDestination", query = "SELECT p FROM Planer p WHERE p.destination = :destination")})
public class Planer implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idPlanner")
    private Integer idPlanner;
    @Basic(optional = false)
    @Column(name = "idUsers")
    private int idUsers;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "start")
    @Temporal(TemporalType.TIMESTAMP)
    private Date start;
    @Basic(optional = false)
    @Column(name = "duration")
    private int duration;
    @Column(name = "destination")
    private String destination;

    public Planer() {
    }

    public Planer(Integer idPlanner) {
        this.idPlanner = idPlanner;
    }

    public Planer(Integer idPlanner, int idUsers, String name, Date start, int duration) {
        this.idPlanner = idPlanner;
        this.idUsers = idUsers;
        this.name = name;
        this.start = start;
        this.duration = duration;
    }

    public Integer getIdPlanner() {
        return idPlanner;
    }

    public void setIdPlanner(Integer idPlanner) {
        this.idPlanner = idPlanner;
    }

    public int getIdUsers() {
        return idUsers;
    }

    public void setIdUsers(int idUsers) {
        this.idUsers = idUsers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPlanner != null ? idPlanner.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Planer)) {
            return false;
        }
        Planer other = (Planer) object;
        if ((this.idPlanner == null && other.idPlanner != null) || (this.idPlanner != null && !this.idPlanner.equals(other.idPlanner))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "smarthouse.Planer[ idPlanner=" + idPlanner + " ]";
    }
    
}
