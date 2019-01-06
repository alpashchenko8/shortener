package com.kontur.shortener.dao;

import com.kontur.shortener.model.Link;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public class LinkDaoImpl implements LinkDao {
    @PersistenceContext
    private EntityManager em ;
    @Override
    public Link save(Link link) {
        Link link2 = getByOriginalLink(link.getOriginalLink());
        if(link2==null){
            em.persist(link);
            recountRank(link);
            return link;
        }
        return link2;
    }

    @Override
    public Link getByShortLink(String shortLink) {
        Long id;
        try {
            id = Long.parseLong(shortLink, 36);
        }catch (NumberFormatException e) {
            return  null;
        }
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Link> criteria = builder.createQuery(Link.class);
        Root<Link> from = criteria.from(Link.class);
        criteria.select(from);
        criteria.where(builder.equal(from.get("id"), id ));
        TypedQuery<Link> typed = em.createQuery(criteria);
        Link link = null;
        try {
            link = typed.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return link;
    }

    @Override
    public Link getByOriginalLink(String originalLink) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Link> criteria = builder.createQuery(Link.class);
        Root<Link> from = criteria.from(Link.class);
        criteria.select(from);
        criteria.where(builder.equal(from.get("originalLink"), originalLink ));
        TypedQuery<Link> typed = em.createQuery(criteria);
        Link link = null;
        try {
            link = typed.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

        return link;
    }

    @Override
    public void countIncrement(Link link) {
        if(link==null) return;
        link.setCount(link.getCount() + 1);
        recountRank(link);

    }

    @Override
    public List<Link> list(int begin, int count) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Link> criteria = builder.createQuery(Link.class);
        Root<Link> from = criteria.from(Link.class);
        criteria.select(from);
        criteria.orderBy(builder.asc(from.get("rank")),builder.asc(from.get("id")));
        TypedQuery<Link> typed = em.createQuery(criteria);
        typed.setFirstResult(begin);
        typed.setMaxResults(count);
        List<Link> listLink = null;
        try {
            listLink = typed.getResultList();
        } catch (NoResultException e) {
            return null;
        }
        return listLink;
    }

    private long recountRank(Link link){
        int rank =  em.createQuery("select count (l) from " + Link.class.getSimpleName() +  " l WHERE l.count > "+link.getCount() +"")
                        .getFirstResult();
        link.setRank((long)(rank)+1L);
        return (long)rank;
    }
}
