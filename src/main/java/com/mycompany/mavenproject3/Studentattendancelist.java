/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject3;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author User
 */
@Entity
@Table(name = "studentattendancelist")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Studentattendancelist.findAll", query = "SELECT s FROM Studentattendancelist s"),
    @NamedQuery(name = "Studentattendancelist.findByIdStudent", query = "SELECT s FROM Studentattendancelist s WHERE s.studentattendancelistPK.idStudent = :idStudent"),
    @NamedQuery(name = "Studentattendancelist.findByIdAttendanceList", query = "SELECT s FROM Studentattendancelist s WHERE s.studentattendancelistPK.idAttendanceList = :idAttendanceList")})
public class Studentattendancelist implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected StudentattendancelistPK studentattendancelistPK;

    public Studentattendancelist() {
    }

    public Studentattendancelist(StudentattendancelistPK studentattendancelistPK) {
        this.studentattendancelistPK = studentattendancelistPK;
    }

    public Studentattendancelist(int idStudent, int idAttendanceList) {
        this.studentattendancelistPK = new StudentattendancelistPK(idStudent, idAttendanceList);
    }

    public StudentattendancelistPK getStudentattendancelistPK() {
        return studentattendancelistPK;
    }

    public void setStudentattendancelistPK(StudentattendancelistPK studentattendancelistPK) {
        this.studentattendancelistPK = studentattendancelistPK;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (studentattendancelistPK != null ? studentattendancelistPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Studentattendancelist)) {
            return false;
        }
        Studentattendancelist other = (Studentattendancelist) object;
        if ((this.studentattendancelistPK == null && other.studentattendancelistPK != null) || (this.studentattendancelistPK != null && !this.studentattendancelistPK.equals(other.studentattendancelistPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.mavenproject3.Studentattendancelist[ studentattendancelistPK=" + studentattendancelistPK + " ]";
    }
    
}
