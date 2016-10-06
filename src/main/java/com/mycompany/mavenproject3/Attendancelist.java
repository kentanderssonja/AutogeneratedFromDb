/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject3;

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
 * @author User
 */
@Entity
@Table(name = "attendancelist")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Attendancelist.findAll", query = "SELECT a FROM Attendancelist a"),
    @NamedQuery(name = "Attendancelist.findByIdAttendanceList", query = "SELECT a FROM Attendancelist a WHERE a.idAttendanceList = :idAttendanceList")})
public class Attendancelist implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idAttendanceList")
    private Integer idAttendanceList;

    public Attendancelist() {
    }

    public Attendancelist(Integer idAttendanceList) {
        this.idAttendanceList = idAttendanceList;
    }

    public Integer getIdAttendanceList() {
        return idAttendanceList;
    }

    public void setIdAttendanceList(Integer idAttendanceList) {
        this.idAttendanceList = idAttendanceList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idAttendanceList != null ? idAttendanceList.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Attendancelist)) {
            return false;
        }
        Attendancelist other = (Attendancelist) object;
        if ((this.idAttendanceList == null && other.idAttendanceList != null) || (this.idAttendanceList != null && !this.idAttendanceList.equals(other.idAttendanceList))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.mavenproject3.Attendancelist[ idAttendanceList=" + idAttendanceList + " ]";
    }
    
}
