package com.laosarl.allocation_ressources;


import com.laosarl.allocation_ressources.domain.Resource;
import com.laosarl.allocation_ressources.model.ResourceDTO;
import com.laosarl.allocation_ressources.model.UserDTO;
import com.laosarl.allocation_ressources.service.mapper.ResourceMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class ResourceMapperTest {

    private final ResourceMapper resourceMapper = Mappers.getMapper(ResourceMapper.class);

    @Test
    void toResourceDTO_ShouldMapFieldsCorrectly(){
        //Given
        Resource resourceToMap = Resource.builder().name("azert").type("azet").description("").isAvailable(true).build();

        //When
        ResourceDTO resourceMapped = resourceMapper.toResourceDTO(resourceToMap);
        //Then
        assertNotNull(resourceMapped);
        assertThat(resourceToMap.getName()).isEqualTo(resourceMapped.getName());
        assertThat(resourceToMap.getType()).isEqualTo(resourceMapped.getType());
        assertThat(resourceToMap.getDescription()).isEqualTo(resourceMapped.getDescription());
        assertThat(resourceToMap.getIsAvailable()).isEqualTo(resourceMapped.getIsAvailable());
    }

}
