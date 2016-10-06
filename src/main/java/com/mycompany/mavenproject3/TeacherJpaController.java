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
public class TeacherJpaController implements Serializable {

    public TeacherJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Teacher teacher) throws RollbackFailureException, Exception {
        if (teacher.getCourseCollection() == null) {
            teacher.setCourseCollection(new ArrayList<Course>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Course> attachedCourseCollection = new ArrayList<Course>();
            for (Course courseCollectionCourseToAttach : teacher.getCourseCollection()) {
                courseCollectionCourseToAttach = em.getReference(courseCollectionCourseToAttach.getClass(), courseCollectionCourseToAttach.getIdCourse());
                attachedCourseCollection.add(courseCollectionCourseToAttach);
            }
            teacher.setCourseCollection(attachedCourseCollection);
            em.persist(teacher);
            for (Course courseCollectionCourse : teacher.getCourseCollection()) {
                Teacher oldIdTeacherOfCourseCollectionCourse = courseCollectionCourse.getIdTeacher();
                courseCollectionCourse.setIdTeacher(teacher);
                courseCollectionCourse = em.merge(courseCollectionCourse);
                if (oldIdTeacherOfCourseCollectionCourse != null) {
                    oldIdTeacherOfCourseCollectionCourse.getCourseCollection().remove(courseCollectionCourse);
                    oldIdTeacherOfCourseCollectionCourse = em.merge(oldIdTeacherOfCourseCollectionCourse);
                }
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

    public void edit(Teacher teacher) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Teacher persistentTeacher = em.find(Teacher.class, teacher.getIdTeacher());
            Collection<Course> courseCollectionOld = persistentTeacher.getCourseCollection();
            Collection<Course> courseCollectionNew = teacher.getCourseCollection();
            Collection<Course> attachedCourseCollectionNew = new ArrayList<Course>();
            for (Course courseCollectionNewCourseToAttach : courseCollectionNew) {
                courseCollectionNewCourseToAttach = em.getReference(courseCollectionNewCourseToAttach.getClass(), courseCollectionNewCourseToAttach.getIdCourse());
                attachedCourseCollectionNew.add(courseCollectionNewCourseToAttach);
            }
            courseCollectionNew = attachedCourseCollectionNew;
            teacher.setCourseCollection(courseCollectionNew);
            teacher = em.merge(teacher);
            for (Course courseCollectionOldCourse : courseCollectionOld) {
                if (!courseCollectionNew.contains(courseCollectionOldCourse)) {
                    courseCollectionOldCourse.setIdTeacher(null);
                    courseCollectionOldCourse = em.merge(courseCollectionOldCourse);
                }
            }
            for (Course courseCollectionNewCourse : courseCollectionNew) {
                if (!courseCollectionOld.contains(courseCollectionNewCourse)) {
                    Teacher oldIdTeacherOfCourseCollectionNewCourse = courseCollectionNewCourse.getIdTeacher();
                    courseCollectionNewCourse.setIdTeacher(teacher);
                    courseCollectionNewCourse = em.merge(courseCollectionNewCourse);
                    if (oldIdTeacherOfCourseCollectionNewCourse != null && !oldIdTeacherOfCourseCollectionNewCourse.equals(teacher)) {
                        oldIdTeacherOfCourseCollectionNewCourse.getCourseCollection().remove(courseCollectionNewCourse);
                        oldIdTeacherOfCourseCollectionNewCourse = em.merge(oldIdTeacherOfCourseCollectionNewCourse);
                    }
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
                Integer id = teacher.getIdTeacher();
                if (findTeacher(id) == null) {
                    throw new NonexistentEntityException("The teacher with id " + id + " no longer exists.");
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
            Teacher teacher;
            try {
                teacher = em.getReference(Teacher.class, id);
                teacher.getIdTeacher();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The teacher with id " + id + " no longer exists.", enfe);
            }
            Collection<Course> courseCollection = teacher.getCourseCollection();
            for (Course courseCollectionCourse : courseCollection) {
                courseCollectionCourse.setIdTeacher(null);
                courseCollectionCourse = em.merge(courseCollectionCourse);
            }
            em.remove(teacher);
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

    public List<Teacher> findTeacherEntities() {
        return findTeacherEntities(true, -1, -1);
    }

    public List<Teacher> findTeacherEntities(int maxResults, int firstResult) {
        return findTeacherEntities(false, maxResults, firstResult);
    }

    private List<Teacher> findTeacherEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Teacher.class));
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

    public Teacher findTeacher(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Teacher.class, id);
        } finally {
            em.close();
        }
    }

    public int getTeacherCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Teacher> rt = cq.from(Teacher.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
