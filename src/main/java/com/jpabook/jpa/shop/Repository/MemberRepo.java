package com.jpabook.jpa.shop.Repository;

import com.jpabook.jpa.shop.domain.Item.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class MemberRepo {
    // Reposotory로 접근하는 bean 제공
    @PersistenceContext
    private EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    // 재사용성 <oop>
    public Member findOne(Long id) {
        return em.find(Member.class, id);
        // id 변수에 담아 Member member로 클래스에 반환하는 코드
        // 단건 조회로 반환객체를 id로 설정
    }
    public List<Member> findAll() {
        // 결과를 담아서 보내기 위해 이용
        List<Member> result1 = em.createQuery("select m from Member m", Member.class).getResultList();
        return result1;
        // 반환타입으로 Member class에 접근한다
    }
    public List<Member> findByName(String name) {
        // repo공간에서 영속성을 기반한 조회기능
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
