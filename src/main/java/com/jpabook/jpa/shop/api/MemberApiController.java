package com.jpabook.jpa.shop.api;

import com.jpabook.jpa.shop.Service.MemberService;
import com.jpabook.jpa.shop.domain.Item.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.transform.Result;
import java.util.List;
import java.util.stream.Collectors;

@RestController // xml구조로 변홚하는 requestBody
@RequiredArgsConstructor
public class MemberApiController {
    private final MemberService memberService;

    // api통신으로 넘어오는 데이터 관리
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        // member에게 전달된 데이터는 json으로 구조됨
        Long id = memberService.join(member);
        return new CreateMemberResponse(id); // 제약조건이 없다면 구조 상 value는 가짐
    } // 생각해야 할 것은 화면에 보여지는 controller이므로 검증을 member에게 넘겨서는 안됨
    // 즉, entity에서 변경한다는 것은 이를 활용하는 모든 api 사용처가 유지보수를 행해야 하는 것

    // 위와는 다르게 DTO에게 검증을 따로 하고 v1을 class를 가지고 구조화하는 구현
    // 이러면 api에 변경이 존재해도 반응을 하고 직접 바꿔주기만 하면 ok
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    // put방법으로 {id} 형식으로 보내는 방법, 어떤 구조로 바뀌더라도 유지되는 형태이다 <put>
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest request) {
        // id와 name 넘어오고 갱신 함 => 전부 변경감지에 활용됨
        memberService.update(id, request.getName());
        // return new UpdateMemberResponse(); => AllArgument 를 이용 시 영속성에 보내는 객체가 요구됨
        // 해당 구조에서는 memberService에서 정의한 update에 따라 member를 객체로 해서 보낼수도 있다
        // 영속성에서 중요한 point!! = update query와 update command는 다른 것이다
        // 왜냐면 flush에 의해 조회 시 동일 이름에 조회는 반드시 id가 엇갈릴 수 있는 것
        // => id를 명시하는 getId()로 반환되는 위치를 알릴수 있음
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    // ddl-auto : none 에 따라 DB data들에 접근이 자유로워 짐 <연습용>
    // 조회기능 api로 여러 버전 확인
    // v1은 유지보수에 너무 불편함
    @GetMapping ("/api/v1/members")
    public List<Member> memberV1() {
        return memberService.findMembers(); // entity가 반환되면서 user 정보도 노출되버림
        // 예를 들면 order entity는 user정보를 조회하는 메서드가 정의되었음 => @JsonIgnore
        // 단순 구조에서 form을 나타내는 메서드도 구현되었다면 presentation layer에 접근한 것
        // order에만 정의된 것이 아니라 여러 곳에서도 반드시 정의될 것이라면 이 방식은 좋지 못함
        // order entity는 여러 곳에 @JsonIgnore로 정의됨에 의존을 받게 되고 양방향 의존관계를 맺음 => 객체지향에서 거부하는 구현

    }

    @GetMapping("/api/v2/members")
    public Result memberV2() {
        List<Member> findmembers = memberService.findMembers();

        // DTO로 구현
        List<MemberDto> collect = findmembers.stream().map(m -> new MemberDto(m.getName())).collect(Collectors.toList());
        return new Result(collect);
    }


    // test를 위해 일단 생성한 것

    @Data
    static class CreateMemberRequest {
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;
        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data; // 객체로 변환하는 Result
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }
}
