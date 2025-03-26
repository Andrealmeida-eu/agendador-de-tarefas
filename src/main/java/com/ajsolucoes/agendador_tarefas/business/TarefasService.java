package com.ajsolucoes.agendador_tarefas.business;

import com.ajsolucoes.agendador_tarefas.business.dto.TarefasDTO;
import com.ajsolucoes.agendador_tarefas.business.mapper.TarefaUpdateConverter;
import com.ajsolucoes.agendador_tarefas.business.mapper.TarefasConverter;
import com.ajsolucoes.agendador_tarefas.infrastructure.entity.TarefasEntity;
import com.ajsolucoes.agendador_tarefas.infrastructure.enums.StatusNotificacaoEnum;
import com.ajsolucoes.agendador_tarefas.infrastructure.exceptions.ResourceNotFoundException;
import com.ajsolucoes.agendador_tarefas.infrastructure.repository.TarefasRepository;
import com.ajsolucoes.agendador_tarefas.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TarefasService {

    private final TarefasRepository tarefasRepository;
    private final TarefasConverter tarefaConverter;
    private final JwtUtil jwtUtil;
    private final TarefaUpdateConverter tarefaUpdateConverter;

    public TarefasDTO gravarTarefa(String token, TarefasDTO dto) {
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        dto.setDataCriacao(LocalDateTime.now());
        dto.setStatusNotificacaoEnums(StatusNotificacaoEnum.PENDENTE);
        dto.setEmailUsuario(email);

        TarefasEntity entity = tarefaConverter.paraTarefasEntity(dto);

        return tarefaConverter.paratarefasDTO(
                tarefasRepository.save(entity));
    }

    public List<TarefasDTO> buscarTarefasAgendadasPorPeriodo(LocalDateTime dataInicial,
                                                             LocalDateTime dataFinal) {
        return tarefaConverter.paraListaTarefasDTO(
                tarefasRepository.findByDataEventoBetween(dataInicial, dataFinal));
    }

    public List<TarefasDTO> buscaTarefasPorEmail(String token) {
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        List<TarefasEntity> listaTarefas = tarefasRepository.findByEmailUsuario(email);

        return tarefaConverter.paraListaTarefasDTO(listaTarefas);
    }

    public void deletaPorId(String id) {
        try {
            tarefasRepository.deleteById(id);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Erro ao deletar tarefa por id" + id,
                    e.getCause());
        }

    }

    public TarefasDTO alteraStatus(StatusNotificacaoEnum status, String id) {

        try {
            TarefasEntity entity = tarefasRepository.findById(id).
                    orElseThrow(() -> new ResourceNotFoundException("Tarefa nao encontrada"));

            entity.setStatusNotificacaoEnums(status);
            return tarefaConverter.paratarefasDTO(tarefasRepository.save(entity));
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Erro ao alterar status da taref" +
                    e.getCause());
        }
    }

    public TarefasDTO updateTarefas(TarefasDTO dto, String id){
        try{
            TarefasEntity entity = tarefasRepository.findById(id).
                    orElseThrow(()-> new ResourceNotFoundException("Tarefa nao enncontrado"));
            tarefaUpdateConverter.updateTarefas(dto, entity);
            return tarefaConverter.paratarefasDTO(tarefasRepository.save(entity));
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Erro ao alterar tarefa" +
                    e.getCause());
        }
    }

}
