package com.LetMeDoWith.LetMeDoWith.infrastructure.auth.client;

import com.LetMeDoWith.LetMeDoWith.common.auth.client.AuthClient;
import com.LetMeDoWith.LetMeDoWith.common.auth.dto.OidcPublicKeyResDto;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Profile("dev")
@Component("dev_kakaoAuthClient")
public class DevKakaoAuthClient implements AuthClient {

    @Override
    public Mono<OidcPublicKeyResDto> getPublicKeyList() {
        return Mono.just(new OidcPublicKeyResDto(List.of(
                new OidcPublicKeyResDto.OidcPublicKeyVO(
                        "3f96980381e451efad0d2ddd30e3d3",
                        "RS256",
                        "sig",
                        "RSA",
                        "q8zZ0b_MNaLd6Ny8wd4cjFomilLfFIZcmhNSc1ttx_oQdJJZt5CDHB8WWwPGBUDUyY8AmfglS9Y1qA0_fxxs-ZUWdt45jSbUxghKNYgEwSutfM5sROh3srm5TiLW4YfOvKytGW1r9TQEdLe98ork8-rNRYPybRI3SKoqpci1m1QOcvUg4xEYRvbZIWku24DNMSeheytKUz6Ni4kKOVkzfGN11rUj1IrlRR-LNA9V9ZYmeoywy3k066rD5TaZHor5bM5gIzt1B4FmUuFITpXKGQZS5Hn_Ck8Bgc8kLWGAU8TzmOzLeROosqKE0eZJ4ESLMImTb2XSEZuN1wFyL0VtJw",
                        "AQAB"),
                new OidcPublicKeyResDto.OidcPublicKeyVO(
                        "9f252dadd5f233f93d2fa528d12fea",
                        "RS256",
                        "sig",
                        "RSA",
                        "qGWf6RVzV2pM8YqJ6by5exoixIlTvdXDfYj2v7E6xkoYmesAjp_1IYL7rzhpUYqIkWX0P4wOwAsg-Ud8PcMHggfwUNPOcqgSk1hAIHr63zSlG8xatQb17q9LrWny2HWkUVEU30PxxHsLcuzmfhbRx8kOrNfJEirIuqSyWF_OBHeEgBgYjydd_c8vPo7IiH-pijZn4ZouPsEg7wtdIX3-0ZcXXDbFkaDaqClfqmVCLNBhg3DKYDQOoyWXrpFKUXUFuk2FTCqWaQJ0GniO4p_ppkYIf4zhlwUYfXZEhm8cBo6H2EgukntDbTgnoha8kNunTPekxWTDhE5wGAt6YpT4Yw",
                        "AQAB"))));
    }
}
