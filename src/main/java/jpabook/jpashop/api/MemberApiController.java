package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController //JSON으로 값 리턴
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    //문제점 -> 엔티티의 모든 값이 노출, api별로 필요한 값들이 다르므로 모든 api를 충족시킬 수 없음
    @GetMapping("api/v1/members")
    public List<Member> memberV1() {
        List<Member> members = memberService.findMembers();
        return members;
    }

    //Result로 한번 감싸야한다 -> Object타입으로 반환해주고 데이터 필드 값은 List가 나간다
    //List를 바로 컬렉션으로 바로 return하면 JSON 배열타입으로 반환되어 유연성이 떨어진다
    @GetMapping("api/v2/members")
    public Result memberV2() {
        //Member List를 MemberDto List로 변환
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }


    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /**
     * 엔티티를 노출하거나 파라미터로 사용하지 말것 (대안) -> DTO 클래스를 하나 더 만들어라
     */
    //DTO 사용 -> api가 필요한 데이터가 무엇인지 파악하기 쉽다.
    //v1과 비교 했을때 v2의 장점 -> member 엔티티를 누군가 바꿔도 api 스펙이 바뀌지 않음 / 가운데서 파라미터랑 엔티티랑 매핑하므로
    //api 스펙에 필요한 valid를 설정할 수 있다. -> 엔티티를 변경하면 api마다 필요한 값이 달라 오류 발생
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable Long id,
                                               @RequestBody @Valid UpdateMemberRequest request) {
        //커맨드와 쿼리를 분리하자 -> 만약 member를 반환하면 update하면서 member를 쿼리하는 꼴이 되어버린다
        //update는 변경성 메서드인데 (커맨드) member를 통해서 찾게되는 (쿼리)가 동시에 일어남
        //만약 update 메서드에서 member를 반환하면 영속성이 끊긴 member를 사용하게되므로 void로 값만 바꿔주는 방법을 사용
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest {
        String name;
    }

    @Data
    @AllArgsConstructor //모든 파라미터를 넘기는 생성자 생성
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

}
