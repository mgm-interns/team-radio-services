package com.mgmtp.radio.service.conversation;

import com.mgmtp.radio.config.Constant;
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
import reactor.core.publisher.Mono;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MessageConfigTests.class)
public class CreateMessageTest {

    @Mock
    MessageRepository messageRepository;

    @Mock
    Constant constant;

    @Autowired
    @Qualifier("messageMapperImpl")
    MessageMapper messageMapper = MessageMapper.INSTANCE;

    FromUserMapper fromUserMapper = FromUserMapper.INSTANCE;

    MessageServiceImpl messageService;

    private UserHelper userHelper;

    @Before
    public void setUp() throws Exception {
        userHelper = new UserHelper();
        MockitoAnnotations.initMocks(this);
        messageService = new MessageServiceImpl(messageRepository, messageMapper, userHelper, constant);
    }

    @Test
    public void createSuccess() {
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

        Message savedMessage = new Message();
        savedMessage.setFrom(fromUser);
        savedMessage.setContent(messageDTO.getContent());
        savedMessage.setStationId(messageDTO.getStationId());

        when(messageRepository.save(any(Message.class))).thenReturn(Mono.just(savedMessage));
        // when
        Mono<MessageDTO> result = messageService.create(messageDTO.getStationId(), user, messageDTO);
        MessageDTO expected = result.log().block();

        // then
        assertEquals(messageDTO.getFrom(), expected.getFrom());
        assertEquals(messageDTO.getContent(), expected.getContent());
        assertEquals(messageDTO.getStationId(), expected.getStationId());
    }

}
