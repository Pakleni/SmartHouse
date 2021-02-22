/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package resources.SmartHouse;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author pakleni
 */
@Entity
@Table(name = "AlarmSound")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AlarmSound.findAll", query = "SELECT a FROM AlarmSound a"),
    @NamedQuery(name = "AlarmSound.findByIdUsers", query = "SELECT a FROM AlarmSound a WHERE a.idUsers = :idUsers"),
    @NamedQuery(name = "AlarmSound.findByQuery", query = "SELECT a FROM AlarmSound a WHERE a.query = :query")})
public class AlarmSound implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idUsers")
    private Integer idUsers;
    @Basic(optional = false)
    @Column(name = "Query")
    private String query;
    @JoinColumn(name = "idUsers", referencedColumnName = "idUsers", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Users users;

    public AlarmSound() {
    }

    public AlarmSound(Integer idUsers) {
        this.idUsers = idUsers;
    }

    public AlarmSound(Integer idUsers, String query) {
        this.idUsers = idUsers;
        this.query = query;
    }

    public Integer getIdUsers() {
        return idUsers;
    }

    public void setIdUsers(Integer idUsers) {
        this.idUsers = idUsers;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idUsers != null ? idUsers.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AlarmSound)) {
            return false;
        }
        AlarmSound other = (AlarmSound) object;
        if ((this.idUsers == null && other.idUsers != null) || (this.idUsers != null && !this.idUsers.equals(other.idUsers))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SmartHouse.AlarmSound[ idUsers=" + idUsers + " ]";
    }
    
}
