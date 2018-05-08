package com.mgmtp.radio.service.conversation;

import com.mgmtp.radio.config.Constant;
import com.mgmtp.radio.config.MessageConfigTests;
import com.mgmtp.radio.domain.conversation.Sender;
import com.mgmtp.radio.domain.conversation.Message;
import com.mgmtp.radio.domain.user.User;
import com.mgmtp.radio.dto.conversation.SenderDTO;
import com.mgmtp.radio.dto.conversation.MessageDTO;
import com.mgmtp.radio.mapper.conversation.SenderMapper;
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
public class FindBySenderUserIdTest {

    @Mock
    MessageRepository messageRepository;

    @Mock
    Constant constant;

    @Autowired
    @Qualifier("messageMapperImpl")
    MessageMapper messageMapper = MessageMapper.INSTANCE;

    SenderMapper senderMapper = SenderMapper.INSTANCE;

    MessageService messageService;

    private UserHelper userHelper;

    @Before
    public void setUp() throws Exception {
        userHelper = new UserHelper();
        MockitoAnnotations.initMocks(this);
        messageService = new MessageServiceImpl(messageRepository, messageMapper, userHelper, constant);
    }

    @Test
    public void getBySenderUserIdSuccess() {
        // given
        User user = new User();
        user.setId("001");
        user.setName("John Doe");
        user.setAvatarUrl("http://image.com/avatar");
        Sender sender = userHelper.convertUserToSender(user);
        SenderDTO senderDTO = senderMapper.senderToSenderDTO(sender);

        MessageDTO messageDTO = new MessageDTO();
        senderDTO.setAvatarUrl(user.getAvatarUrl());
        messageDTO.setSender(senderDTO);
        messageDTO.setContent("hello world");
        messageDTO.setStationId("S001");

        Message message = new Message();
        message.setSender(sender);
        message.setContent(messageDTO.getContent());
        message.setStationId(messageDTO.getStationId());

        when(messageRepository.findBySender_UserId(anyString())).thenReturn(Flux.just(message));

        // when
        Flux<MessageDTO> result = messageService.findBySenderUserId(messageDTO.getSender().getUserId());
        MessageDTO expected = result.log().next().block();

        // then
        assertEquals(messageDTO, expected);
    }

    @Test
    public void getEmptyMessageListFromUserId() {
        // given
        User user = new User();
        user.setId("001");
        user.setName("John Doe");
        user.setAvatarUrl("http://image.com/avatar");
        Sender sender = userHelper.convertUserToSender(user);
        SenderDTO senderDTO = senderMapper.senderToSenderDTO(sender);

        MessageDTO messageDTO = new MessageDTO();
        senderDTO.setAvatarUrl(user.getAvatarUrl());
        messageDTO.setSender(senderDTO);
        messageDTO.setContent("hello world");
        messageDTO.setStationId("S001");

        when(messageRepository.findBySender_UserId(anyString())).thenReturn(Flux.empty());

        // when
        Flux<MessageDTO> result = messageService.findBySenderUserId(messageDTO.getSender().getUserId());

        // then
        assertNull(result.log().next().block());
    }
}
