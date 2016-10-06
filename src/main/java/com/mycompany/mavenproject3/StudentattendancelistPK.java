/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject3;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author User
 */
@Embeddable
public class StudentattendancelistPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "idStudent")
    private int idStudent;
    @Basic(optional = false)
    @NotNull
    @Column(name = "idAttendanceList")
    private int idAttendanceList;

    public StudentattendancelistPK() {
    }

    public StudentattendancelistPK(int idStudent, int idAttendanceList) {
        this.idStudent = idStudent;
        this.idAttendanceList = idAttendanceList;
    }

    public int getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(int idStudent) {
        this.idStudent = idStudent;
    }

    public int getIdAttendanceList() {
        return idAttendanceList;
    }

    public void setIdAttendanceList(int idAttendanceList) {
        this.idAttendanceList = idAttendanceList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idStudent;
        hash += (int) idAttendanceList;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudentattendancelistPK)) {
            return false;
        }
        StudentattendancelistPK other = (StudentattendancelistPK) object;
        if (this.idStudent != other.idStudent) {
            return false;
        }
        if (this.idAttendanceList != other.idAttendanceList) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.mavenproject3.StudentattendancelistPK[ idStudent=" + idStudent + ", idAttendanceList=" + idAttendanceList + " ]";
    }
    
}
