package com.mgmtp.radio.service.reputation;

import com.mgmtp.radio.RadioApplicationTests;
import com.mgmtp.radio.domain.reputation.Reputation;
import com.mgmtp.radio.dto.reputation.ReputationDTO;
import com.mgmtp.radio.dto.user.UserDTO;
import com.mgmtp.radio.exception.RadioNotFoundException;
import com.mgmtp.radio.mapper.reputation.ReputationMapper;
import com.mgmtp.radio.respository.reputation.ReputationRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.publisher.Mono;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RadioApplicationTests.class)
public class ReputationImplTest {

    ReputationService reputationService;

    ReputationMapper reputationMapper = ReputationMapper.INSTANCE;

    @Mock
    ReputationRepository reputationRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        reputationService = new ReputationServiceImpl(reputationRepository, reputationMapper);
    }

    @Test(expected = Exception.class)
    public void getReputationWithUserNotFound() {
        String userId = "123";
        when(reputationRepository.findByUserId(anyString())).thenThrow(new RadioNotFoundException());
        reputationService.getReputation(userId);
    }

    @Test
    public void getReputationWithUserFound() {
        String userId = "123";
        Reputation reputation = new Reputation();
        reputation.setUserId("123456789");
        reputation.setScore(30);
        reputation.setUpdateAvatarAlready(true);

        ReputationDTO reputationDTO = new ReputationDTO();
        reputationDTO.setUserId("123456789");
        reputationDTO.setScore(30);
        reputationDTO.setUpdateAvatarAlready(true);

        when(reputationRepository.findByUserId(anyString())).thenReturn(Mono.just(reputation));
        Mono<ReputationDTO> expected = reputationService.getReputation(userId);
        ReputationDTO result = expected.log().block();
        assertEquals(reputationDTO.getUserId(), result.getUserId());
        assertEquals(reputationDTO.getScore(), result.getScore());
        assertEquals(reputationDTO.isUpdateAvatarAlready(), result.isUpdateAvatarAlready());
    }

}