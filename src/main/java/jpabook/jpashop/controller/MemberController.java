package jpabook.jpashop.controller;

import jpabook.jpashop.controller.Form.MemberForm;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {

        //입력 데이터가 유지됨 -> 에러가 있더라도 form 데이터를 그대로 가져가기 때문에 다시 뿌리게 된다.
        if(result.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping(value = "/members")
    public String List(Model model) {

        //DTO로 변환해서 화면에 꼭 필요한 데이터들만 출력하는게 좋다
        //API에서는 엔티티를 넘기면 안됨 -> 엔티티에 필드가 추가되면 API 스펙이 바뀌어버려서 모두 변경이 필요함
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);

        return "members/memberList";
    }
}
