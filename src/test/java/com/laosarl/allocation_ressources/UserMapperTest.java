package com.laosarl.allocation_ressources;

import com.laosarl.allocation_ressources.domain.User;
import com.laosarl.allocation_ressources.model.UserDTO;
import com.laosarl.allocation_ressources.service.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toUserDTO_ShouldMapFieldsCorrectly() {
        //Given
        User userToMap = User.builder().name("John").surname("Doe").email("Johndoe@gmail.com").build();

        //When
        UserDTO userMapped = userMapper.toUserDTO(userToMap);
        //Then
        assertNotNull(userMapped);
        assertThat(userToMap.getName()).isEqualTo(userMapped.getName());
        assertThat(userToMap.getSurname()).isEqualTo(userMapped.getSurname());
        assertThat(userToMap.getEmail()).isEqualTo(userMapped.getEmail());
    }
}
