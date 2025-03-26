package com.ajsolucoes.agendador_tarefas.business.mapper;


import com.ajsolucoes.agendador_tarefas.business.dto.TarefasDTO;
import com.ajsolucoes.agendador_tarefas.infrastructure.entity.TarefasEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TarefasConverter {

    TarefasEntity paraTarefasEntity(TarefasDTO dto);

    TarefasDTO paratarefasDTO(TarefasEntity entity);
}
