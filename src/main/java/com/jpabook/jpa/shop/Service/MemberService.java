package com.jpabook.jpa.shop.Service;

import com.jpabook.jpa.shop.Repository.MemberRepo;
import com.jpabook.jpa.shop.domain.Item.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class MemberService {
    @Autowired
    private final MemberRepo memberRepo; // bean에 injection된다

//    @Autowired
//    public void setMemberRepo(MemberRepo memberRepo) {
//        this.memberRepo = memberRepo;
//        // setter injection, 현재는 사용하지 않는 방법
//    }
//    @Autowired
//    public MemberService(MemberRepo memberRepo) {
//        this.memberRepo = memberRepo;
//        // 생성자 injection, 아무리 변경되도 생성자이므로 큰 오류없이 넘길수 있다
//    }
// 모두 test case에서 누구의 것을 relate한지 변수를 놓치지 않고 확인하는 static void main()을 위해
// injection을 자동으로 지원하는 spring3, 대신 @autowired 대신 final로 명시함
// lombok의 allargsconstructor를 이용하면 생성자를 지원해줌, final만을 지원하는 requiredargsconstructor


    // 회원 가입 기능, 조회기능을 위해 transaction readonly가 아닌 부분만 별도 지정(우선권)
    @Transactional
    public Long join(Member member) {
        // 중복이 있을 경우를 조회한다면 Member는 영속성 상태이므로
        // pk인 member_id가 되면서 조회값이 value로서 id객체에 받아짐 <spring 영속성>
        validateDuplicateMember(member);
        memberRepo.save(member);
        return member.getId(); // id 객체로 반환
    }

    private void validateDuplicateMember(Member member) {
        // 중복이 없는 case라면 빠져나오기
        List<Member> findMembers = memberRepo.findByName(member.getName()) ;
        if (findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회 => 이 시점에서는 트랜젝션이 열려있는 상태다
    // 추가로 읽기전용을 부여함
    public List<Member> findMembers() {
        return memberRepo.findAll();
    }
    public Member findOne(Long memberId) {
        return memberRepo.findOne(memberId);
    }

    // api 요청에 맞게끔 로직을 구현
    // service 계층에서는 entity를 검증하는 구현으로 spec이 변경되면 확인할 곳
    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepo.findOne(id); // return 대신 영속성에 들어간 것
        member.setName(name); // 트랜잭션에 의해 DB에 flush하며 보냄
    }

}
