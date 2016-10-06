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
public class CourseJpaController implements Serializable {

    public CourseJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Course course) throws RollbackFailureException, Exception {
        if (course.getStudentCollection() == null) {
            course.setStudentCollection(new ArrayList<Student>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Teacher idTeacher = course.getIdTeacher();
            if (idTeacher != null) {
                idTeacher = em.getReference(idTeacher.getClass(), idTeacher.getIdTeacher());
                course.setIdTeacher(idTeacher);
            }
            Collection<Student> attachedStudentCollection = new ArrayList<Student>();
            for (Student studentCollectionStudentToAttach : course.getStudentCollection()) {
                studentCollectionStudentToAttach = em.getReference(studentCollectionStudentToAttach.getClass(), studentCollectionStudentToAttach.getIdStudent());
                attachedStudentCollection.add(studentCollectionStudentToAttach);
            }
            course.setStudentCollection(attachedStudentCollection);
            em.persist(course);
            if (idTeacher != null) {
                idTeacher.getCourseCollection().add(course);
                idTeacher = em.merge(idTeacher);
            }
            for (Student studentCollectionStudent : course.getStudentCollection()) {
                studentCollectionStudent.getCourseCollection().add(course);
                studentCollectionStudent = em.merge(studentCollectionStudent);
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

    public void edit(Course course) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Course persistentCourse = em.find(Course.class, course.getIdCourse());
            Teacher idTeacherOld = persistentCourse.getIdTeacher();
            Teacher idTeacherNew = course.getIdTeacher();
            Collection<Student> studentCollectionOld = persistentCourse.getStudentCollection();
            Collection<Student> studentCollectionNew = course.getStudentCollection();
            if (idTeacherNew != null) {
                idTeacherNew = em.getReference(idTeacherNew.getClass(), idTeacherNew.getIdTeacher());
                course.setIdTeacher(idTeacherNew);
            }
            Collection<Student> attachedStudentCollectionNew = new ArrayList<Student>();
            for (Student studentCollectionNewStudentToAttach : studentCollectionNew) {
                studentCollectionNewStudentToAttach = em.getReference(studentCollectionNewStudentToAttach.getClass(), studentCollectionNewStudentToAttach.getIdStudent());
                attachedStudentCollectionNew.add(studentCollectionNewStudentToAttach);
            }
            studentCollectionNew = attachedStudentCollectionNew;
            course.setStudentCollection(studentCollectionNew);
            course = em.merge(course);
            if (idTeacherOld != null && !idTeacherOld.equals(idTeacherNew)) {
                idTeacherOld.getCourseCollection().remove(course);
                idTeacherOld = em.merge(idTeacherOld);
            }
            if (idTeacherNew != null && !idTeacherNew.equals(idTeacherOld)) {
                idTeacherNew.getCourseCollection().add(course);
                idTeacherNew = em.merge(idTeacherNew);
            }
            for (Student studentCollectionOldStudent : studentCollectionOld) {
                if (!studentCollectionNew.contains(studentCollectionOldStudent)) {
                    studentCollectionOldStudent.getCourseCollection().remove(course);
                    studentCollectionOldStudent = em.merge(studentCollectionOldStudent);
                }
            }
            for (Student studentCollectionNewStudent : studentCollectionNew) {
                if (!studentCollectionOld.contains(studentCollectionNewStudent)) {
                    studentCollectionNewStudent.getCourseCollection().add(course);
                    studentCollectionNewStudent = em.merge(studentCollectionNewStudent);
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
                Integer id = course.getIdCourse();
                if (findCourse(id) == null) {
                    throw new NonexistentEntityException("The course with id " + id + " no longer exists.");
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
            Course course;
            try {
                course = em.getReference(Course.class, id);
                course.getIdCourse();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The course with id " + id + " no longer exists.", enfe);
            }
            Teacher idTeacher = course.getIdTeacher();
            if (idTeacher != null) {
                idTeacher.getCourseCollection().remove(course);
                idTeacher = em.merge(idTeacher);
            }
            Collection<Student> studentCollection = course.getStudentCollection();
            for (Student studentCollectionStudent : studentCollection) {
                studentCollectionStudent.getCourseCollection().remove(course);
                studentCollectionStudent = em.merge(studentCollectionStudent);
            }
            em.remove(course);
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

    public List<Course> findCourseEntities() {
        return findCourseEntities(true, -1, -1);
    }

    public List<Course> findCourseEntities(int maxResults, int firstResult) {
        return findCourseEntities(false, maxResults, firstResult);
    }

    private List<Course> findCourseEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Course.class));
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

    public Course findCourse(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Course.class, id);
        } finally {
            em.close();
        }
    }

    public int getCourseCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Course> rt = cq.from(Course.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
