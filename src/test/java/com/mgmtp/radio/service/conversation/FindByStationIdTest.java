package com.mgmtp.radio.service.conversation;

import com.mgmtp.radio.config.MessageConfigTests;
import com.mgmtp.radio.domain.conversation.FromUser;
import com.mgmtp.radio.domain.conversation.Message;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.conversation.FromUserDTO;
import com.mgmtp.radio.dto.conversation.MessageDTO;
import com.mgmtp.radio.mapper.conversation.FromUserMapper;
import com.mgmtp.radio.mapper.conversation.MessageMapper;
import com.mgmtp.radio.respository.conversation.MessageRepository;
import com.mgmtp.radio.support.UserHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.publisher.Flux;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MessageConfigTests.class)
public class FindByStationIdTest {

    @Mock
    MessageRepository messageRepository;

    @Autowired
    @Qualifier("messageMapperImpl")
    MessageMapper messageMapper = MessageMapper.INSTANCE;

    FromUserMapper fromUserMapper = FromUserMapper.INSTANCE;

    MessageService messageService;

    private UserHelper userHelper;

    @Before
    public void setUp() throws Exception {
        userHelper = new UserHelper();
        MockitoAnnotations.initMocks(this);
        messageService = new MessageServiceImpl(messageRepository, messageMapper, userHelper);
    }

    @Test
    public void getByStationIdSuccess() {
        // given
        User user = new User();
        user.setId("001");
        user.setUsername("John Doe");
        user.setAvatarUrl("http://image.com/avatar");
        FromUser fromUser = userHelper.convertUserToFromUser(user);
        FromUserDTO fromUserDTO = fromUserMapper.fromUserToFromUserDTO(fromUser);

        MessageDTO messageDTO = new MessageDTO();
        fromUserDTO.setAvatarUrl(user.getAvatarUrl());
        messageDTO.setFrom(fromUserDTO);
        messageDTO.setContent("hello world");
        messageDTO.setStationId("S001");

        Message message = new Message();
        message.setFrom(fromUser);
        message.setContent(messageDTO.getContent());
        message.setStationId(messageDTO.getStationId());

        when(messageRepository.findByStationId(anyString())).thenReturn(Flux.just(message));

        // when
        Flux<MessageDTO> result = messageService.findByStationId(messageDTO.getStationId());
        MessageDTO expected = result.log().next().block();

        // then
        assertEquals(messageDTO, expected);
    }

    @Test
    public void getEmptyMessageListFromStationId() {
        // given
        User user = new User();
        user.setId("001");
        user.setUsername("John Doe");
        user.setAvatarUrl("http://image.com/avatar");
        FromUser fromUser = userHelper.convertUserToFromUser(user);
        FromUserDTO fromUserDTO = fromUserMapper.fromUserToFromUserDTO(fromUser);

        MessageDTO messageDTO = new MessageDTO();
        fromUserDTO.setAvatarUrl(user.getAvatarUrl());
        messageDTO.setFrom(fromUserDTO);
        messageDTO.setContent("hello world");
        messageDTO.setStationId("S001");

        when(messageRepository.findByStationId(anyString())).thenReturn(Flux.empty());

        // when
        Flux<MessageDTO> result = messageService.findByStationId(messageDTO.getStationId());

        // then
        assertNull(result.log().next().block());
    }
}
