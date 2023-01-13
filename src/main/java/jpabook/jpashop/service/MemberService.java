package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service //컴포넌트 스캔 대상이됨 -> 스프링 빈으로 등록
@Transactional(readOnly = true) //읽기 전용 트랜잭션
@RequiredArgsConstructor
public class MemberService {

    //final로 하면 컴파일 시점에 체크가 가능
    private final MemberRepository memberRepository;

/*
    //생성자 주입 -> 생성할 때 완성되어 중간에 set으로 값을 바꿀 수 없다.
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    //Setter 주입 -> 테스트 코드 작성에서 mock을 작성해줄 수 있음
    @Autowired
    private void setMemberRepository(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
*/

    /**
     * 회원가입
     */
    @Transactional //쓰기 트랜잭션
    public Long join(Member member) {
        validateDuplicateMember(member); //중복 회원 검증
        memberRepository.save(member); //JPA에서 persist하면 영속성 컨텍스트에 Member를 올리는데 DB에 들어가기전에 Key값을 미리 생성해야하므로 id값이 있는게 보장
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }

        /*if (findMembers.size() > 0) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }*/
    }

    /**
     * 회원 조회
     */

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }
}
