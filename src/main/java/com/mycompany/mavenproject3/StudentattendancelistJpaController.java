/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject3;

import com.mycompany.mavenproject3.exceptions.NonexistentEntityException;
import com.mycompany.mavenproject3.exceptions.PreexistingEntityException;
import com.mycompany.mavenproject3.exceptions.RollbackFailureException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;

/**
 *
 * @author User
 */
public class StudentattendancelistJpaController implements Serializable {

    public StudentattendancelistJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Studentattendancelist studentattendancelist) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (studentattendancelist.getStudentattendancelistPK() == null) {
            studentattendancelist.setStudentattendancelistPK(new StudentattendancelistPK());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            em.persist(studentattendancelist);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findStudentattendancelist(studentattendancelist.getStudentattendancelistPK()) != null) {
                throw new PreexistingEntityException("Studentattendancelist " + studentattendancelist + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Studentattendancelist studentattendancelist) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            studentattendancelist = em.merge(studentattendancelist);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                StudentattendancelistPK id = studentattendancelist.getStudentattendancelistPK();
                if (findStudentattendancelist(id) == null) {
                    throw new NonexistentEntityException("The studentattendancelist with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(StudentattendancelistPK id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Studentattendancelist studentattendancelist;
            try {
                studentattendancelist = em.getReference(Studentattendancelist.class, id);
                studentattendancelist.getStudentattendancelistPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The studentattendancelist with id " + id + " no longer exists.", enfe);
            }
            em.remove(studentattendancelist);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Studentattendancelist> findStudentattendancelistEntities() {
        return findStudentattendancelistEntities(true, -1, -1);
    }

    public List<Studentattendancelist> findStudentattendancelistEntities(int maxResults, int firstResult) {
        return findStudentattendancelistEntities(false, maxResults, firstResult);
    }

    private List<Studentattendancelist> findStudentattendancelistEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Studentattendancelist.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Studentattendancelist findStudentattendancelist(StudentattendancelistPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Studentattendancelist.class, id);
        } finally {
            em.close();
        }
    }

    public int getStudentattendancelistCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Studentattendancelist> rt = cq.from(Studentattendancelist.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
