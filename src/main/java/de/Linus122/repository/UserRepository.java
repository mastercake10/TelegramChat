package de.Linus122.repository;

import de.Linus122.TelegramChat.TelegramChat;
import de.Linus122.entity.User;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class UserRepository {

    private static UserRepository INSTANCE;


    public synchronized static UserRepository getInstance() {
        if (INSTANCE == null) {
            Thread.currentThread().setContextClassLoader(TelegramChat.getInstance().getClass().getClassLoader());
            final EntityManager entityManager = Persistence.createEntityManagerFactory("persistence-unit")
                    .createEntityManager();
            INSTANCE = new UserRepository(entityManager);
        }
        return INSTANCE;
    }

    private EntityManager em;

    public UserRepository(EntityManager em) {
        this.em = em;
    }

    public Optional<User> create(User person) {
        try {
            em.getTransaction().begin();
            em.persist(person);
            em.flush();
        } catch (EntityExistsException e) {
            em.getTransaction().rollback();
            return Optional.of(person);
        }
        em.getTransaction().commit();
        return Optional.of(person);

    }

    public List<User> findAllMsgOnUsersAmongOnlinePlayers(Set<String> onlinePlayers) {
        try {
            em.getTransaction().begin();
            return em.createQuery("select u from User u where u.playerId in (:ids) and u.chatEnabled = false")
                    .setParameter("ids", onlinePlayers)
                    .getResultList();
        } finally {
            em.getTransaction().commit();
        }
    }

    public Optional<User> readByChatId(Long id) {
        try {
            em.getTransaction().begin();
            User user = em.createQuery("select u from User u where u.chatId = :chatId", User.class)
                    .setParameter("chatId", id)
                    .getSingleResult();
            return Optional.ofNullable(user);
        } catch (NoResultException e) {
            // skip
            return Optional.empty();
        } finally {
            em.getTransaction().commit();
        }

    }

    public Optional<User> readByPlayerId(String playerId) {
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, playerId);
            return Optional.ofNullable(user);
        } finally {
            em.getTransaction().commit();
        }
    }

    public Optional<User> update(User person) {
        try {
            em.getTransaction().begin();
            person = em.merge(person);
            em.flush();
            return Optional.of(person);
        } finally {
            em.getTransaction().commit();
        }
    }
}
