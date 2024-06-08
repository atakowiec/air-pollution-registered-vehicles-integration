package pl.pollub.is.backend.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

public class EntityManagerCircularList {
    private Node current;

    private EntityManagerCircularList(Node head) {
        this.current = head;
    }

    public EntityManager next() {
        current = current.next;
        return current.entityManager;
    }

    public void forEach(Consumer<EntityManager> consumer) {
        Node start = current;
        do {
            consumer.accept(current.entityManager);
            current = current.next;
        } while (current != start);
    }

    public void rollbackAll() {
        forEach(em -> em.getTransaction().rollback());
    }

    public void beginAll() {
        forEach(em -> em.getTransaction().begin());
    }

    public void commitAll() {
        forEach(em -> {
            em.getTransaction().commit();
            em.clear();
        });
    }

    public static EntityManagerCircularList from(EntityManagerFactory factory, int capacity) {
        Node head = new Node(factory.createEntityManager());
        Node current = head;
        for (int i = 1; i < capacity; i++) {
            current.next = new Node(factory.createEntityManager());
            current = current.next;
        }
        current.next = head;
        return new EntityManagerCircularList(head);
    }

    @RequiredArgsConstructor
    @Getter
    private static class Node {
        private Node next;
        private final EntityManager entityManager;
    }
}
