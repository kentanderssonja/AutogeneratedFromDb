/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject3;

import com.mycompany.mavenproject3.exceptions.NonexistentEntityException;
import com.mycompany.mavenproject3.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author User
 */
public class StudentJpaController implements Serializable {

    public StudentJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Student student) throws RollbackFailureException, Exception {
        if (student.getCourseCollection() == null) {
            student.setCourseCollection(new ArrayList<Course>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Course> attachedCourseCollection = new ArrayList<Course>();
            for (Course courseCollectionCourseToAttach : student.getCourseCollection()) {
                courseCollectionCourseToAttach = em.getReference(courseCollectionCourseToAttach.getClass(), courseCollectionCourseToAttach.getIdCourse());
                attachedCourseCollection.add(courseCollectionCourseToAttach);
            }
            student.setCourseCollection(attachedCourseCollection);
            em.persist(student);
            for (Course courseCollectionCourse : student.getCourseCollection()) {
                courseCollectionCourse.getStudentCollection().add(student);
                courseCollectionCourse = em.merge(courseCollectionCourse);
            }
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

    public void edit(Student student) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Student persistentStudent = em.find(Student.class, student.getIdStudent());
            Collection<Course> courseCollectionOld = persistentStudent.getCourseCollection();
            Collection<Course> courseCollectionNew = student.getCourseCollection();
            Collection<Course> attachedCourseCollectionNew = new ArrayList<Course>();
            for (Course courseCollectionNewCourseToAttach : courseCollectionNew) {
                courseCollectionNewCourseToAttach = em.getReference(courseCollectionNewCourseToAttach.getClass(), courseCollectionNewCourseToAttach.getIdCourse());
                attachedCourseCollectionNew.add(courseCollectionNewCourseToAttach);
            }
            courseCollectionNew = attachedCourseCollectionNew;
            student.setCourseCollection(courseCollectionNew);
            student = em.merge(student);
            for (Course courseCollectionOldCourse : courseCollectionOld) {
                if (!courseCollectionNew.contains(courseCollectionOldCourse)) {
                    courseCollectionOldCourse.getStudentCollection().remove(student);
                    courseCollectionOldCourse = em.merge(courseCollectionOldCourse);
                }
            }
            for (Course courseCollectionNewCourse : courseCollectionNew) {
                if (!courseCollectionOld.contains(courseCollectionNewCourse)) {
                    courseCollectionNewCourse.getStudentCollection().add(student);
                    courseCollectionNewCourse = em.merge(courseCollectionNewCourse);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = student.getIdStudent();
                if (findStudent(id) == null) {
                    throw new NonexistentEntityException("The student with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Student student;
            try {
                student = em.getReference(Student.class, id);
                student.getIdStudent();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The student with id " + id + " no longer exists.", enfe);
            }
            Collection<Course> courseCollection = student.getCourseCollection();
            for (Course courseCollectionCourse : courseCollection) {
                courseCollectionCourse.getStudentCollection().remove(student);
                courseCollectionCourse = em.merge(courseCollectionCourse);
            }
            em.remove(student);
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

    public List<Student> findStudentEntities() {
        return findStudentEntities(true, -1, -1);
    }

    public List<Student> findStudentEntities(int maxResults, int firstResult) {
        return findStudentEntities(false, maxResults, firstResult);
    }

    private List<Student> findStudentEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Student.class));
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

    public Student findStudent(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Student.class, id);
        } finally {
            em.close();
        }
    }

    public int getStudentCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Student> rt = cq.from(Student.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
