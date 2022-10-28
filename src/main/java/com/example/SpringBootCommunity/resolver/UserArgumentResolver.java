package com.example.SpringBootCommunity.resolver;

import com.example.SpringBootCommunity.annotation.SocialUser;
import com.example.SpringBootCommunity.domain.User;
import com.example.SpringBootCommunity.domain.enums.SocialType;
import com.example.SpringBootCommunity.repository.UserRepository;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.example.SpringBootCommunity.domain.enums.SocialType.*;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private UserRepository userRepository;

    public UserArgumentResolver(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public boolean supportsParameter(MethodParameter parameter){
        return parameter.getParameterAnnotation(SocialUser.class) != null &&
                parameter.getParameterType().equals(User.class);
    }

    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
        User user = (User) session.getAttribute("user");

        return getUser(user, session);
    }

    private User getUser(User user, HttpSession session){
        if(user==null){
            try{
                OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
                Map<String, String> map = (HashMap<String, String>) authentication.getUserAuthentication().getDetails();
                User convertUser = convertUser(String.valueOf(authentication.getAuthorities().toArray()[0]), map);

                user = userRepository.findByEmail(convertUser.getEmail());
                if(user==null){
                    user=userRepository.save(convertUser);
                }

                setRoleIfNotSame(user, authentication, map);
                session.setAttribute("user", user);
            }catch(ClassCastException e){
                return user;
            }
        }
        return user;
    }

    //소셜 미디어 타입에 따라 빌더 사용, 카카오는 별도 사용
    private User convertUser(String authority, Map<String, String> map){
        if(FACEBOOK.isEquals(authority)) return getMordernUser(FACEBOOK, map);
        else if(GOOGLE.isEquals(authority)) return getMordernUser(GOOGLE, map);
        else if(KAKAO.isEquals(authority)) return getKakaoUser(map);
        return null;
    }

    //페이스북, 구글과 같이 공통 명명규칙 매핑
    private User getMordernUser(SocialType socialType, Map<String, String> map){
        return User.builder()
                .name(map.get("name"))
                .email(map.get("email"))
                .principal(map.get("id"))
                .socialType(socialType)
                .createdDate(LocalDateTime.now())
                .build();
    }

    //카카오 명명규칭 매핑
    private User getKakaoUser(Map<String, String> map){
        HashMap<String, String> propertyMap = (HashMap<String, String>) (Object) map.get("properties");

        return User.builder()
                .name(propertyMap.get("name"))
                .email(map.get("kaccount_email"))
                .principal(String.valueOf(map.get("id")))
                .socialType(KAKAO)
                .createdDate(LocalDateTime.now())
                .build();
    }

    //권한 체크
    private void setRoleIfNotSame(User user, OAuth2Authentication authentication, Map<String, String> map){
        if(!authentication.getAuthorities().contains(new SimpleGrantedAuthority(user.getSocialType().getRoleType()))){
            SecurityContextHolder.getContext().setAuthentication(new
                    UsernamePasswordAuthenticationToken(map, "N/A",
                    AuthorityUtils.createAuthorityList(user.getSocialType().getRoleType())));
        }
    }
}
