package com.jpabook.jpa.shop.Service;

import com.jpabook.jpa.shop.Repository.MemberRepo;
import com.jpabook.jpa.shop.domain.Item.Member;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
// import org.assertj.core.api.AssertionsForClassTypes;
// import static org.junit.jupiter.api.Assertions.;

// @RunWith(SpringRunner.class) -> junit4에서 지원
@ExtendWith(SpringExtension.class) // junit5
@SpringBootTest
@Transactional
// 회원가입과 중복회원확인 test
public class MemberServiceTest {

    // private를 불러오는 autowired
    @Autowired MemberService memberService;
    @Autowired MemberRepo memberRepo;
    @Autowired EntityManager em;

    @Test
    @Rollback(false)
    public void userOpen() throws Exception {
        // given
        Member member = new Member();
        member.setName("baek");
        //when
        // em.flush()
        Long saveId = memberService.join(member);
        // then
        assertEquals(member, memberRepo.findOne(saveId)); // 같은 영속성 안에서 pk를 공유
    }

    // @Test(expected=IllegalStateException.class), 줄일수 있음
    @Test
    public void doubleChance() throws Exception {
        // given
        Member member1 = new Member();
        member1.setName("baek");

        Member member2 = new Member();
        member2.setName("baek"); // 중복인 경우이니 try+catch
        // when
        memberService.join(member1);
        try {
            memberService.join(member2); // 예외로 처리되지 않는 것은 class문제
        } catch (IllegalStateException e) {
            return;
        }
        // then
        fail("예외가 발생됐습니다");
    }
}