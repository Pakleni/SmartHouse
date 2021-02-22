/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthouse;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author pakleni
 */
@Entity
@Table(name = "PlayHistory")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PlayHistory.findAll", query = "SELECT p FROM PlayHistory p"),
    @NamedQuery(name = "PlayHistory.findByIdPlayHistory", query = "SELECT p FROM PlayHistory p WHERE p.idPlayHistory = :idPlayHistory"),
    @NamedQuery(name = "PlayHistory.findByQuery", query = "SELECT p FROM PlayHistory p WHERE p.query = :query"),
    @NamedQuery(name = "PlayHistory.findByIdUsers", query = "SELECT p FROM PlayHistory p WHERE p.idUsers = :idUsers")})
public class PlayHistory implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idPlayHistory")
    private Integer idPlayHistory;
    @Basic(optional = false)
    @Column(name = "Query")
    private String query;
    @Basic(optional = false)
    @Column(name = "idUsers")
    private int idUsers;

    public PlayHistory() {
    }

    public PlayHistory(Integer idPlayHistory) {
        this.idPlayHistory = idPlayHistory;
    }

    public PlayHistory(Integer idPlayHistory, String query, int idUsers) {
        this.idPlayHistory = idPlayHistory;
        this.query = query;
        this.idUsers = idUsers;
    }

    public Integer getIdPlayHistory() {
        return idPlayHistory;
    }

    public void setIdPlayHistory(Integer idPlayHistory) {
        this.idPlayHistory = idPlayHistory;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
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
        hash += (idPlayHistory != null ? idPlayHistory.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PlayHistory)) {
            return false;
        }
        PlayHistory other = (PlayHistory) object;
        if ((this.idPlayHistory == null && other.idPlayHistory != null) || (this.idPlayHistory != null && !this.idPlayHistory.equals(other.idPlayHistory))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "smarthouse.PlayHistory[ idPlayHistory=" + idPlayHistory + " ]";
    }
    
}
