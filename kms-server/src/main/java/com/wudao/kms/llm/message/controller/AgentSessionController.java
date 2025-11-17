package com.wudao.kms.llm.message.controller;

import cn.hutool.core.lang.id.NanoId;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wudao.common.model.vo.PageDomain;
import com.wudao.common.model.vo.R;
import com.wudao.kms.llm.message.domain.AgentSession;
import com.wudao.kms.llm.message.service.AgentSessionService;
import com.wudao.security.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/agent/session")
@RequiredArgsConstructor
@RestController
@Tag(name = "agent会话管理")
public class AgentSessionController {
    private final AgentSessionService agentSessionService;

    @GetMapping("/queryList")
    public PageDomain<AgentSession> queryList(@ParameterObject String sessionName,
                                              @ParameterObject String agentUuid,
                                              @ParameterObject PageDomain<AgentSession> page) {
        return  agentSessionService.queryList(sessionName, agentUuid, page);
    }

    @PostMapping
    public R<String> add(@RequestBody AgentSession agentSession){
        agentSession.setUuid(NanoId.randomNanoId(10));
        agentSessionService.save(agentSession);
        return R.ok(agentSession.getUuid());
    }

    @PutMapping
    public R<Void>  update(@RequestBody AgentSession agentSession){
        agentSessionService.updateById(agentSession);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id){
        agentSessionService.removeById(id);
        return R.ok();
    }

    @Tag(name = "清空对话", description = "清空调用者的所有对话")
    @DeleteMapping("/clean")
    public R<Void> clean(){
        agentSessionService.remove(Wrappers.lambdaQuery(AgentSession.class).eq(AgentSession::getCreatedBy, SecurityUtils.getUserId()));
        return R.ok();
    }
}
