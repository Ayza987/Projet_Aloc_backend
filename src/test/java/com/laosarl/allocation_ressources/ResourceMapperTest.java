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

    @Test
    void shouldUpdateResourceFromDto() {
        Resource resource = new Resource();
        resource.setName("Ancien Nom");
        resource.setType("Ancien Type");
        resource.setDescription("Ancienne Description");
        resource.setIsAvailable(true);

        ResourceDTO dto = new ResourceDTO();
        dto.setName("Nouveau Nom");
        dto.setType(null);
        dto.setDescription("Nouvelle Description");

        resourceMapper.updateResourceFromDto(dto, resource);

        assertThat(resource.getName()).isEqualTo("Nouveau Nom");
        assertThat(resource.getType()).isEqualTo("Ancien Type");
        assertThat(resource.getDescription()).isEqualTo("Nouvelle Description");
        assertThat(resource.getIsAvailable()).isTrue();
    }

}
